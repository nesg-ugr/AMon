package es.ugr.mdsm.restDump;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Connection {

    @SerializedName("mac")
    @Expose
    public String mac;
    @SerializedName("timestamp")
    @Expose
    public Long timestamp;
    @SerializedName("wifi")
    @Expose
    public List<Wifi> wifi = null;
    @SerializedName("bluetooth")
    @Expose
    public List<String> bluetooth = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Connection() {
    }

    /**
     *
     * @param timestamp
     * @param bluetooth
     * @param wifi
     * @param mac
     */
    public Connection(String mac, Long timestamp, List<Wifi> wifi, List<String> bluetooth) {
        super();
        this.mac = mac;
        this.timestamp = timestamp;
        this.wifi = wifi;
        this.bluetooth = bluetooth;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "mac='" + mac + '\'' +
                ", timestamp=" + timestamp +
                ", wifi=" + wifi +
                ", bluetooth=" + bluetooth +
                '}';
    }
}