package es.ugr.mdsm.deviceInfo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;

public class Connection {
    private static String TAG = "MDSM.Connection";

    @RequiresPermission(ACCESS_COARSE_LOCATION)
    public static boolean unsecuredWifiConnection(Context context) {
        if (!Probe.isNetworkLocationEnabled(context)){
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
                    Log.d(TAG, network.SSID + " capabilities : " + capabilities);

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

    public static Map<String,String> configuredWifiList(Context context){
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

    public static ArrayList<String> bluetoothBoundedDevices(){
        if(!Probe.isBluetoothEnabled()){
            return null;
        }

        ArrayList<String> result = new ArrayList<>();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bluetoothDeviceSet = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bluetoothDevice:
             bluetoothDeviceSet) {
            result.add(bluetoothDevice.getName());
        }
        return result;
    }
}
