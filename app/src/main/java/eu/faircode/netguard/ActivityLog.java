package eu.faircode.netguard;

/*
    This file is part of NetGuard.

    NetGuard is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    NetGuard is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NetGuard.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2015-2019 by Marcel Bokhorst (M66B)
*/

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NavUtils;

public class ActivityLog extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "NetGuard.Log";

    private boolean running = false;

    private static final int REQUEST_PCAP = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logging);
        running = true;

        // Action bar
        View actionView = getLayoutInflater().inflate(R.layout.actionlog, null, false);
        SwitchCompat swEnabled = actionView.findViewById(R.id.swEnabled);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(actionView);

        getSupportActionBar().setTitle(R.string.menu_log);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get settings
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean log = prefs.getBoolean("log", false);

        // Set enabled switch
        swEnabled.setChecked(log);
        swEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean("log", isChecked).apply();
            }
        });

        // Listen for preference changes
        prefs.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onDestroy() {
        running = false;
        //adapter = null;
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String name) {
        Log.i(TAG, "Preference " + name + "=" + prefs.getAll().get(name));
        if ("log".equals(name)) {
            // Get enabled
            boolean log = prefs.getBoolean(name, false);

            // Display disabled warning
            TextView tvDisabled = findViewById(R.id.tvDisabled);
            tvDisabled.setVisibility(log ? View.GONE : View.VISIBLE);

            // Check switch state
            SwitchCompat swEnabled = getSupportActionBar().getCustomView().findViewById(R.id.swEnabled);
            if (swEnabled.isChecked() != log)
                swEnabled.setChecked(log);

            ServiceSinkhole.reload("changed " + name, ActivityLog.this, false);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final File pcap_file = new File(getDir("data", MODE_PRIVATE), "netguard.pcap");

        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i(TAG, "Up");
                NavUtils.navigateUpFromSameTask(this);
                return true;
/*
            // Ported
            case R.id.menu_pcap_enabled:
                item.setChecked(!item.isChecked());
                prefs.edit().putBoolean("pcap", item.isChecked()).apply();
                ServiceSinkhole.setPcap(item.isChecked(), ActivityLog.this);
                return true;

            case R.id.menu_pcap_export:
                startActivityForResult(getIntentPCAPDocument(), REQUEST_PCAP);
                return true;

            case R.id.menu_log_clear:
                new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... objects) {
                        DatabaseHelper.getInstance(ActivityLog.this).clearLog(-1);
                        if (prefs.getBoolean("pcap", false)) {
                            ServiceSinkhole.setPcap(false, ActivityLog.this);
                            if (pcap_file.exists() && !pcap_file.delete())
                                Log.w(TAG, "Delete PCAP failed");
                            ServiceSinkhole.setPcap(true, ActivityLog.this);
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
                return true;
*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // ported
    private Intent getIntentPCAPDocument() {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            if (Util.isPackageInstalled("org.openintents.filemanager", this)) {
                intent = new Intent("org.openintents.action.PICK_DIRECTORY");
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=org.openintents.filemanager"));
            }
        } else {
            intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/octet-stream");
            intent.putExtra(Intent.EXTRA_TITLE, "netguard_" + new SimpleDateFormat("yyyyMMdd").format(new Date().getTime()) + ".pcap");
        }
        return intent;
    }

    // ported
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.i(TAG, "onActivityResult request=" + requestCode + " result=" + requestCode + " ok=" + (resultCode == RESULT_OK));

        if (requestCode == REQUEST_PCAP) {
            if (resultCode == RESULT_OK && data != null)
                handleExportPCAP(data);

        } else {
            Log.w(TAG, "Unknown activity result request=" + requestCode);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // ported
    private void handleExportPCAP(final Intent data) {
        new AsyncTask<Object, Object, Throwable>() {
            @Override
            protected Throwable doInBackground(Object... objects) {
                OutputStream out = null;
                FileInputStream in = null;
                try {
                    // Stop capture
                    ServiceSinkhole.setPcap(false,null, ActivityLog.this);

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
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ActivityLog.this);
                    if (prefs.getBoolean("pcap", false))
                        ServiceSinkhole.setPcap(true,null, ActivityLog.this);
                }
            }

            @Override
            protected void onPostExecute(Throwable ex) {
                if (ex == null)
                    Toast.makeText(ActivityLog.this, R.string.msg_completed, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(ActivityLog.this, ex.toString(), Toast.LENGTH_LONG).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
