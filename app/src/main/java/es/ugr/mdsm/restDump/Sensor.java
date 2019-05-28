package es.ugr.mdsm.restDump;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sensor {

    @SerializedName("connectivity")
    @Expose
    private Connectivity connectivity;
    @SerializedName("stat")
    @Expose
    private Stat stat;
    @SerializedName("security")
    @Expose
    private Security security;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    /**
     * No args constructor for use in serialization
     *
     */
    public Sensor() {
    }

    /**
     *
     * @param timestamp
     * @param connectivity
     * @param security
     * @param stat
     */
    public Sensor(Connectivity connectivity, Stat stat, Security security, String timestamp) {
        super();
        this.connectivity = connectivity;
        this.stat = stat;
        this.security = security;
        this.timestamp = timestamp;
    }

    public Connectivity getConnectivity() {
        return connectivity;
    }

    public void setConnectivity(Connectivity connectivity) {
        this.connectivity = connectivity;
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "connectivity=" + connectivity +
                ", stat=" + stat +
                ", security=" + security +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}