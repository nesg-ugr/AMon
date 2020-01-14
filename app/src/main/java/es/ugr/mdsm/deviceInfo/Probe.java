package es.ugr.mdsm.deviceInfo;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;

public class Probe {

    private final static String TAG = "MDSM.Probe";

    public static int batteryLevel(Context context){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        return batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }

    public static int batteryTotal(Context context){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        return batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    }

    // https://stackoverflow.com/a/46736648
    public static double batteryCapacity(Context context) {
        Object mPowerProfile;
        double batteryCapacity;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);

            batteryCapacity = (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            Log.e(TAG, "Battery Capacity can't be acquired", e);
            batteryCapacity = 0;
        }

        return batteryCapacity;

    }

    // Return ram value in MB
    public static long ramUsage(Context context){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        return (memoryInfo.totalMem - memoryInfo.availMem)/1024/1024;
    }

    // Return ram value in MB
    public static long ramTotal(Context context){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem/1024/1024;
    }
    public static int numCpuCores() {

        return Runtime.getRuntime().availableProcessors();

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

    public static boolean isWifiEnabled(Context context) {
        return ((WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
    }

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public static boolean isMobileDataEnabled(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).isDataEnabled();
        }else{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            try {
                Class cmClass = Class.forName(cm.getClass().getName());
                Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                method.setAccessible(true); // Make the method callable
                // get the setting for "mobile data"
                return (Boolean)method.invoke(cm);
            } catch (Exception e) {
                // Some problem accessible private API
                return false;
            }
        }
    }

    public static boolean isRoamingEnabled(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.DATA_ROAMING, 0) == 1;
    }

    public static boolean isGpsEnabled(Context context){
        return ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isAirplaneModeEnabled(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
    }

    public static boolean isNetworkLocationEnabled(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}
