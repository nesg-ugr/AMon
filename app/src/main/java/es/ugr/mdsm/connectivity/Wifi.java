package es.ugr.mdsm.connectivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class Wifi {

    private final static String TAG = "Connectivity.Wifi";

    @RequiresPermission(ACCESS_COARSE_LOCATION)
    public static boolean isCurrentConnectionUnsecure(Context context) {
        if (!Location.isNetworkLocationEnabled(context)){
            return false;
        }

        // https://stackoverflow.com/a/40052951
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> networkList = wifiManager.getScanResults();

        //get current connected SSID for comparison to ScanResult
        String currentSSID = wifiManager.getConnectionInfo().getSSID().replaceAll("^\"(.*)\"$", "$1");

        if (networkList != null) {
            for (ScanResult network : networkList) {
                //check if current connected SSID
                if (currentSSID.equals(network.SSID)) {
                    //get capabilities of current connection
                    String capabilities = network.capabilities;
                    Log.d(Wifi.TAG, network.SSID + " capabilities : " + capabilities);

                    if (capabilities.contains("WPA2")) {
                        return false;
                    } else if (capabilities.contains("WPA")) {
                        return false;
                    } else if (capabilities.contains("WEP")) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Map<String,String> configuredNetworks(Context context){
        Map<String, String> map = new HashMap<>();
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configurationList  = wifiManager.getConfiguredNetworks();

        // https://stackoverflow.com/a/28027619
        for (WifiConfiguration wifiConfiguration :
                configurationList) {
            String security;
            if(wifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)){
                if(wifiConfiguration.allowedProtocols.get(WifiConfiguration.Protocol.RSN)){
                    security = "WPA2";
                }else{
                    security = "WPA";
                }
            } else if(wifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP)){
                security = "WPA-EAP";
            } else if(wifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)){
                security = "802.1X";
            } else if(wifiConfiguration.wepKeys[0]!=null){
                security = "WEP";
            } else {
                security = "NONE";
            }
            map.put(wifiConfiguration.SSID, security);
        }
        return map;
    }

    public static boolean isActive(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = (cm == null ? null : cm.getActiveNetwork());
        return network != null && cm.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }

    public static boolean isEnabled(Context context) {
        return ((WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
    }

    public static boolean isPresent(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI);
    }
}
