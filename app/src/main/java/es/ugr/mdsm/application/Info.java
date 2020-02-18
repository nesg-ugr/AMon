package es.ugr.mdsm.application;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import es.ugr.mdsm.restDump.Util;

public class Info {
    private final static String TAG = "Application.Info";

    public static String versionOfApp(Context context, ApplicationInfo applicationInfo){
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(applicationInfo.packageName,0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Name not found for application:" + applicationInfo,e);
            return "unavailable";
        }
    }

    public static List<ApplicationInfo> getInstalledApplication(Context context){
        PackageManager pm = context.getPackageManager();
        return  pm.getInstalledApplications(PackageManager.GET_META_DATA);
    }

    public static String getNameForUid(Context context, int uid){
        ApplicationInfo info = null;
        PackageManager pm = context.getPackageManager();
        String[] pkg = pm.getPackagesForUid(uid);
        if (pkg != null && pkg.length > 0){
            try {
                info = pm.getApplicationInfo(pkg[0], 0);
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }

        return info==null ? null : Util.anonymizeApp(context, info.packageName);

    }

    // Can't assure that the application is autostarted, just that the app have the autostart permission
    public static boolean isAutoStarted(Context context, ApplicationInfo applicationInfo){
        String[] permissions = Permission.permissionsOfApp(context, applicationInfo);
        return permissions != null && Arrays.asList(permissions).contains(Manifest.permission.RECEIVE_BOOT_COMPLETED);
    }

    public static String getInstallerOfPackage(Context context, ApplicationInfo applicationInfo){

        return context.getPackageManager().getInstallerPackageName(applicationInfo.packageName);

    }

    public static boolean isInstalledFromGooglePlay(Context context, ApplicationInfo applicationInfo){
        return getInstallerOfPackage(context, applicationInfo).equals("com.android.vending");
    }
}
