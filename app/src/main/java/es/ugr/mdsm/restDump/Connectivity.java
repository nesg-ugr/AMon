package es.ugr.mdsm.restDump;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Connectivity {

    @SerializedName("data")
    @Expose
    private Boolean data;
    @SerializedName("wifi")
    @Expose
    private Boolean wifi;
    @SerializedName("airplane")
    @Expose
    private Boolean airplane;
    @SerializedName("gps")
    @Expose
    private Boolean gps;

    /**
     * No args constructor for use in serialization
     *
     */
    public Connectivity() {
    }

    /**
     *
     * @param wifi
     * @param airplane
     * @param data
     * @param gps
     */
    public Connectivity(Boolean data, Boolean wifi, Boolean airplane, Boolean gps) {
        super();
        this.data = data;
        this.wifi = wifi;
        this.airplane = airplane;
        this.gps = gps;
    }

    public Boolean getData() {
        return data;
    }

    public void setData(Boolean data) {
        this.data = data;
    }

    public Boolean getWifi() {
        return wifi;
    }

    public void setWifi(Boolean wifi) {
        this.wifi = wifi;
    }

    public Boolean getAirplane() {
        return airplane;
    }

    public void setAirplane(Boolean airplane) {
        this.airplane = airplane;
    }

    public Boolean getGps() {
        return gps;
    }

    public void setGps(Boolean gps) {
        this.gps = gps;
    }

    @Override
    public String toString() {
        return "Connectivity{" +
                "data=" + data +
                ", wifi=" + wifi +
                ", airplane=" + airplane +
                ", gps=" + gps +
                '}';
    }
}