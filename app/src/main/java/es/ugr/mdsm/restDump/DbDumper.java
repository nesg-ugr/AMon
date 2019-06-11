package es.ugr.mdsm.restDump;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.ugr.mdsm.deviceInfo.Probes;
import es.ugr.mdsm.deviceInfo.Software;
import eu.faircode.netguard.DatabaseHelper;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DbDumper {
    private static final String TAG = "MDSM.DbDumper";
    private static final int MINIMAL_AMOUNT_FLOWS = 10;

    private long mInterval;   // in ms
    private Context mContext;
    private Handler restHandler;
    private Runnable periodicUpdate;
    private Runnable asyncUpdate;
    private Runnable devicePush;
    private Runnable appPush;
    private Runnable sensorPush;
    private Api api;
    private DatabaseHelper dh;
    private boolean started;

    public DbDumper(int interval, Context context){
        mInterval = interval;
        mContext = context;
        restHandler = new Handler();
        periodicUpdate = new Runnable() {
            @Override
            public void run() {
                flowDump();
                restHandler.postDelayed(this, mInterval);
            }
        };
        asyncUpdate = new Runnable() {
            @Override
            public void run() {
                flowDump();
            }
        };
        devicePush = new Runnable() {
            @Override
            public void run() {
                deviceDump();
            }
        };
        appPush = new Runnable() {
            @Override
            public void run() {
                appDump();
            }
        };
        sensorPush = new Runnable() {
            @Override
            public void run() {
                sensorDump();
            }
        };
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        api = retrofit.create(Api.class);
        dh = DatabaseHelper.getInstance(mContext);
    }

    public void start(){
        restHandler.post(periodicUpdate);
    }

    public void async(){
        restHandler.post(asyncUpdate);
    }

    public void dumpDeviceInfo(){
        restHandler.post(devicePush);
    }

    public void dumpAppInfo(){
        restHandler.post(appPush);
    }

    public void dumpSensorInfo(){
        restHandler.post(sensorPush);
    }
    public void stop(){
        restHandler.removeCallbacks(periodicUpdate);
        restHandler.removeCallbacks(asyncUpdate);
    }

    private void flowDump(){
        final long now = Calendar.getInstance().getTimeInMillis();
        List<Flow_> flows = new ArrayList<>();
        Flow flow;
        Gson gson = new Gson();

        // Read flows since now
        try (Cursor cursor = dh.getFlow(now)){
            if(cursor.getCount() <= 0){
                Log.d(TAG, "There's no flow entry to send");
                return;
            }else if(cursor.getCount() <= MINIMAL_AMOUNT_FLOWS){
                Log.d(TAG, "Not enough flows");
                return;
            }else{
                while (cursor.moveToNext()){
                    flows.add(new Flow_(
                            cursor.getString(cursor.getColumnIndex("packageName")),
                            cursor.getLong(cursor.getColumnIndex("time")),
                            cursor.getLong(cursor.getColumnIndex("duration")),
                            cursor.getInt(cursor.getColumnIndex("protocol")),
                            cursor.getString(cursor.getColumnIndex("saddr")),
                            cursor.getInt(cursor.getColumnIndex("sport")),
                            cursor.getString(cursor.getColumnIndex("daddr")),
                            cursor.getInt(cursor.getColumnIndex("dport")),
                            cursor.getLong(cursor.getColumnIndex("sent")),
                            cursor.getLong(cursor.getColumnIndex("received")),
                            cursor.getInt(cursor.getColumnIndex("sentPackets")),
                            cursor.getInt(cursor.getColumnIndex("receivedPackets")),
                            cursor.getInt(cursor.getColumnIndex("tcpFlags")),
                            cursor.getInt(cursor.getColumnIndex("ToS")),
                            cursor.getInt(cursor.getColumnIndex("NewFlow"))!=0,
                            cursor.getInt(cursor.getColumnIndex("Finished"))!=0
                            ));
                }
            }
        }

        flow = new Flow(getFormattedMac(), flows);

        Log.d(TAG, gson.toJson(flow));

        // Post data
        api.postFlows(flow)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<Void>>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response response) {
                        if(response.isSuccessful()){
                            Log.i(TAG, "Successful flows POST");
                            // Remove everything
                            dh.safeCleanupFlow(now);
                            // Remove only finished flows
                            // dh.cleanupFinishedFlow(now);
                        }else{
                            Log.w(TAG, "Failed to POST flows");
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"Error in flows POST",e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void deviceDump(){

        Device device = new Device(
                getFormattedMac(),
                new Build(
                        android.os.Build.MANUFACTURER,
                        android.os.Build.BRAND,
                        android.os.Build.MODEL,
                        android.os.Build.BOARD,
                        android.os.Build.HARDWARE,
                        android.os.Build.BOOTLOADER,
                        android.os.Build.USER,
                        android.os.Build.HOST,
                        android.os.Build.VERSION.SDK_INT,
                        android.os.Build.ID,
                        android.os.Build.TIME,
                        android.os.Build.FINGERPRINT
                ),
                new Specification(Probes.numCpuCores(), Probes.ramTotal(mContext), Probes.batteryTotal(mContext)),
                getTimeStamp()
        );

        Log.d(TAG, device.toString());

        // Post data
        api.postDevice(device)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response response) {
                        if(response.isSuccessful()){
                            Log.i(TAG, "Successful device push");
                        }else{
                            Log.w(TAG, "Failed to POST device");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"Error in device POST",e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void appDump(){

        List<App_> app_list = new ArrayList<>();
        for (ApplicationInfo applicationInfo :
                Software.getInstalledApplication(mContext)) {
            app_list.add(new App_(
                    Util.anonymizeApp(mContext, applicationInfo.packageName),
                    Util.bitSetToBase64(Software.permissionsAsBitArray(Software.permissionsOfApp(mContext, applicationInfo))),
                    Software.versionOfApp(mContext,applicationInfo),
                    Software.isAutoStarted(mContext, applicationInfo)
            ));
        }
        App app = new App(
                app_list,
                getFormattedMac(),
                getTimeStamp()
        );

        Log.d(TAG, app.toString());

        // Post app
        api.postApp(app)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response response) {
                        if(response.isSuccessful()){
                            Log.i(TAG, "Successful app push");
                        }else{
                            Log.w(TAG, "Failed to POST app");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"Error in app POST",e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void sensorDump(){

        Sensor sensor = new Sensor(
                new Connectivity(
                        Probes.isMobileDataEnabled(mContext),
                        Probes.isWifiEnabled(mContext),
                        Probes.isAirplaneModeEnabled(mContext),
                        Probes.isBluetoothEnabled(),
                        Probes.isGpsEnabled(mContext)
                ),
                new Stat(
                        Probes.cpuUsage(),
                        Probes.ramUsage(mContext),
                        Probes.batteryLevel(mContext)
                ),
                new Security(
                        Software.isUnknownSourcesEnabled(mContext),
                        Software.isDeveloperOptionsEnabled(mContext),
                        Software.isDeviceSecure(mContext),
                        Software.isRooted(mContext)
                ),
                getFormattedMac(),
                getTimeStamp()
        );

        Log.d(TAG, sensor.toString());

        // Post sensor
        api.postSensor(sensor)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response response) {
                        if(response.isSuccessful()){
                            Log.i(TAG, "Successful sensor push");
                        }else{
                            Log.w(TAG, "Failed to POST sensor");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"Error in sensor POST",e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private Long getTimeStamp(){
        return System.currentTimeMillis();
    }

    private String getFormattedMac(){
        return eu.faircode.netguard.Util.getMacAddress().replace(":","");
    }


}

