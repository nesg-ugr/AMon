package es.ugr.mdsm.connectivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Tethering {

    private final static String TAG = "Connectivity.Tethering";

    public static boolean isWifiTetheringEnabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            return (Boolean) method.invoke(wifiManager);
        } catch (Exception e) {
            return false;
        }
    }

    // https://stackoverflow.com/a/22668259
    // May be not working
    public static boolean isBluetoothTetheringEnabled(Context context) {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            Class classBluetoothPan = Class.forName("android.bluetooth.BluetoothPan");
            Method mIsBTTetheringOn = classBluetoothPan.getDeclaredMethod("isTetheringOn", null);
            Constructor<?> BTPanCtor = classBluetoothPan.getDeclaredConstructor(Context.class, BluetoothProfile.ServiceListener.class);
            BTPanCtor.setAccessible(true);
            Object BTSrvInstance = BTPanCtor.newInstance(context, new BluetoothProfile.ServiceListener() {
                        @Override
                        public void onServiceConnected(int profile, BluetoothProfile proxy) {
                            //Some code must be here or the compiler will optimize away this callback.
                            Log.i(TAG, "BTPan proxy connected");
                        }

                        @Override
                        public void onServiceDisconnected(int profile) {
                        }
                    });
            return bluetoothAdapter!= null & (Boolean) mIsBTTetheringOn.invoke(BTSrvInstance, null);
        } catch (Exception e) {
            return false;
        }
    }

    // Not tested
    public static boolean isUsbTetheringEnabled(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Method method = cm.getClass().getDeclaredMethod("getTetherableIfaces");
            return Arrays.asList(method.invoke(cm)).contains("usb0");
        } catch (Exception e) {
            return false;
        }
    }


}
