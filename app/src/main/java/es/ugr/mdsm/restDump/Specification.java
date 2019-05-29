package es.ugr.mdsm.restDump;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Specification {

    @SerializedName("cpuCores")
    @Expose
    private Integer cpuCores;
    @SerializedName("ramTotal")
    @Expose
    private Long ramTotal;
    @SerializedName("batteryTotal")
    @Expose
    private Integer batteryTotal;

    /**
     * No args constructor for use in serialization
     *
     */
    public Specification() {
    }

    /**
     *
     * @param cpuCores
     * @param ramTotal
     * @param batteryTotal
     */
    public Specification(Integer cpuCores, Long ramTotal, Integer batteryTotal) {
        super();
        this.cpuCores = cpuCores;
        this.ramTotal = ramTotal;
        this.batteryTotal = batteryTotal;
    }

    public Integer getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(Integer cpuCores) {
        this.cpuCores = cpuCores;
    }

    public Long getRamTotal() {
        return ramTotal;
    }

    public void setRamTotal(Long ramTotal) {
        this.ramTotal = ramTotal;
    }

    public Integer getBatteryTotal() {
        return batteryTotal;
    }

    public void setBatteryTotal(Integer batteryTotal) {
        this.batteryTotal = batteryTotal;
    }

    @Override
    public String toString() {
        return "Specification{" +
                "cpuCores=" + cpuCores +
                ", ramTotal=" + ramTotal +
                ", batteryTotal=" + batteryTotal +
                '}';
    }
}