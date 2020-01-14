package es.ugr.mdsm.deviceInfo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class Connection {
    private static String TAG = "MDSM.Connection";

    public static final int NETWORK_UNKNOWN = 0;
    public static final int NETWORK_2G = 1;
    public static final int NETWORK_3G = 2;
    public static final int NETWORK_4G = 3;

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

    public static ArrayList<String> bluetoothBondedDevices(){
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

    public static boolean isWifiActive(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = (cm == null ? null : cm.getActiveNetwork());
        return network != null && cm.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }

    public static boolean isMobileDataActive(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = (cm == null ? null : cm.getActiveNetwork());
        return network != null && cm.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
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

    public static boolean isVpnActive(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = (cm == null ? null : cm.getActiveNetwork());
        return network != null && !cm.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_VPN);
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

    public static boolean hasUsbAccesoryFeature(Context context){
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY);
    }

    public static boolean hasUsbHostFeature(Context context){
        PackageManager pm = context.getPackageManager();
        return pm!=null && pm.hasSystemFeature(PackageManager.FEATURE_USB_HOST);
    }

    public static UsbAccessory[] getAttachedAccessories(Context context){
        UsbManager um = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        return um==null ? null : um.getAccessoryList();
    }

    public static HashMap<String, UsbDevice> getAttachedDevices(Context context){
        UsbManager um = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        return um==null ? null : um.getDeviceList();
    }

}
