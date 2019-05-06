package eu.faircode.netguard;

/*
    This file is part of NetGuard.

    NetGuard is free software: you can redistribute it and/or modify

    NetGuard is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NetGuard.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2015-2019 by Marcel Bokhorst (M66B)
*/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import es.ugr.mdsm.restDump.DbDumper;

public class ActivityMain extends AppCompatActivity {
    private static final String TAG = "NetGuard.Main";

    private boolean running = false;
    //private ImageView ivIcon;
    //private ImageView ivQueue;
    private Switch swEnabled;
    //private ImageView ivMetered;
    private AlertDialog dialogFirst = null;
    private AlertDialog dialogVpn = null;
    private AlertDialog dialogDoze = null;
    //private AlertDialog dialogAbout = null;

    private static final int REQUEST_VPN = 1;
    private static final int REQUEST_LOGCAT = 2;
    public static final int REQUEST_ROAMING = 3;
    public static final int REQUEST_PCAP = 4;
    public static final int INTERVAL_UPDATE = 5*60*1000;

    private static final int MIN_SDK = Build.VERSION_CODES.LOLLIPOP_MR1;

    public static final String ACTION_RULES_CHANGED = "eu.faircode.netguard.ACTION_RULES_CHANGED";
    public static final String ACTION_QUEUE_CHANGED = "eu.faircode.netguard.ACTION_QUEUE_CHANGED";
    public static final String EXTRA_REFRESH = "Refresh";
    public static final String EXTRA_SEARCH = "Search";
    public static final String EXTRA_RELATED = "Related";
    public static final String EXTRA_APPROVE = "Approve";
    public static final String EXTRA_LOGCAT = "Logcat";
    public static final String EXTRA_CONNECTED = "Connected";
    public static final String EXTRA_METERED = "Metered";
    public static final String EXTRA_SIZE = "Size";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Create version=" + Util.getSelfVersionName(this) + "/" + Util.getSelfVersionCode(this));
        Util.logExtras(getIntent());

