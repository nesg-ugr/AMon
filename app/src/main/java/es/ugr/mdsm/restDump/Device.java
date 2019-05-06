package es.ugr.mdsm.restDump;
import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device {

    @SerializedName("mac")
    @Expose
    private String mac;
    @SerializedName("sdk")
    @Expose
    private Integer sdk;

    /**
     *
     * @param mac MAC address of the device
     * @param sdk SDK of the Android Build
     */
    Device(String mac, Integer sdk) {
        super();
        this.mac = mac;
        this.sdk = sdk;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Integer getSdk() {
        return sdk;
    }

    public void setSdk(Integer sdk) {
        this.sdk = sdk;
    }

    @Override
    public String toString() {
        return "Device{" +
                "mac='" + mac + '\'' +
                ", sdk=" + sdk +
                '}';
    }
}
