package es.ugr.mdsm.restDump;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stat {

    @SerializedName("cpuUsage")
    @Expose
    private Double cpuUsage;
    @SerializedName("ramUsage")
    @Expose
    private Double ramUsage;
    @SerializedName("batteryLevel")
    @Expose
    private Double batteryLevel;

    /**
     * No args constructor for use in serialization
     *
     */
    public Stat() {
    }

    /**
     *
     * @param ramUsage
     * @param batteryLevel
     * @param cpuUsage
     */
    public Stat(Double cpuUsage, Double ramUsage, Double batteryLevel) {
        super();
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
        this.batteryLevel = batteryLevel;
    }

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Double getRamUsage() {
        return ramUsage;
    }

    public void setRamUsage(Double ramUsage) {
        this.ramUsage = ramUsage;
    }

    public Double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    @Override
    public String toString() {
        return "Stat{" +
                "cpuUsage=" + cpuUsage +
                ", ramUsage=" + ramUsage +
                ", batteryLevel=" + batteryLevel +
                '}';
    }
}
