package es.ugr.mdsm.restDump;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import es.ugr.mdsm.application.Info;
import es.ugr.mdsm.application.Permission;
import es.ugr.mdsm.connectivity.AirplaneMode;
import es.ugr.mdsm.connectivity.Bluetooth;
import es.ugr.mdsm.connectivity.Location;
import es.ugr.mdsm.connectivity.MobileData;
import es.ugr.mdsm.ecosystem.Configuration;
import es.ugr.mdsm.hardware.PhysicalAccess;
import es.ugr.mdsm.hardware.Usage;
import eu.faircode.netguard.ServiceSinkhole;
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
    private static final int MINIMAL_AMOUNT_FLOWS = 20;
    public static final long DEFAULT_INTERVAL = 60*1000; // in ms

    private Context mContext;
    private HandlerThread handlerThread;
    private Looper looper;
    private Handler restHandler;
    private Api api;
    // private DatabaseHelper dh;

    private Runnable flowPush;
    private Runnable flowPushPeriodic;
    private Runnable devicePush;
    private Runnable devicePushPeriodic;
    private Runnable appPush;
    private Runnable appPushPeriodic;
    private Runnable sensorPush;
    private Runnable sensorPushPeriodic;
    private Runnable connectionPush;
    private Runnable getConnectionPushPeriodic;


    public DbDumper(Context context){
        mContext = context;
        handlerThread = new HandlerThread("DB Dumping", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        looper = handlerThread.getLooper();
        restHandler = new Handler(looper);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        api = retrofit.create(Api.class);
        // dh = DatabaseHelper.getInstance(mContext);


    }

    // Start a periodic dump of some tasks with a default interval
    public void start(){
        start(DEFAULT_INTERVAL);
    }

    public void start(long interval){
        dumpFlowInfo(interval);
        dumpSensorInfo(interval);
    }

    // Stop all pending callbacks and messages
    public void stop(){
        restHandler.removeCallbacksAndMessages(null);
    }

    public void onDestroy(){
        looper.quit();
    }

    // Async call to flowPush
    public void dumpFlowInfo(){
        flowPush = new Runnable() {
            @Override
            public void run() {
                flowDump();
            }
        };
        restHandler.post(flowPush);
    }

    // Periodic call to flowPush
    public void dumpFlowInfo(final long interval){
        restHandler.removeCallbacks(flowPushPeriodic);
        flowPushPeriodic = new Runnable() {
            @Override
            public void run() {
                flowDump();
                restHandler.postDelayed(this, interval);
            }
        };
        restHandler.post(flowPushPeriodic);
    }

    // Async call to devicePush
    public void dumpDeviceInfo(){
        devicePush = new Runnable() {
            @Override
            public void run() {
                deviceDump();
            }
        };
        restHandler.post(devicePush);
    }

    // Periodic call to flowPush
    public void dumpDeviceInfo(final long interval){
        restHandler.removeCallbacks(devicePushPeriodic);
        devicePushPeriodic = new Runnable() {
            @Override
            public void run() {
                deviceDump();
                restHandler.postDelayed(this, interval);
            }
        };
        restHandler.post(devicePushPeriodic);
    }

    // Async call to appPush
    public void dumpAppInfo(){
        appPush = new Runnable() {
            @Override
            public void run() {
                appDump();
            }
        };
        restHandler.post(appPush);
    }

    // Periodic call to appPush
    public void dumpAppInfo(final long interval){
        restHandler.removeCallbacks(appPushPeriodic);
        appPushPeriodic = new Runnable() {
            @Override
            public void run() {
                appDump();
                restHandler.postDelayed(this, interval);
            }
        };
        restHandler.post(appPushPeriodic);
    }

    // Async call to sensorPush
    public void dumpSensorInfo(){
        sensorPush = new Runnable() {
            @Override
            public void run() {
                sensorDump();
            }
        };
        restHandler.post(sensorPush);
    }

    public void dumpSensorInfo(final long interval){
        restHandler.removeCallbacks(sensorPushPeriodic);
        sensorPushPeriodic = new Runnable() {
            @Override
            public void run() {
                sensorDump();
                restHandler.postDelayed(this, interval);
            }
        };
        restHandler.post(sensorPushPeriodic);
    }

    public void dumpConnectionInfo(){
        connectionPush = new Runnable() {
            @Override
            public void run() {
                connectionDump();
            }
        };
        restHandler.post(connectionPush);
    }

    public void dumpConnectionInfo(final long interval){
        restHandler.removeCallbacks(getConnectionPushPeriodic);
        getConnectionPushPeriodic = new Runnable() {
            @Override
            public void run() {
                connectionDump();
                restHandler.postDelayed(this, interval);
            }
        };
        restHandler.post(getConnectionPushPeriodic);
    }

    public void flowDump(){
        final long now = Calendar.getInstance().getTimeInMillis();
        List<Flow_> flows = new ArrayList<>();
        Flow flow;
        Gson gson = new Gson();

        /*
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
        */
        final LinkedBlockingQueue<eu.faircode.netguard.Flow> flowQueue = ServiceSinkhole.getQueue();
        final ArrayList<eu.faircode.netguard.Flow> tempFlows = new ArrayList<>();
        if (flowQueue.isEmpty()){
            Log.d(TAG, "There's no flow entry to send");
            return;
        } else if(flowQueue.size()<= MINIMAL_AMOUNT_FLOWS){
            Log.d(TAG, "Not enough flows");
            return;
        }else {
            Log.d(TAG, "Dumping "+flowQueue.size()+" flows");
            flowQueue.drainTo(tempFlows);
        }

        for (eu.faircode.netguard.Flow f:
             tempFlows) {
            flows.add(new Flow_(f));
        }

        flow = new Flow(getFormattedMac(), flows);

        Log.d(TAG, gson.toJson(flow));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean compact = prefs.getBoolean("compactFlow", false);

        // Post data
        if(compact) {
            api.postCompactFlow(flow)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<Void>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Response response) {
                            if (response.isSuccessful()) {
                                Log.i(TAG, "Successful compact flows POST");
                                // Remove everything
                                // dh.safeCleanupFlow(now);
                                // Remove only finished flows
                                // dh.cleanupFinishedFlow(now);

                            } else {
                                Log.w(TAG, "Failed to POST compact flows");
                                flowQueue.addAll(tempFlows);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "Error in compact flows POST", e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }else {
            api.postFlow(flow)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<Void>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Response response) {
                            if (response.isSuccessful()) {
                                Log.i(TAG, "Successful flows POST");
                                // Remove everything
                                // dh.safeCleanupFlow(now);
                                // Remove only finished flows
                                // dh.cleanupFinishedFlow(now);


                            } else {
                                Log.w(TAG, "Failed to POST flows");
                                flowQueue.addAll(tempFlows);
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "Error in flows POST", e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    public void deviceDump(){

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
                new Specification(es.ugr.mdsm.hardware.Info.numCpuCores(), es.ugr.mdsm.hardware.Info.ramTotal(mContext), es.ugr.mdsm.hardware.Info.batteryCapacity(mContext)),
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

    public void appDump(){

        List<App_> app_list = new ArrayList<>();
        for (ApplicationInfo applicationInfo :
                Info.getInstalledApplication(mContext)) {
            app_list.add(new App_(
                    Util.anonymizeApp(mContext, applicationInfo.packageName),
                    Util.bitSetToBase64(Permission.permissionsAsBitArray(Permission.permissionsOfApp(mContext, applicationInfo))),
                    Info.versionOfApp(mContext,applicationInfo),
                    Info.isAutoStarted(mContext, applicationInfo)
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

    public void sensorDump(){

        Sensor sensor = new Sensor(
                new Connectivity(
                        MobileData.isEnabled(mContext),
                        es.ugr.mdsm.connectivity.Wifi.isEnabled(mContext),
                        AirplaneMode.isEnabled(mContext),
                        Bluetooth.isEnabled(),
                        Location.isGpsEnabled(mContext)
                ),
                new Stat(
                        Usage.cpuUsage(),
                        Usage.ramUsage(mContext),
                        Usage.batteryLevel(mContext)
                ),
                new Security(
                        Configuration.isUnknownSourcesEnabled(mContext),
                        Configuration.isDeveloperOptionsEnabled(mContext),
                        PhysicalAccess.isDeviceSecure(mContext),
                        Configuration.isRooted(mContext)
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
                            Log.i(TAG, "Successful sensor POST");
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

    private void connectionDump(){
        
        List<Wifi> wifiList = new ArrayList<>();
        for (Map.Entry<String, String> entry:
                es.ugr.mdsm.connectivity.Wifi.configuredNetworks(mContext).entrySet()) {
            wifiList.add(new Wifi(entry.getKey(), entry.getValue()));
        }
        
        Connection connection = new Connection(getFormattedMac(),getTimeStamp(), wifiList, Bluetooth.bondedDevices());

        // Post connection
        api.postConnection(connection)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response response) {
                        if(response.isSuccessful()){
                            Log.i(TAG, "Successful connection POST");
                        }else{
                            Log.w(TAG, "Failed to POST connection");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"Error in connection POST",e);
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

