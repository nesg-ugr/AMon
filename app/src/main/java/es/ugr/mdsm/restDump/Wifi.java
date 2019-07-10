package es.ugr.mdsm.restDump;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wifi {

    @SerializedName("ssid")
    @Expose
    public String ssid;
    @SerializedName("security")
    @Expose
    public String security;

    /**
     * No args constructor for use in serialization
     *
     */
    public Wifi() {
    }

    /**
     *
     * @param ssid
     * @param security
     */
    public Wifi(String ssid, String security) {
        super();
        this.ssid = ssid;
        this.security = security;
    }

    @Override
    public String toString() {
        return "Wifi{" +
                "SSID='" + ssid + '\'' +
                ", security='" + security + '\'' +
                '}';
    }
}