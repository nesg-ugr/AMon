package es.ugr.mdsm.restDump;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdditionalData {

    @SerializedName("bluetooth")
    @Expose
    public List<Bluetooth> bluetooth = null;
    @SerializedName("usb")
    @Expose
    public List<Usb> usb = null;
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
    public AdditionalData() {
    }

    /**
     *
     * @param bluetooth
     * @param usb
     */
    public AdditionalData(List<Bluetooth> bluetooth, List<Usb> usb, String mac, Long timestamp) {
        super();
        this.bluetooth = bluetooth;
        this.usb = usb;
        this.mac = mac;
        this.timestamp = timestamp;
    }

    public List<Bluetooth> getBluetooth() {
        return bluetooth;
    }

    public void setBluetooth(List<Bluetooth> bluetooth) {
        this.bluetooth = bluetooth;
    }

    public List<Usb> getUsb() {
        return usb;
    }

    public void setUsb(List<Usb> usb) {
        this.usb = usb;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "AdditionalData{" +
                "bluetooth=" + bluetooth +
                ", usb=" + usb +
                ", mac='" + mac + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}