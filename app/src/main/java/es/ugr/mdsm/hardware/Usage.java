package es.ugr.mdsm.hardware;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Usage {

    private final static String TAG = "Hardware.Usage";

    public static int batteryLevel(Context context){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        return batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }

    // Return ram value in MB
    public static long ramUsage(Context context){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        return (memoryInfo.totalMem - memoryInfo.availMem)/1024/1024;
    }

    // https://stackoverflow.com/a/25717220
    public static float cpuUsageByCore(int i) {
        /*
         * how to calculate multicore
         * this function reads the bytes from a logging file in the android system (/proc/stat for cpu values)
         * then puts the line into a string
         * then spilts up each individual part into an array
         * then(since he know which part represents what) we are able to determine each cpu total and work
         * then combine it together to get a single float for overall cpu usage
         */

        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            //skip to the line we need
            for(int ii = 0; ii < i + 1; ++ii){
                reader.readLine();
            }
            String load = reader.readLine();

            //cores will eventually go offline, and if it does, then it is at 0% because it is not being
            //used. so we need to do check if the line we got contains cpu, if not, then this core = 0
            if(load.contains("cpu")){
                String[] toks = load.split("\\s+");

                //we are recording the work being used by the user and system(work) and the total info
                //of cpu stuff (total)
                //https://stackoverflow.com/questions/3017162/how-to-get-total-cpu-usage-in-linux-c/3017438#3017438

                long work1 = Long.parseLong(toks[1])+ Long.parseLong(toks[2]) + Long.parseLong(toks[3]);
                long total1 = Long.parseLong(toks[1])+ Long.parseLong(toks[2]) + Long.parseLong(toks[3]) +
                        Long.parseLong(toks[4]) + Long.parseLong(toks[5])
                        + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

                try{
                    //short sleep time = less accurate. But android devices typically don't have more than
                    //4 cores, and I'n my app, I run this all in a second. So, I need it a bit shorter
                    Thread.sleep(200);
                } catch (Exception e) {}

                reader.seek(0);
                //skip to the line we need
                for(int ii = 0; ii < i + 1; ++ii)
                {
                    reader.readLine();
                }
                load = reader.readLine();
                //cores will eventually go offline, and if it does, then it is at 0% because it is not being
                //used. so we need to do check if the line we got contains cpu, if not, then this core = 0%
                if(load.contains("cpu")){
                    reader.close();
                    toks = load.split("\\s+");

                    long work2 = Long.parseLong(toks[1])+ Long.parseLong(toks[2]) + Long.parseLong(toks[3]);
                    long total2 = Long.parseLong(toks[1])+ Long.parseLong(toks[2]) + Long.parseLong(toks[3]) +
                            Long.parseLong(toks[4]) + Long.parseLong(toks[5])
                            + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);



                    //here we find the change in user work and total info, and divide by one another to get our total
                    //seems to be accurate need to test on quad core
                    //https://stackoverflow.com/questions/3017162/how-to-get-total-cpu-usage-in-linux-c/3017438#3017438

                    return (float)(work2 - work1) / ((total2 - total1));
                } else{
                    reader.close();
                    return 0;
                }

            }
            else{
                reader.close();
                return 0;
            }

        }catch (IOException ex){
            ex.printStackTrace();
        }

        return 0;
    }

    public static float cpuUsage(){
        return cpuUsageByCore(-1);
    }
}
