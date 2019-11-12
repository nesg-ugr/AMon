package es.ugr.mdsm.amon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Version {

    @SerializedName("last-version")
    @Expose
    private Integer lastVersion;

    public Version() {
    }

    public Version(Integer lastVersion) {
        this.lastVersion = lastVersion;
    }

    public Integer getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(Integer lastVersion) {
        this.lastVersion = lastVersion;
    }

    @Override
    public String toString() {
        return "Version{" +
                "lastVersion=" + lastVersion +
                '}';
    }
}
