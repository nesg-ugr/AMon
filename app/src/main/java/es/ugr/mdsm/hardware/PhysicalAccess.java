package es.ugr.mdsm.hardware;

import android.app.KeyguardManager;
import android.content.Context;

public class PhysicalAccess {

    private final static String TAG = "Hardware.PhysicalAccess";

    // Wrapper for KeyguardManager
    public static boolean isDeviceSecure(Context context){
       return ((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE)).isDeviceSecure();
    }

    // Wrapper for KeyguardManager
    public static boolean isDeviceLocked(Context context){
        return ((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE)).isDeviceLocked();
    }
}
