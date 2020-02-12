package es.ugr.mdsm.hardware;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class Info {

    private final static String TAG = "Hardware.Info";

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
    public static long ramTotal(Context context){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem/1024/1024;
    }

    public static int numCpuCores() {

        return Runtime.getRuntime().availableProcessors();

    }
}
