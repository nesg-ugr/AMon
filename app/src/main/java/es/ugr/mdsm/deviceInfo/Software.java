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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import es.ugr.mdsm.restDump.Connectivity;
import es.ugr.mdsm.restDump.Util;

public class Software {

    private final static String TAG = "DeviceInfo.Software";
    private final static int TOTAL_NUMBER_OF_PERMISSIONS = 156;

    public static String[] permissionsOfApp(Context context, ApplicationInfo applicationInfo){
        PackageManager pm = context.getPackageManager();
        String[] permissions = {};
        try {
            PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
            if(packageInfo.requestedPermissions != null){
                permissions = packageInfo.requestedPermissions;
            }
            return permissions;    // could be null
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Name not found for application:" + applicationInfo,e);
            return permissions;
        }
    }

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

    public static BitSet permissionsAsBitArray(String[] permissions){
        BitSet bitSet = new BitSet(TOTAL_NUMBER_OF_PERMISSIONS);
        String trimmedPermission;
        String[] splitArray;
        Map<String, Boolean> map = initialPermissionsMap();
        for (String permission :
                permissions) {
            splitArray = permission.split("\\.");
            trimmedPermission = splitArray[splitArray.length - 1];
            if(map.containsKey(trimmedPermission)) {
                map.put(trimmedPermission, true); // this way new keys aren't added
            }
        }

        List<Boolean> values = new ArrayList<>(map.values());

        for(int i=0; i < map.size(); i++){
            bitSet.set(i, values.get(i));
        }

        return bitSet;
    }

