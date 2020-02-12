package es.ugr.mdsm.connectivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

public class Location {

    private final static String TAG = "Connectivity.Location";

    public static boolean isGpsEnabled(Context context){
        return ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isGpsPresent(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    public static boolean isNetworkLocationEnabled(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
