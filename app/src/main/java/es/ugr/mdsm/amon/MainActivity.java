package es.ugr.mdsm.amon;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import es.ugr.mdsm.deviceInfo.Rule;
import es.ugr.mdsm.deviceInfo.VpnActivity;
import es.ugr.mdsm.restDump.DbDumper;

public class MainActivity extends VpnActivity {

    private ImageButton imgSwitch;
    private TextView textSwitch;
    private DbDumper dbDumper = null;
    private AlertDialog dialogFirst;
    private AlertDialog dialogBattery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        imgSwitch = findViewById(R.id.swEnabled);
        textSwitch = findViewById(R.id.textEnabled);
        Update.createNotificationChannel(this);

        //Document doc = ManifestParser.extractManifest(Software.getInstalledApplication(this).get(0));
        // Connection.bluetoothBondedDevices();

        // Check for updates
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(UpdateWorker.class,24,TimeUnit.HOURS).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("updateWorker", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);

        // Activate VPN and dumping
        setupVpn(MODE_FLOW);
        anonymizeData(true);

        dbDumper = new DbDumper(this);

        activateVpn(true);
        setWatchdog(5);

        boolean enabled = isVpnEnabled();

        periodicDump(enabled);
        dbDumper.dumpDeviceInfo();
        dbDumper.dumpAppInfo();
        updateSwitch(enabled);

        /*
        // Manage Firewall
        try {
            clearRules();
            int appId = getPackageManager().getApplicationInfo("com.android.chrome", 0).uid;
            ArrayList<Rule> rules = new ArrayList<>();
            rules.add(new Rule(4, Rule.TCP, "www.google.com", 443, appId, true));
            addRule(rules);
            filter(true);
            whitelistFilter(true);
            loadRules();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        */

        // Update UI
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateVpn(!isVpnEnabled());
            }
        };

        imgSwitch.setOnClickListener(listener);
        textSwitch.setOnClickListener(listener);

        // Handle battery optimizations
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean initialized = prefs.getBoolean("initialized", false);

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if(!pm.isIgnoringBatteryOptimizations(getPackageName())){
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.battery_optimization, null, false);

            TextView tvBattery = view.findViewById(R.id.tvBattery);
            tvBattery.setMovementMethod(LinkMovementMethod.getInstance());

            // Show dialog
            dialogBattery = new AlertDialog.Builder(this)
                    .setView(view)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, BatteryActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            dialogBattery = null;
                        }
                    })
                    .create();
        }


        // Show first time dialog
        if(!initialized){
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.first, null, false);

            TextView tvFirst = view.findViewById(R.id.tvFirst);
            tvFirst.setMovementMethod(LinkMovementMethod.getInstance());
            // Show dialog
            dialogFirst = new AlertDialog.Builder(this)
                    .setView(view)
                    .setCancelable(false)
                    .setPositiveButton(R.string.app_agree, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            prefs.edit().putBoolean("initialized", true).apply();
                            dialogBattery.show();
                        }
                    })
                    .setNegativeButton(R.string.app_disagree, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbDumper.onDestroy();
        if (dialogFirst != null) {
            dialogFirst.dismiss();
            dialogFirst = null;
        }
        if (dialogBattery != null) {
            dialogBattery.dismiss();
            dialogBattery = null;
        }
    }

    private void updateSwitch(boolean enabled){
        imgSwitch.setImageResource(enabled ? R.drawable.icon : R.drawable.icon_grayscale);
        textSwitch.setText(enabled ? R.string.textSwitchOn : R.string.textSwitchOff);
    }

    private void periodicDump(boolean enabled){
        if(enabled){
            dbDumper.dumpFlowInfo(DbDumper.DEFAULT_INTERVAL);
            dbDumper.dumpSensorInfo(DbDumper.DEFAULT_INTERVAL);
            dbDumper.dumpConnectionInfo(DbDumper.DEFAULT_INTERVAL);
        }else{
            dbDumper.stop();
        }
    }

    @Override
    protected void onStateUpdated(){
        super.onStateUpdated();

        boolean enabled = isVpnEnabled();
        periodicDump(enabled);
        updateSwitch(enabled);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.battery_menu:
                startActivity(new Intent(this, BatteryActivity.class));
                return true;
            case R.id.about_menu:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
