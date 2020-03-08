package es.ugr.mdsm.connectivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Set;

public class Bluetooth {

    private final static String TAG = "Connectivity.Bluetooth";

    public static ArrayList<String> getBondedDevicesByName(){

        ArrayList<String> result = new ArrayList<>();
        Set<BluetoothDevice> bluetoothDeviceSet = getBondedDevices();
        if (bluetoothDeviceSet==null) return null;
        for (BluetoothDevice bluetoothDevice:
             bluetoothDeviceSet) {
            result.add(bluetoothDevice.getName());
        }
        return result;
    }

    public static Set<BluetoothDevice> getBondedDevices(){
        if(!isEnabled()){
            return null;
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.getBondedDevices();
    }

    public static boolean isEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public static boolean isPresent(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }
}
