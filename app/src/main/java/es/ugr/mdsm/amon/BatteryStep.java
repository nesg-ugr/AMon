package es.ugr.mdsm.amon;

import android.os.Build;
import android.view.View;

import java.util.ArrayList;

public class BatteryStep {

    private String step;
    private String description;
    private View.OnClickListener listener;

    public BatteryStep(String step, String description, View.OnClickListener listener) {
        this.step = step;
        this.description = description;
        this.listener = listener;
    }

    public BatteryStep(String step, String description) {
        this.step = step;
        this.description = description;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

}
