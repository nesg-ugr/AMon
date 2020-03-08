package es.ugr.mdsm.restDump;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.ugr.mdsm.connectivity.MobileData;

public class Util {
    private static String TAG = "MDSM.Util";

    public static BigInteger bitSetToInteger(BitSet bitSet){
        byte[] bytes = bitSet.toByteArray();
        reverseByteArray(bytes);
        if (bytes.length > 0) {
            return new BigInteger(1, bytes);
        }else{
            return new BigInteger("0");
        }
    }

    public static String bitSetToBase64(BitSet bitSet){
        byte[] bytes = bitSet.toByteArray();
        reverseByteArray(bytes);
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static void reverseByteArray(byte[] bytes){
        for(int i=0; i< bytes.length/2; i++){
            byte temp = bytes[i];
            bytes[i] = bytes[bytes.length -i -1];
            bytes[bytes.length -i -1] = temp;
        }
    }

    public static String anonymizeApp(Context context, String input){
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean anonymize = prefs.getBoolean("anonymizeApp", false);
        //if(anonymize){
        if(false){
            try {
                return eu.faircode.netguard.Util.md5(input,"");
            } catch (Exception e) {
                Log.e(TAG, "MD5 not available" ,e);
                return input;
            }
        }else{
            return input;
        }
    }

    public static int formatNetworkGeneration(int networkCode){
        switch (networkCode){
            case MobileData.NETWORK_2G:
                return 2;
            case MobileData.NETWORK_3G:
                return 3;
            case MobileData.NETWORK_4G:
                return 4;
            default:
                return 0;
        }
    }

    public static List<Bluetooth> formatBluetoothList(Set<BluetoothDevice> set) {

        List<Bluetooth> result = new ArrayList<>();
        for(BluetoothDevice device : set){
            List<String> uuidList = new ArrayList<>();
            for(ParcelUuid uuid : device.getUuids()){
                uuidList.add(uuid.getUuid().toString().substring(4,8));
            }
            result.add(new Bluetooth(device.getName(), uuidList));

        }

        return result;

    }

    public static List<Usb> getUsbList(Context context) {
        List<Usb> result = new ArrayList<>();

        HashMap<String, UsbDevice> hostList = es.ugr.mdsm.connectivity.Usb.getAttachedDevices(context);
        UsbAccessory[] accessoryArray = es.ugr.mdsm.connectivity.Usb.getAttachedAccessories(context);

        if(hostList!=null){
            for (UsbDevice usbDevice : hostList.values()){
                result.add(new Usb(
                        usbDevice.getDeviceName(),
                        usbDevice.getManufacturerName(),
                        Usb.HOST_TYPE,
                        usbDevice.getDeviceClass(),
                        usbDevice.getDeviceSubclass()
                ));
            }
        }

        if(accessoryArray != null){
            List<UsbAccessory> accessoryList = Arrays.asList(accessoryArray);
            for (UsbAccessory usbAccessory : accessoryList){
                result.add(new Usb(
                        usbAccessory.getModel(),
                        usbAccessory.getManufacturer(),
                        Usb.ACCESSORY_TYPE,
                        null,null
                ));
            }
        }

        return result;
    }
}