    private static TreeMap<String, Boolean> initialPermissionsMap(){
        TreeMap<String, Boolean> map = new TreeMap<>();
        map.put("ACCESS_BACKGROUND_LOCATION",false);
        map.put("ACCESS_CHECKIN_PROPERTIES",false);
        map.put("ACCESS_COARSE_LOCATION",false);
        map.put("ACCESS_FINE_LOCATION",false);
        map.put("ACCESS_LOCATION_EXTRA_COMMANDS",false);
        map.put("ACCESS_MEDIA_LOCATION",false);
        map.put("ACCESS_NETWORK_STATE",false);
        map.put("ACCESS_NOTIFICATION_POLICY",false);
        map.put("ACCESS_WIFI_STATE",false);
        map.put("ACCOUNT_MANAGER",false);
        map.put("ACTIVITY_RECOGNITION",false);
        map.put("ADD_VOICEMAIL",false);
        map.put("ANSWER_PHONE_CALLS",false);
        map.put("BATTERY_STATS",false);
        map.put("BIND_ACCESSIBILITY_SERVICE",false);
        map.put("BIND_APPWIDGET",false);
        map.put("BIND_AUTOFILL_SERVICE",false);
        map.put("BIND_CALL_REDIRECTION_SERVICE",false);
        map.put("BIND_CARRIER_MESSAGING_CLIENT_SERVICE",false);
        map.put("BIND_CARRIER_MESSAGING_SERVICE",false);
        map.put("BIND_CARRIER_SERVICES",false);
        map.put("BIND_CHOOSER_TARGET_SERVICE",false);
        map.put("BIND_CONDITION_PROVIDER_SERVICE",false);
        map.put("BIND_DEVICE_ADMIN",false);
        map.put("BIND_DREAM_SERVICE",false);
        map.put("BIND_INCALL_SERVICE",false);
        map.put("BIND_INPUT_METHOD",false);
        map.put("BIND_MIDI_DEVICE_SERVICE",false);
        map.put("BIND_NFC_SERVICE",false);
        map.put("BIND_NOTIFICATION_LISTENER_SERVICE",false);
        map.put("BIND_PRINT_SERVICE",false);
        map.put("BIND_QUICK_SETTINGS_TILE",false);
        map.put("BIND_REMOTEVIEWS",false);
        map.put("BIND_SCREENING_SERVICE",false);
        map.put("BIND_TELECOM_CONNECTION_SERVICE",false);
        map.put("BIND_TEXT_SERVICE",false);
        map.put("BIND_TV_INPUT",false);
        map.put("BIND_VISUAL_VOICEMAIL_SERVICE",false);
        map.put("BIND_VOICE_INTERACTION",false);
        map.put("BIND_VPN_SERVICE",false);
        map.put("BIND_VR_LISTENER_SERVICE",false);
        map.put("BIND_WALLPAPER",false);
        map.put("BLUETOOTH",false);
        map.put("BLUETOOTH_ADMIN",false);
        map.put("BLUETOOTH_PRIVILEGED",false);
        map.put("BODY_SENSORS",false);
        map.put("BROADCAST_PACKAGE_REMOVED",false);
        map.put("BROADCAST_SMS",false);
        map.put("BROADCAST_STICKY",false);
        map.put("BROADCAST_WAP_PUSH",false);
        map.put("CALL_COMPANION_APP",false);
        map.put("CALL_PHONE",false);
        map.put("CALL_PRIVILEGED",false);
        map.put("CAMERA",false);
        map.put("CAPTURE_AUDIO_OUTPUT",false);
        map.put("CHANGE_COMPONENT_ENABLED_STATE",false);
        map.put("CHANGE_CONFIGURATION",false);
        map.put("CHANGE_NETWORK_STATE",false);
        map.put("CHANGE_WIFI_MULTICAST_STATE",false);
        map.put("CHANGE_WIFI_STATE",false);
        map.put("CLEAR_APP_CACHE",false);
        map.put("CONTROL_LOCATION_UPDATES",false);
        map.put("DELETE_CACHE_FILES",false);
        map.put("DELETE_PACKAGES",false);
        map.put("DIAGNOSTIC",false);
        map.put("DISABLE_KEYGUARD",false);
        map.put("DUMP",false);
        map.put("EXPAND_STATUS_BAR",false);
        map.put("FACTORY_TEST",false);
        map.put("FOREGROUND_SERVICE",false);
        map.put("GET_ACCOUNTS",false);
        map.put("GET_ACCOUNTS_PRIVILEGED",false);
        map.put("GET_PACKAGE_SIZE",false);
        map.put("GET_TASKS",false);
        map.put("GLOBAL_SEARCH",false);
        map.put("INSTALL_LOCATION_PROVIDER",false);
        map.put("INSTALL_PACKAGES",false);
        map.put("INSTALL_SHORTCUT",false);
        map.put("INSTANT_APP_FOREGROUND_SERVICE",false);
        map.put("INTERNET",false);
        map.put("KILL_BACKGROUND_PROCESSES",false);
        map.put("LOCATION_HARDWARE",false);
        map.put("MANAGE_DOCUMENTS",false);
        map.put("MANAGE_OWN_CALLS",false);
        map.put("MASTER_CLEAR",false);
        map.put("MEDIA_CONTENT_CONTROL",false);
        map.put("MODIFY_AUDIO_SETTINGS",false);
        map.put("MODIFY_PHONE_STATE",false);
        map.put("MOUNT_FORMAT_FILESYSTEMS",false);
        map.put("MOUNT_UNMOUNT_FILESYSTEMS",false);
        map.put("NFC",false);
        map.put("NFC_TRANSACTION_EVENT",false);
        map.put("PACKAGE_USAGE_STATS",false);
        map.put("PERSISTENT_ACTIVITY",false);
        map.put("PROCESS_OUTGOING_CALLS",false);
        map.put("READ_CALENDAR",false);
        map.put("READ_CALL_LOG",false);
        map.put("READ_CONTACTS",false);
        map.put("READ_EXTERNAL_STORAGE",false);
        map.put("READ_INPUT_STATE",false);
        map.put("READ_LOGS",false);
        map.put("READ_PHONE_NUMBERS",false);
        map.put("READ_PHONE_STATE",false);
        map.put("READ_SMS",false);
        map.put("READ_SYNC_SETTINGS",false);
        map.put("READ_SYNC_STATS",false);
        map.put("READ_VOICEMAIL",false);
        map.put("REBOOT",false);
        map.put("RECEIVE_BOOT_COMPLETED",false);
        map.put("RECEIVE_MMS",false);
        map.put("RECEIVE_SMS",false);
        map.put("RECEIVE_WAP_PUSH",false);
        map.put("RECORD_AUDIO",false);
        map.put("REORDER_TASKS",false);
        map.put("REQUEST_COMPANION_RUN_IN_BACKGROUND",false);
        map.put("REQUEST_COMPANION_USE_DATA_IN_BACKGROUND",false);
        map.put("REQUEST_DELETE_PACKAGES",false);
        map.put("REQUEST_IGNORE_BATTERY_OPTIMIZATIONS",false);
        map.put("REQUEST_INSTALL_PACKAGES",false);
        map.put("REQUEST_PASSWORD_COMPLEXITY",false);
        map.put("RESTART_PACKAGES",false);
        map.put("SEND_RESPOND_VIA_MESSAGE",false);
        map.put("SEND_SMS",false);
        map.put("SET_ALARM",false);
        map.put("SET_ALWAYS_FINISH",false);
        map.put("SET_ANIMATION_SCALE",false);
        map.put("SET_DEBUG_APP",false);
        map.put("SET_PREFERRED_APPLICATIONS",false);
        map.put("SET_PROCESS_LIMIT",false);
        map.put("SET_TIME",false);
        map.put("SET_TIME_ZONE",false);
        map.put("SET_WALLPAPER",false);
        map.put("SET_WALLPAPER_HINTS",false);
        map.put("SIGNAL_PERSISTENT_PROCESSES",false);
        map.put("SMS_FINANCIAL_TRANSACTIONS",false);
        map.put("STATUS_BAR",false);
        map.put("SYSTEM_ALERT_WINDOW",false);
        map.put("TRANSMIT_IR",false);
        map.put("UNINSTALL_SHORTCUT",false);
        map.put("UPDATE_DEVICE_STATS",false);
        map.put("USE_BIOMETRIC",false);
        map.put("USE_FINGERPRINT",false);
        map.put("USE_FULL_SCREEN_INTENT",false);
        map.put("USE_SIP",false);
        map.put("VIBRATE",false);
        map.put("WAKE_LOCK",false);
        map.put("WRITE_APN_SETTINGS",false);
        map.put("WRITE_CALENDAR",false);
        map.put("WRITE_CALL_LOG",false);
        map.put("WRITE_CONTACTS",false);
        map.put("WRITE_EXTERNAL_STORAGE",false);
        map.put("WRITE_GSERVICES",false);
        map.put("WRITE_SECURE_SETTINGS",false);
        map.put("WRITE_SETTINGS",false);
        map.put("WRITE_SYNC_SETTINGS",false);
        map.put("WRITE_VOICEMAIL",false);
        return map;
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
        String[] permissions = permissionsOfApp(context, applicationInfo);
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
