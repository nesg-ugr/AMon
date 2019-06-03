package es.ugr.mdsm.restDump;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class App_ {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("permissions")
    @Expose
    private String permissions;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("autoStart")
    @Expose
    private Boolean autoStart;

    /**
     * No args constructor for use in serialization
     *
     */
    public App_() {
    }

    /**
     *
     * @param autoStart
     * @param name
     * @param permissions
     * @param version
     */
    public App_(String name, String permissions, String version, Boolean autoStart) {
        super();
        this.name = name;
        this.permissions = permissions;
        this.version = version;
        this.autoStart = autoStart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getAutoStart() {
        return autoStart;
    }

    public void setAutoStart(Boolean autoStart) {
        this.autoStart = autoStart;
    }

    @Override
    public String toString() {
        return "App_{" +
                "name='" + name + '\'' +
                ", permissions=" + permissions +
                ", version='" + version + '\'' +
                ", autoStart=" + autoStart +
                '}';
    }
}