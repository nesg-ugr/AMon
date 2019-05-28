package es.ugr.mdsm.restDump;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class App {

    @SerializedName("app")
    @Expose
    private List<App_> app = null;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    /**
     * No args constructor for use in serialization
     *
     */
    public App() {
    }

    /**
     *
     * @param timestamp
     * @param app
     */
    public App(List<App_> app, String timestamp) {
        super();
        this.app = app;
        this.timestamp = timestamp;
    }

    public List<App_> getApp() {
        return app;
    }

    public void setApp(List<App_> app) {
        this.app = app;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "App{" +
                "app=" + app +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}