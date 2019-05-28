package es.ugr.mdsm.restDump;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device {

    @SerializedName("mac")
    @Expose
    private String mac;
    @SerializedName("build")
    @Expose
    private Build build;
    @SerializedName("specification")
    @Expose
    private Specification specification;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    /**
     * No args constructor for use in serialization
     *
     */
    public Device() {
    }

    /**
     *
     * @param timestamp
     * @param specification
     * @param mac
     * @param build
     */
    public Device(String mac, Build build, Specification specification, String timestamp) {
        super();
        this.mac = mac;
        this.build = build;
        this.specification = specification;
        this.timestamp = timestamp;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Build getBuild() {
        return build;
    }

    public void setBuild(Build build) {
        this.build = build;
    }

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Device{" +
                "mac='" + mac + '\'' +
                ", build=" + build +
                ", specification=" + specification +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