        // Check minimum Android version
        if (Build.VERSION.SDK_INT < MIN_SDK) {
            Log.i(TAG, "SDK=" + Build.VERSION.SDK_INT);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.android);
            return;
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        running = true;

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Disable traffic lockdown
        lockNetwork(false);
        // Disable filtering
        logging(false);
        // Disable usage tracking
        trackUsage(false);
        // Enable flow collecting
        collectFlow(true);
        // Enable PCAP file
        enablePcap(null);
        findViewById(R.id.pcapButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportPcap();
            }
        });

        boolean enabled = prefs.getBoolean("enabled", false);
        boolean initialized = prefs.getBoolean("initialized", false);

        // Upgrade - Modify some prefs, could be extracted.
        ReceiverAutostart.upgrade(initialized, this);
        //prefs.edit().putBoolean("lockdown_wifi",false).apply();
        //prefs.edit().putBoolean("lockdown_other",false).apply();

        DbDumper dbDumper = new DbDumper(20*1000, this);
        dbDumper.dumpDeviceInfo();
        dbDumper.start();

        // Debug switch
        swEnabled = findViewById(R.id.swEnabled);
        swEnabled.setChecked(enabled);
        swEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activateVpn(isChecked);
            }
        });


        if (!getIntent().hasExtra(EXTRA_APPROVE)) {
            if (enabled)
                ServiceSinkhole.start("UI", this);
            else
                ServiceSinkhole.stop("UI", this, false);
        }

        if (enabled)
            checkDoze();

        // Listen for rule set changes
        IntentFilter ifr = new IntentFilter(ACTION_RULES_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(onRulesChanged, ifr);

        // Listen for queue changes
        IntentFilter ifq = new IntentFilter(ACTION_QUEUE_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(onQueueChanged, ifq);

        // Listen for added/removed applications
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(packageChangedReceiver, intentFilter);

        // First use
        if (!initialized) {
            // Create view
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.first, null, false);

            TextView tvFirst = view.findViewById(R.id.tvFirst);
            TextView tvEula = view.findViewById(R.id.tvEula);
            TextView tvPrivacy = view.findViewById(R.id.tvPrivacy);
            tvFirst.setMovementMethod(LinkMovementMethod.getInstance());
            tvEula.setMovementMethod(LinkMovementMethod.getInstance());
            tvPrivacy.setMovementMethod(LinkMovementMethod.getInstance());

            // Show dialog
            dialogFirst = new AlertDialog.Builder(this)
                    .setView(view)
                    .setCancelable(false)
                    .setPositiveButton(R.string.app_agree, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (running) {
                                prefs.edit().putBoolean("initialized", true).apply();
                            }
                        }
                    })
                    .setNegativeButton(R.string.app_disagree, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (running)
                                finish();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            dialogFirst = null;
                        }
                    })
                    .create();
            dialogFirst.show();
        }

        // Handle intent
        checkExtras(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "New intent");
        Util.logExtras(intent);
        super.onNewIntent(intent);

        if (Build.VERSION.SDK_INT < MIN_SDK || Util.hasXposed(this))
            return;

        setIntent(intent);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "Resume");

        if (Build.VERSION.SDK_INT < MIN_SDK || Util.hasXposed(this)) {
            super.onResume();
            return;
        }

        super.onResume();

        // Visual feedback
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        swEnabled.setChecked(prefs.getBoolean("enabled",false));
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "Pause");
        super.onPause();

        if (Build.VERSION.SDK_INT < MIN_SDK || Util.hasXposed(this))
            return;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "Config");
        super.onConfigurationChanged(newConfig);

        if (Build.VERSION.SDK_INT < MIN_SDK || Util.hasXposed(this))
            return;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Destroy");

        if (Build.VERSION.SDK_INT < MIN_SDK || Util.hasXposed(this)) {
            super.onDestroy();
            return;
        }

        running = false;

        //PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(onRulesChanged);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onQueueChanged);
        unregisterReceiver(packageChangedReceiver);

        if (dialogFirst != null) {
            dialogFirst.dismiss();
            dialogFirst = null;
        }
        if (dialogVpn != null) {
            dialogVpn.dismiss();
            dialogVpn = null;
        }
        if (dialogDoze != null) {
            dialogDoze.dismiss();
            dialogDoze = null;
        }

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.i(TAG, "onActivityResult request=" + requestCode + " result=" + requestCode + " ok=" + (resultCode == RESULT_OK));
        Util.logExtras(data);

        if (requestCode == REQUEST_VPN) {
            // Handle VPN approval
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putBoolean("enabled", resultCode == RESULT_OK).apply();
            prefs.edit().putBoolean("filter", true).apply();
            if (resultCode == RESULT_OK) {
                ServiceSinkhole.start("prepared", this);

                Toast on = Toast.makeText(ActivityMain.this, R.string.msg_on, Toast.LENGTH_LONG);
                on.setGravity(Gravity.CENTER, 0, 0);
                on.show();

                checkDoze();
            } else if (resultCode == RESULT_CANCELED)
                Toast.makeText(this, R.string.msg_vpn_cancelled, Toast.LENGTH_LONG).show();


        } else if (requestCode == REQUEST_LOGCAT) {
            // Send logcat by e-mail
            if (resultCode == RESULT_OK) {
                Uri target = data.getData();
                if (data.hasExtra("org.openintents.extra.DIR_PATH"))
                    target = Uri.parse(target + "/logcat.txt");
                Log.i(TAG, "Export URI=" + target);
                Util.sendLogcat(target, this);
            }
        }else if (requestCode == REQUEST_PCAP){
            if (resultCode == RESULT_OK && data != null){
                handleExportPCAP(data);
            }
        } else {
            Log.w(TAG, "Unknown activity result request=" + requestCode);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ROAMING)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ServiceSinkhole.reload("permission granted", this, false);
    }

    private BroadcastReceiver onRulesChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received " + intent);
            Util.logExtras(intent);
        }
    };

    private BroadcastReceiver onQueueChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received " + intent);
            Util.logExtras(intent);
        }
    };

    private BroadcastReceiver packageChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received " + intent);
            Util.logExtras(intent);
        }
    };

    private void checkExtras(Intent intent) {
        // Approve request
        if (intent.hasExtra(EXTRA_APPROVE)) {
            Log.i(TAG, "Requesting VPN approval");
            //swEnabled.toggle();
            activateVpn(!isVpnActivated());
        }

        if (intent.hasExtra(EXTRA_LOGCAT)) {
            Log.i(TAG, "Requesting logcat");
            Intent logcat = getIntentLogcat();
            if (logcat.resolveActivity(getPackageManager()) != null)
                startActivityForResult(logcat, REQUEST_LOGCAT);
        }
    }

    private void checkDoze() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final Intent doze = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            if (Util.batteryOptimizing(this) && getPackageManager().resolveActivity(doze, 0) != null) {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                if (!prefs.getBoolean("nodoze", false)) {
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View view = inflater.inflate(R.layout.doze, null, false);
                    final CheckBox cbDontAsk = view.findViewById(R.id.cbDontAsk);
                    dialogDoze = new AlertDialog.Builder(this)
                            .setView(view)
                            .setCancelable(true)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    prefs.edit().putBoolean("nodoze", cbDontAsk.isChecked()).apply();
                                    startActivity(doze);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    prefs.edit().putBoolean("nodoze", cbDontAsk.isChecked()).apply();
                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    dialogDoze = null;
                                    checkDataSaving();
                                }
                            })
                            .create();
                    dialogDoze.show();
                } else
                    checkDataSaving();
            } else
                checkDataSaving();
        }
    }

    private void checkDataSaving() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final Intent settings = new Intent(
                    Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            if (Util.dataSaving(this) && getPackageManager().resolveActivity(settings, 0) != null) {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                if (!prefs.getBoolean("nodata", false)) {
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View view = inflater.inflate(R.layout.datasaving, null, false);
                    final CheckBox cbDontAsk = view.findViewById(R.id.cbDontAsk);
                    dialogDoze = new AlertDialog.Builder(this)
                            .setView(view)
                            .setCancelable(true)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    prefs.edit().putBoolean("nodata", cbDontAsk.isChecked()).apply();
                                    startActivity(settings);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    prefs.edit().putBoolean("nodata", cbDontAsk.isChecked()).apply();
                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    dialogDoze = null;
                                }
                            })
                            .create();
                    dialogDoze.show();
                }
            }
        }
    }

    private Intent getIntentLogcat() {
        Intent intent;
        intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "logcat.txt");
        return intent;
    }

    public void enablePcap(File file){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        prefs.edit().putBoolean("pcap", true).apply();
        ServiceSinkhole.setPcap(true, file, this);
    }

    public void clearPcapLog(File file){
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final File pcap_file = file;

        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... objects) {
                DatabaseHelper.getInstance(ActivityMain.this).clearLog(-1);
                if (prefs.getBoolean("pcap", false)) {
                    ServiceSinkhole.setPcap(false,pcap_file,ActivityMain.this);
                    if (pcap_file.exists() && !pcap_file.delete())
                        Log.w(TAG, "Delete PCAP failed");
                    ServiceSinkhole.setPcap(true,pcap_file,ActivityMain.this);
                } else {
                    if (pcap_file.exists() && !pcap_file.delete())
                        Log.w(TAG, "Delete PCAP failed");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                if (running){
                    //updateAdapter();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void exportPcap(){
        startActivityForResult(getIntentPCAPDocument(), REQUEST_PCAP);
    }

    private Intent getIntentPCAPDocument() {
        Intent intent;
        intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_TITLE, "netguard_" + new SimpleDateFormat("yyyyMMdd").format(new Date().getTime()) + ".pcap");
        return intent;
    }

    private void handleExportPCAP(final Intent data) {
        new AsyncTask<Object, Object, Throwable>() {
            @Override
            protected Throwable doInBackground(Object... objects) {
                OutputStream out = null;
                FileInputStream in = null;
                try {
                    // Stop capture
                    ServiceSinkhole.setPcap(false,null, ActivityMain.this);

                    Uri target = data.getData();
                    if (data.hasExtra("org.openintents.extra.DIR_PATH"))
                        target = Uri.parse(target + "/netguard.pcap");
                    Log.i(TAG, "Export PCAP URI=" + target);
                    out = getContentResolver().openOutputStream(target);

                    File pcap = new File(getDir("data", MODE_PRIVATE), "netguard.pcap");
                    in = new FileInputStream(pcap);

                    int len;
                    long total = 0;
                    byte[] buf = new byte[4096];
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                        total += len;
                    }
                    Log.i(TAG, "Copied bytes=" + total);

                    return null;
                } catch (Throwable ex) {
                    Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                    return ex;
                } finally {
                    if (out != null)
                        try {
                            out.close();
                        } catch (IOException ex) {
                            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                        }
                    if (in != null)
                        try {
                            in.close();
                        } catch (IOException ex) {
                            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                        }

                    // Resume capture
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ActivityMain.this);
                    if (prefs.getBoolean("pcap", false))
                        ServiceSinkhole.setPcap(true,null, ActivityMain.this);
                }
            }

            @Override
            protected void onPostExecute(Throwable ex) {
                if (ex == null)
                    Toast.makeText(ActivityMain.this, R.string.msg_completed, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(ActivityMain.this, ex.toString(), Toast.LENGTH_LONG).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public boolean isVpnActivated(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("enabled",false);
    }

    // Exported functionality from the previous switch.
    public void activateVpn(boolean enabled){
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(enabled){
            String alwaysOn = Settings.Secure.getString(getContentResolver(), "always_on_vpn_app");
            Log.i(TAG, "Always-on=" + alwaysOn);
            if (!TextUtils.isEmpty(alwaysOn))
                if (getPackageName().equals(alwaysOn)) {
                    if (prefs.getBoolean("filter", false)) {
                        int lockdown = Settings.Secure.getInt(getContentResolver(), "always_on_vpn_lockdown", 0);
                        Log.i(TAG, "Lockdown=" + lockdown);
                        if (lockdown != 0) {
                            Toast.makeText(ActivityMain.this, R.string.msg_always_on_lockdown, Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(ActivityMain.this, R.string.msg_always_on, Toast.LENGTH_LONG).show();
                    return;
                }

            String dns_mode = Settings.Global.getString(getContentResolver(), "private_dns_mode");
            Log.i(TAG, "Private DNS mode=" + dns_mode);
            if (dns_mode == null)
                dns_mode = "off";
            if (!"off".equals(dns_mode)) {
                Toast.makeText(ActivityMain.this, R.string.msg_private_dns, Toast.LENGTH_LONG).show();
                return;
            }

            try {
                final Intent prepare = VpnService.prepare(ActivityMain.this);
                if (prepare == null) {
                    Log.i(TAG, "Prepare done");
                    onActivityResult(REQUEST_VPN, RESULT_OK, null);
                } else {
                    // Show dialog
                    LayoutInflater inflater = LayoutInflater.from(ActivityMain.this);
                    View view = inflater.inflate(R.layout.vpn, null, false);
                    dialogVpn = new AlertDialog.Builder(ActivityMain.this)
                            .setView(view)
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (running) {
                                        Log.i(TAG, "Start intent=" + prepare);
                                        try {
                                            // com.android.vpndialogs.ConfirmDialog required
                                            startActivityForResult(prepare, REQUEST_VPN);
                                        } catch (Throwable ex) {
                                            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                                            onActivityResult(REQUEST_VPN, RESULT_CANCELED, null);
                                            prefs.edit().putBoolean("enabled", false).apply();
                                        }
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
            } catch (Throwable ex) {
                // Prepare failed
                Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                prefs.edit().putBoolean("enabled", false).apply();
            }

        } else{
            ServiceSinkhole.stop("switch off", ActivityMain.this, false);
        }
    }

    public boolean isNetworkLocked(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("lockdown",false);
    }

    public void lockNetwork(boolean locked){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("lockdown",locked).apply();
        ServiceSinkhole.reload("changed lockdown", this, false);
        //WidgetLockdown.updateWidgets(this);
    }

    public boolean isLogging(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("log",false);

    }

    public void logging(boolean enabled) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("log",enabled).apply();
        ServiceSinkhole.reload("changed logging", this, false);

    }

    public boolean isLoggingApp(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("log_app",false);
    }

    public void appLogging(boolean enabled){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("log_app",enabled).apply();
    }

    public boolean isUsageTracked(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("track_usage",false) && isLoggingApp();
    }

    public void trackUsage(boolean enabled){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("track_usage", enabled).apply();
        ServiceSinkhole.reload("changed tracking", this, false);
    }

    // Unused
    public Map<String, Boolean> loggedPackets(){
        Map<String, Boolean> booleanMap = new HashMap<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        booleanMap.put("proto_udp",prefs.getBoolean("proto_udp",false));
        booleanMap.put("proto_tcp",prefs.getBoolean("proto_tcp",false));
        booleanMap.put("proto_other",prefs.getBoolean("proto_other",false));
        booleanMap.put("traffic_allowed",prefs.getBoolean("traffic_allowed",false));
        booleanMap.put("traffic_blocked",prefs.getBoolean("traffic_blocked",false));

        return booleanMap;
    }

    // Unused
    public void packetLogging(boolean udp, boolean tcp, boolean other,
                              boolean traffic_allowed, boolean traffic_blocked){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("proto_udp",udp).apply();
        prefs.edit().putBoolean("proto_tcp",tcp).apply();
        prefs.edit().putBoolean("proto_other",other).apply();
        prefs.edit().putBoolean("traffic_allowed",traffic_allowed).apply();
        prefs.edit().putBoolean("traffic_blocked",traffic_blocked).apply();
        ServiceSinkhole.reload("changed packet logging", this, false);
    }

    public boolean isFlowCollected(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("collect_flow",false);
    }

    public void collectFlow(boolean enabled){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("collect_flow", enabled).apply();
        ServiceSinkhole.reload("changed flow capturing", this, false);
    }
}
