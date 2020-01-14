package es.ugr.mdsm.amon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class BatteryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView noStepText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        noStepText = findViewById(R.id.noStep);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        updateSteps();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateSteps();
    }

    private void updateSteps(){
        ArrayList<BatteryStep> batterySteps = stepsByManufacturer();
        adapter = new BatteryAdapter(batterySteps);
        recyclerView.setAdapter(adapter);
        if (batterySteps.size() == 0){
            noStepText.setVisibility(View.VISIBLE);
        }else{
            noStepText.setVisibility(View.GONE);
        }
    }

    public ArrayList<BatteryStep> stepsByManufacturer() {
        // TODO: Modify added step by manufacturer using if's
        int initialStep = 0;
        ArrayList<BatteryStep> batterySteps = new ArrayList<>();

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if(!pm.isIgnoringBatteryOptimizations(getPackageName())){
            batterySteps.add(new BatteryStep(
                    getString(R.string.step, ++initialStep),
                    getString(R.string.step_doze),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                            if(getPackageManager().resolveActivity(intent, 0) != null){
                                startActivity(intent);
                            }
                        }
                    }
            ));
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (cm.getRestrictBackgroundStatus() == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED){
                batterySteps.add(new BatteryStep(
                        getString(R.string.step, ++initialStep),
                        getString(R.string.step_data),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS);
                                intent.setData(Uri.fromParts("package", getPackageName(), null));
                                if(getPackageManager().resolveActivity(intent, 0) != null){
                                    startActivity(intent);
                                }
                            }
                        }
                ));
            }
        }

        String manufacturer = Build.MANUFACTURER;

        if(manufacturer.equalsIgnoreCase("Xiaomi")){
            batterySteps.add(new BatteryStep(
                        getString(R.string.step, ++initialStep),
                        getString(R.string.step_description_batterySaving),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.fromParts("package", getPackageName(), null));
                                if(getPackageManager().resolveActivity(intent, 0) != null){
                                    startActivity(intent);
                                }                            }
                        }));

            batterySteps.add(new BatteryStep(
                    getString(R.string.step, ++initialStep),
                    getString(R.string.step_description_autostart),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                            if(getPackageManager().resolveActivity(intent, 0) != null){
                                startActivity(intent);
                            }                        }
                    })
            );

            batterySteps.add(new BatteryStep(
                    getString(R.string.step, ++initialStep),
                    getString(R.string.step_description_lock)
            ));
        }

        return batterySteps;
    }
}
