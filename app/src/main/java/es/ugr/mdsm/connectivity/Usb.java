package es.ugr.mdsm.connectivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.util.HashMap;

public class Usb {

    private final static String TAG = "Connectivity.Usb";

    public static boolean isActive(Context context){
        HashMap<String, UsbDevice> tmp = getAttachedDevices(context);
        return getAttachedAccessories(context)!=null || (tmp!=null && !tmp.isEmpty());
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
