package es.ugr.mdsm.restDump;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Connectivity {

    @SerializedName("data")
    @Expose
    private Boolean data;
    @SerializedName("data_tech")
    @Expose
    private Integer dataTech;
    @SerializedName("roaming")
    @Expose
    private Boolean roaming;
    @SerializedName("wifi")
    @Expose
    private Boolean wifi;
    @SerializedName("airplane")
    @Expose
    private Boolean airplane;
    @SerializedName("bluetooth")
    @Expose
    private Boolean bluetooth;
    @SerializedName("nfc")
    @Expose
    private Boolean nfc;
    @SerializedName("gps")
    @Expose
    private Boolean gps;
    @SerializedName("vpn")
    @Expose
    private Boolean vpn;
    @SerializedName("usb")
    @Expose
    private Boolean usb;

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
     * @param bluetooth
     * @param gps
     */
    public Connectivity(Boolean data, Integer dataTech, Boolean roaming, Boolean wifi, Boolean airplane, Boolean bluetooth, Boolean nfc, Boolean gps, Boolean vpn, Boolean usb) {
        super();
        this.data = data;
        this.dataTech = dataTech;
        this.roaming = roaming;
        this.wifi = wifi;
        this.airplane = airplane;
        this.bluetooth = bluetooth;
        this.nfc = nfc;
        this.gps = gps;
        this.vpn = vpn;
        this.usb = usb;
    }

    public Boolean getData() {
        return data;
    }

    public void setData(Boolean data) {
        this.data = data;
    }

    public Integer getDataTech() {
        return dataTech;
    }

    public void setDataTech(Integer dataTech) {
        this.dataTech = dataTech;
    }

    public Boolean getRoaming() {
        return roaming;
    }

    public void setRoaming(Boolean roaming) {
        this.roaming = roaming;
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

    public Boolean getBluetooth() {
        return bluetooth;
    }

    public void setBluetooth(Boolean bluetooth) {
        this.bluetooth = bluetooth;
    }

    public Boolean getNfc() {
        return nfc;
    }

    public void setNfc(Boolean nfc) {
        this.nfc = nfc;
    }

    public Boolean getGps() {
        return gps;
    }

    public void setGps(Boolean gps) {
        this.gps = gps;
    }

    public Boolean getVpn() {
        return vpn;
    }

    public void setVpn(Boolean vpn) {
        this.vpn = vpn;
    }

    public Boolean getUsb() {
        return usb;
    }

    public void setUsb(Boolean usb) {
        this.usb = usb;
    }

    @Override
    public String toString() {
        return "Connectivity{" +
                "data=" + data +
                ", dataTech=" + dataTech +
                ", roaming=" + roaming +
                ", wifi=" + wifi +
                ", airplane=" + airplane +
                ", bluetooth=" + bluetooth +
                ", nfc=" + nfc +
                ", gps=" + gps +
                ", vpn=" + vpn +
                ", usb=" + usb +
                '}';
    }
}