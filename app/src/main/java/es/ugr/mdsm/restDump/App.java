package es.ugr.mdsm.restDump;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class App {


    @SerializedName("app")
    @Expose
    private List<App_> app = null;
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
    public App() {
    }

    /**
     *
     * @param mac
     * @param timestamp
     * @param app
     */
    public App(List<App_> app, String mac, Long timestamp) {
        super();
        this.app = app;
        this.mac = mac;
        this.timestamp = timestamp;
    }

    public List<App_> getApp() {
        return app;
    }

    public void setApp(List<App_> app) {
        this.app = app;
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
        return "App{" +
                "app=" + app +
                ", mac='" + mac + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}