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
    @SerializedName("mac")
    @Expose
    private String mac;
    @SerializedName("timestamp")
    @Expose
    private Long timestamp;

    /**
     * No args constructor for use in serialization
     *
     */
    public Sensor() {
    }

    /**
     *
     * @param mac
     * @param timestamp
     * @param connectivity
     * @param security
     * @param stat
     */
    public Sensor(Connectivity connectivity, Stat stat, Security security, String mac, Long timestamp) {
        super();
        this.connectivity = connectivity;
        this.stat = stat;
        this.security = security;
        this.mac = mac;
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

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "connectivity=" + connectivity +
                ", stat=" + stat +
                ", security=" + security +
                ", mac='" + mac + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}