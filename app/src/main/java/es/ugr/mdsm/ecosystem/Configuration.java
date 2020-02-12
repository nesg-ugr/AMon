package es.ugr.mdsm.ecosystem;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import com.scottyab.rootbeer.RootBeer;

public class Configuration {

    private final static String TAG = "Ecosystem.Configuration";

    // For newer version (after Oreo), that permission can only be checked for its own app
    public static boolean isUnknownSourcesEnabled(Context context){
        PackageManager packageManager = context.getPackageManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            return packageManager.canRequestPackageInstalls();
        }else{
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0) == 1;
        }
    }

    public static boolean isDeveloperOptionsEnabled(Context context){
        return Settings.Secure.getInt(context.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0) == 1;
    }

    public static boolean isAdbEnabled(Context context) {
        return Settings.Global.getInt(context.getContentResolver(),Settings.Global.ADB_ENABLED,0) != 0;
    }

    // Wrapper for RootBeer lib
    public static boolean isRooted(Context context){
        return (new RootBeer(context).isRooted());
    }

}
