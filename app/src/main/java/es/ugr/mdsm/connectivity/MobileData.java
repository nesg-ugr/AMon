package es.ugr.mdsm.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

public class MobileData {

    private final static String TAG = "Connectivity.MobileData";


    public static final int NETWORK_UNKNOWN = 0;
    public static final int NETWORK_2G = 1;
    public static final int NETWORK_3G = 2;
    public static final int NETWORK_4G = 3;

    public static boolean isPresent(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = (cm == null ? null : cm.getActiveNetwork());
        return network != null && cm.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
    }

    public static boolean isEnabled(Context context){
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

    public static boolean isRoamingActive(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            Network network = (cm == null ? null : cm.getActiveNetwork());
            return network != null && !cm.getNetworkCapabilities(network).hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING);
        }else {
            NetworkInfo ni = (cm == null ? null : cm.getActiveNetworkInfo());
            return (ni != null && ni.isRoaming());
        }
    }

    public static int getNetworkGeneration(Context context){
        int nt;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            nt = tm!=null ? tm.getNetworkType() : TelephonyManager.NETWORK_TYPE_UNKNOWN;
        }else {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm!=null ? cm.getActiveNetworkInfo() : null;
            nt = ni != null && ni.getType() == ConnectivityManager.TYPE_MOBILE ? ni.getSubtype() : TelephonyManager.NETWORK_TYPE_UNKNOWN;
        }
        switch (nt){
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return NETWORK_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
            case TelephonyManager.NETWORK_TYPE_IWLAN:
                return NETWORK_4G;
            default:
                return NETWORK_UNKNOWN;
        }
    }



    public static boolean isRoamingEnabled(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.DATA_ROAMING, 0) == 1;
    }
}
