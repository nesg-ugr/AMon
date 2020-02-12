package es.ugr.mdsm.connectivity;

import android.content.Context;
import android.provider.Settings;

public class AirplaneMode {

    private final static String TAG = "Connectivity.AirplaneMode";

    public static boolean isEnabled(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
    }
}
