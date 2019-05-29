package es.ugr.mdsm.restDump;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stat {

    @SerializedName("cpuUsage")
    @Expose
    private Float cpuUsage;
    @SerializedName("ramUsage")
    @Expose
    private Long ramUsage;
    @SerializedName("batteryLevel")
    @Expose
    private Integer batteryLevel;

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
    public Stat(Float cpuUsage, Long ramUsage, Integer batteryLevel) {
        super();
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
        this.batteryLevel = batteryLevel;
    }

    public Float getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Float cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Long getRamUsage() {
        return ramUsage;
    }

    public void setRamUsage(Long ramUsage) {
        this.ramUsage = ramUsage;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
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
