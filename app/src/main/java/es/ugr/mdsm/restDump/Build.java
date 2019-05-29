package es.ugr.mdsm.restDump;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Build {

    @SerializedName("manufacturer")
    @Expose
    private String manufacturer;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("board")
    @Expose
    private String board;
    @SerializedName("hardware")
    @Expose
    private String hardware;
    @SerializedName("bootloader")
    @Expose
    private String bootloader;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("host")
    @Expose
    private String host;
    @SerializedName("sdk")
    @Expose
    private Integer sdkInt;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("time")
    @Expose
    private Long time;
    @SerializedName("fingerprint")
    @Expose
    private String fingerprint;

    /**
     * No args constructor for use in serialization
     *
     */
    public Build() {

    }

    /**
     *
     * @param id
     * @param fingerprint
     * @param time
     * @param model
     * @param host
     * @param hardware
     * @param manufacturer
     * @param sdkInt
     * @param brand
     * @param bootloader
     * @param board
     * @param user
     */
    public Build(String manufacturer, String brand, String model, String board, String hardware, String bootloader, String user, String host, Integer sdkInt, String id, Long time, String fingerprint) {
        super();
        this.manufacturer = manufacturer;
        this.brand = brand;
        this.model = model;
        this.board = board;
        this.hardware = hardware;
        this.bootloader = bootloader;
        this.user = user;
        this.host = host;
        this.sdkInt = sdkInt;
        this.id = id;
        this.time = time;
        this.fingerprint = fingerprint;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

    public String getBootloader() {
        return bootloader;
    }

    public void setBootloader(String bootloader) {
        this.bootloader = bootloader;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getSdkInt() {
        return sdkInt;
    }

    public void setSdkInt(Integer sdkInt) {
        this.sdkInt = sdkInt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    @Override
    public String toString() {
        return "Build{" +
                "manufacturer='" + manufacturer + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", board='" + board + '\'' +
                ", hardware='" + hardware + '\'' +
                ", bootloader='" + bootloader + '\'' +
                ", user='" + user + '\'' +
                ", host='" + host + '\'' +
                ", sdkInt=" + sdkInt +
                ", id='" + id + '\'' +
                ", time=" + time +
                ", fingerprint='" + fingerprint + '\'' +
                '}';
    }
}
