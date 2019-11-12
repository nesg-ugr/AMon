package es.ugr.mdsm.amon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class BatteryAdapter extends RecyclerView.Adapter<BatteryAdapter.BatteryViewHolder> {

    private ArrayList<BatteryStep> stepArrayList;

    public BatteryAdapter(ArrayList<BatteryStep> stepArrayList) {
        this.stepArrayList = stepArrayList;
    }

    public static class BatteryViewHolder extends RecyclerView.ViewHolder{

        private TextView step;
        private TextView description;
        private Button goButton;

        public BatteryViewHolder(View v){
            super(v);
            step = v.findViewById(R.id.step);
            description = v.findViewById(R.id.description);
            goButton = v.findViewById(R.id.go_button);
        }

        public void assign(BatteryStep batteryStep){
            step.setText(batteryStep.getStep());
            description.setText(batteryStep.getDescription());
            View.OnClickListener listener = batteryStep.getListener();
            if (listener != null){
                goButton.setOnClickListener(listener);
            } else {
                goButton.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    @Override
    public BatteryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_battery, parent, false);
        return new BatteryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BatteryViewHolder holder, int position) {
        holder.assign(stepArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return stepArrayList.size();
    }
}
