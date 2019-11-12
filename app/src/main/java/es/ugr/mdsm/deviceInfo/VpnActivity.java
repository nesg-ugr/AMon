package es.ugr.mdsm.deviceInfo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import es.ugr.mdsm.amon.R;
import eu.faircode.netguard.ServiceSinkhole;
import eu.faircode.netguard.Util;


public class VpnActivity extends AppCompatActivity {
    public final static int MODE_FLOW = 0;
    private static final int REQUEST_VPN = 1;
    private final static String TAG = "MDSM.VpnActivity";
    private SharedPreferences prefs;
    private AlertDialog dialogVpn = null;
    private boolean lastState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("Vpn", Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Handle external VPN stops
        if (lastState && !isVpnEnabled()){
            activateVpn(false);
        }

        onStateUpdated();
    }

    public void activateVpn(boolean enable){
        if(enable){
            String alwaysOn = Settings.Secure.getString(getContentResolver(), "always_on_vpn_app");
            Log.i(TAG, "Always-on=" + alwaysOn);
            if (!TextUtils.isEmpty(alwaysOn))
                if (getPackageName().equals(alwaysOn)) {
                    if (isFiltering()) {
                        int lockdown = Settings.Secure.getInt(getContentResolver(), "always_on_vpn_lockdown", 0);
                        Log.i(TAG, "Lockdown=" + lockdown);
                        if (lockdown != 0) {
                            Toast.makeText(this, R.string.msg_always_on_lockdown, Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, R.string.msg_always_on, Toast.LENGTH_LONG).show();
                    return;
                }

            String dns_mode = Settings.Global.getString(getContentResolver(), "private_dns_mode");
            Log.i(TAG, "Private DNS mode=" + dns_mode);
            if (dns_mode == null)
                dns_mode = "off";
            if (!"off".equals(dns_mode)) {
                Toast.makeText(this, R.string.msg_private_dns, Toast.LENGTH_LONG).show();
                return;
            }

            try {
                final Intent prepare = VpnService.prepare(this);
                if (prepare == null) {
                    Log.i(TAG, "Prepare done");
                    onActivityResult(REQUEST_VPN, RESULT_OK, null);
                } else {
                    // Show dialog
                    showDialog(prepare);
                }
            } catch (Throwable ex) {
                // Prepare failed
                Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                enabledVpn(false);
                onStateUpdated();
            }

        } else{
            ServiceSinkhole.stop("switch off", this, false);
            enabledVpn(false);
            onStateUpdated();
        }
    }

    // Do the VPN request by a dialog
    private void showDialog(final Intent prepare) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.vpn, null, false);
        dialogVpn = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Start intent=" + prepare);
                        try {
                            // com.android.vpndialogs.ConfirmDialog required
                            startActivityForResult(prepare, REQUEST_VPN);
                        } catch (Throwable ex) {
                            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                            onActivityResult(REQUEST_VPN, RESULT_CANCELED, null);
                            enabledVpn(false);
                            onStateUpdated();
                        }

                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        dialogVpn = null;
                    }
                })
                .create();
        dialogVpn.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.i(TAG, "onActivityResult request=" + requestCode + " result=" + requestCode + " ok=" + (resultCode == RESULT_OK));
        Util.logExtras(data);

        if (requestCode == REQUEST_VPN) {
            // Handle VpnActivity approval
            enabledVpn(resultCode == RESULT_OK);
            onStateUpdated();
            filter(true);
            if (resultCode == RESULT_OK) {
                ServiceSinkhole.start("prepared", this);

                Toast on = Toast.makeText(this, R.string.msg_on, Toast.LENGTH_LONG);
                on.setGravity(Gravity.CENTER, 0, 0);
                on.show();

            } else if (resultCode == RESULT_CANCELED)
                Toast.makeText(this, R.string.msg_vpn_cancelled, Toast.LENGTH_LONG).show();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void setupVpn(int mode){
        switch (mode){
            case MODE_FLOW:
                // Disable traffic lockdown
                lockNetwork(false);
                // Disable filtering
                logging(false);
                // Disable usage tracking
                trackUsage(false);
                // Enable flow collecting
                collectFlow(true);
                // Disable anoymization
                anonymizeData(false);
                // Compact flows
                compactFlow(false);
                // Enable apps under WiFi
                whitelistWifi(false);
                // Enable other apps
                whitelistOther(false);
                break;
            default:
        }
    }

    // Set to 0 to disable the watchdog
    public void setWatchdog(int minutes){
        prefs.edit().putInt("watchdog", minutes).apply();
    }

    protected void onStateUpdated(){
        lastState = isVpnEnabled();
        // Implement your own changes
    }

    @Override
    protected void onDestroy() {
        if(dialogVpn != null){
            dialogVpn.dismiss();
            dialogVpn = null;
        }
        super.onDestroy();
    }

    public boolean isVpnEnabled(){
        return prefs.getBoolean("enabled",false);
    }

    // Only for private usage. For user usage check activateVpn()
    private void enabledVpn(boolean enabled){
        prefs.edit().putBoolean("enabled", enabled).apply();
    }

    public boolean isFiltering(){
        return prefs.getBoolean("filter",false);
    }

    public void filter(boolean filtered){
        prefs.edit().putBoolean("filter", filtered).apply();

    }

    public boolean isNetworkLocked(){
        return prefs.getBoolean("lockdown",false);
    }

    public void lockNetwork(boolean locked){
        prefs.edit().putBoolean("lockdown",locked).apply();
        ServiceSinkhole.reload("changed lockdown", this, false);
    }

    public boolean isLogging(){
        return prefs.getBoolean("log",false);

    }

    public void logging(boolean enabled) {
        prefs.edit().putBoolean("log",enabled).apply();
        ServiceSinkhole.reload("changed logging", this, false);

    }

    public boolean isLoggingApp(){
        return prefs.getBoolean("log_app",false);
    }

    public void appLogging(boolean enabled){
        prefs.edit().putBoolean("log_app",enabled).apply();
    }

    public boolean isUsageTracked(){
        return prefs.getBoolean("track_usage",false) && isLoggingApp();
    }

    public void trackUsage(boolean enabled){
        prefs.edit().putBoolean("track_usage", enabled).apply();
        ServiceSinkhole.reload("changed tracking", this, false);
    }

    public boolean isFlowCollected(){
        return prefs.getBoolean("collect_flow",false);
    }

    public void collectFlow(boolean enabled){
        prefs.edit().putBoolean("collect_flow", enabled).apply();
        ServiceSinkhole.reload("changed flow capturing", this, false);
    }

    public boolean isDataAnonymized(){
        return prefs.getBoolean("anonymizeApp",false);
    }

    public void anonymizeData(boolean enabled){
        prefs.edit().putBoolean("anonymizeApp", enabled).apply();
        ServiceSinkhole.reload("changed anonymization", this, false);
    }

    public boolean isFlowCompacted(){
        return prefs.getBoolean("compactFlow",false);
    }

    public void compactFlow(boolean enabled){
        prefs.edit().putBoolean("compactFlow", enabled).apply();
        ServiceSinkhole.reload("changed flow compaction", this, false);
    }

    public boolean isWifiWhitelisted(){
        return prefs.getBoolean("whitelist_wifi",false);
    }

    public void whitelistWifi(boolean enabled){
        prefs.edit().putBoolean("whitelist_wifi", enabled).apply();
        ServiceSinkhole.reload("changed wifi whitelisting", this, false);
    }

    public boolean isOtherWhitelisted(){
        return prefs.getBoolean("whitelist_other",false);
    }

    public void whitelistOther(boolean enabled){
        prefs.edit().putBoolean("whitelist_other", enabled).apply();
        ServiceSinkhole.reload("changed other whitelisting", this, false);
    }


}
