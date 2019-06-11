package es.ugr.mdsm.restDump;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Flow {

    @SerializedName("mac")
    @Expose
    private String mac;
    @SerializedName("flows")
    @Expose
    private List<Flow_> flows = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Flow() {
    }

    /**
     *
     * @param mac
     * @param flows
     */
    public Flow(String mac, List<Flow_> flows) {
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

    public List<Flow_> getFlows() {
        return flows;
    }

    public void setFlows(List<Flow_> flows) {
        this.flows = flows;
    }

    @Override
    public String toString() {
        return "Flow{" +
                "mac='" + mac + '\'' +
                ", flows=" + flows.toString() +
                '}';
    }
}