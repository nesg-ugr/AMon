package es.ugr.mdsm.restDump;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class FlowDump {

    @SerializedName("mac")
    @Expose
    private String mac;
    @SerializedName("flows")
    @Expose
    private List<Flow> flows = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public FlowDump() {
    }

    /**
     *
     * @param mac
     * @param flows
     */
    public FlowDump(String mac, List<Flow> flows) {
        super();
        this.mac = mac;
        this.flows = flows;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public List<Flow> getFlows() {
        return flows;
    }

    public void setFlows(List<Flow> flows) {
        this.flows = flows;
    }

    @Override
    public String toString() {
        return "FlowDump{" +
                "mac='" + mac + '\'' +
                ", flows=" + flows.toString() +
                '}';
    }
}