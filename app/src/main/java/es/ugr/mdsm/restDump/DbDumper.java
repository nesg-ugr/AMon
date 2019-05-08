package es.ugr.mdsm.restDump;


import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import eu.faircode.netguard.DatabaseHelper;
import eu.faircode.netguard.Util;
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
    //private static final String ENDPOINT = "https://mdsm1.ugr.es/";

    private long mInterval;   // in ms
    private Context mContext;
    private Handler restHandler;
    private Runnable periodicUpdate;
    private Runnable asyncUpdate;
    private Runnable devicePush;
    private Api api;
    private DatabaseHelper dh;

    public DbDumper(int interval, Context context){
        mInterval = interval;
        mContext = context;
        restHandler = new Handler();
        periodicUpdate = new Runnable() {
            @Override
            public void run() {
                dataDump();
                restHandler.postDelayed(this, mInterval);
            }
        };
        asyncUpdate = new Runnable() {
            @Override
            public void run() {
                dataDump();
            }
        };
        devicePush = new Runnable() {
            @Override
            public void run() {
                deviceDump();
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
    public void stop(){
        restHandler.removeCallbacks(periodicUpdate);
        restHandler.removeCallbacks(asyncUpdate);
    }

    private void dataDump(){
        final long now = Calendar.getInstance().getTimeInMillis();
        List<GFlow> gFlows = new ArrayList<>();
        FlowDump flowDump;
        Gson gson = new Gson();

        // Read flows since now
        Cursor cursor = dh.getFlow(now);

        if(cursor.getCount() <= 0){
            Log.d(TAG, "There's no flow entry to send");
            return;
        }

        while (cursor.moveToNext()){
            gFlows.add(new GFlow(
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
                    cursor.getInt(cursor.getColumnIndex("ToS"))
            ));
        }

        cursor.close();

        flowDump = new FlowDump(Util.getMacAddress().replace(":",""), gFlows);

        //Log.d(TAG, flowDump.toString());
        Log.d(TAG, gson.toJson(flowDump));

        // Post data
        api.postFlows(flowDump)
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
                            // Remove only ended flows
                            //dh.cleanupEndedFlow(now);
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

        // Create Gson object
        Device device = new Device(
                Util.getMacAddress().replace(":",""),
                Build.VERSION.SDK_INT);

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


}

