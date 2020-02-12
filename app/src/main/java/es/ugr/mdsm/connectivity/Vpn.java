package es.ugr.mdsm.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class Vpn {

    private final static String TAG = "Connectivity.Vpn";

    public static boolean isActive(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = (cm == null ? null : cm.getActiveNetwork());
        return network != null && !cm.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_VPN);
    }
}
