package es.ugr.mdsm.restDump;


import android.os.Handler;

public class DbDumper {
    private long mInterval;   // in ms
    private Handler restHandler;
    private Runnable periodicUpdate;
    private Runnable asyncUpdate;

    public DbDumper(int interval){
        mInterval = interval;
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
    }

    public void start(){
        restHandler.post(periodicUpdate);
    }

    public void async(){
        restHandler.post(asyncUpdate);
    }
    public void stop(){
        restHandler.removeCallbacks(periodicUpdate);
        restHandler.removeCallbacks(asyncUpdate);
    }

    public void dataDump(){
        // Read flows since now

        // Create Gson object

        // Post data

        // Remove only ended flows

    }



}

