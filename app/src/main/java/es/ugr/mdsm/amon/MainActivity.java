package es.ugr.mdsm.amon;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.appcompat.widget.Toolbar;

import es.ugr.mdsm.deviceInfo.VpnActivity;
import es.ugr.mdsm.restDump.DbDumper;
import eu.faircode.netguard.R;
import eu.faircode.netguard.Util;

public class MainActivity extends VpnActivity {

    private ImageButton imgSwitch;
    private TextView textSwitch;
    private DbDumper dbDumper = null;
    private AlertDialog dialogFirst;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setupVpn(MODE_FLOW);

        dbDumper = new DbDumper(this);

        imgSwitch = findViewById(R.id.swEnabled);
        textSwitch = findViewById(R.id.textEnabled);

        activateVpn(true);
        setWatchdog(5);

        boolean enabled = isVpnEnabled();

        updateDump(enabled);
        dbDumper.dumpDeviceInfo();
        dbDumper.dumpAppInfo();
        updateSwitch(enabled);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateVpn(!isVpnEnabled());
            }
        };

        imgSwitch.setOnClickListener(listener);
        textSwitch.setOnClickListener(listener);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean initialized = prefs.getBoolean("initialized", false);

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

    private void updateSwitch(boolean enabled){
        imgSwitch.setImageResource(enabled ? R.drawable.icon : R.drawable.icon_grayscale);
        textSwitch.setText(enabled ? R.string.textSwitchOn : R.string.textSwitchOff);
    }

    private void updateDump(boolean enabled){
        if(enabled){
            dbDumper.start();
        }else{
            dbDumper.stop();
        }
    }

    @Override
    protected void onStateUpdated(){
        super.onStateUpdated();

        boolean enabled = isVpnEnabled();
        updateDump(enabled);
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
                Intent intent = new Intent(this, BatteryActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
