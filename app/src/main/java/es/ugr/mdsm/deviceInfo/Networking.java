package es.ugr.mdsm.deviceInfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.net.Inet4Address;
import java.util.List;

public class Networking {

    private static final String TAG = "DeviceInfo.Networking";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String getNetworkAddress(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        List<LinkAddress> linkAddresses = manager.getLinkProperties(manager.getActiveNetwork()).getLinkAddresses();
        for (LinkAddress linkAddress: linkAddresses){
            if(linkAddress.getAddress() instanceof Inet4Address){
                return linkAddress.getAddress().getHostAddress();
            }
        }
        return null;
    }

}
