package es.ugr.mdsm.deviceInfo;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.scottyab.rootbeer.RootBeer;

import java.io.File;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class Software {

    private final static String TAG = "DeviceInfo.Software";
    private final static int TOTAL_NUMBER_OF_PERMISSIONS = 155;

    public static String[] permissionsByApp(Context context, ApplicationInfo applicationInfo){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
            return packageInfo.requestedPermissions;    // could be null
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Name not found for application:" + applicationInfo,e);
            return null;
        }
    }

    public static List<ApplicationInfo> getInstalledApplication(Context context){
        PackageManager pm = context.getPackageManager();
        return  pm.getInstalledApplications(PackageManager.GET_META_DATA);
    }

    public static BitSet permissionsAsBitArray(String[] permissions){
        BitSet bitSet = new BitSet(TOTAL_NUMBER_OF_PERMISSIONS);
        // TODO: parse permissions to the bit array
        return bitSet;
    }

    // Can't assure that the application is autostarted, just that the app have the autostart permission
    public static boolean isAutoStarted(Context context, ApplicationInfo applicationInfo){
        String[] permissions = permissionsByApp(context, applicationInfo);
        return permissions != null && Arrays.asList(permissions).contains(Manifest.permission.RECEIVE_BOOT_COMPLETED);
    }

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

    // Wrapper for RootBeer lib
    public static boolean isRooted(Context context){
        return (new RootBeer(context).isRooted());
    }

    // Wrapper for KeyguardManager
    public static boolean isDeviceSecure(Context context){
       return ((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE)).isDeviceSecure();
    }

    // Wrapper for KeyguardManager
    public static boolean isDeviceLocked(Context context){
        return ((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE)).isDeviceLocked();
    }
}
