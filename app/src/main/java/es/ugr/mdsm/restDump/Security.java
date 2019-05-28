package es.ugr.mdsm.restDump;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Security {

    @SerializedName("unknownSources")
    @Expose
    private Boolean unknownSources;
    @SerializedName("developerOptions")
    @Expose
    private Boolean developerOptions;
    @SerializedName("secure")
    @Expose
    private Boolean secure;
    @SerializedName("root")
    @Expose
    private Boolean root;

    /**
     * No args constructor for use in serialization
     *
     */
    public Security() {
    }

    /**
     *
     * @param developerOptions
     * @param root
     * @param secure
     * @param unknownSources
     */
    public Security(Boolean unknownSources, Boolean developerOptions, Boolean secure, Boolean root) {
        super();
        this.unknownSources = unknownSources;
        this.developerOptions = developerOptions;
        this.secure = secure;
        this.root = root;
    }

    public Boolean getUnknownSources() {
        return unknownSources;
    }

    public void setUnknownSources(Boolean unknownSources) {
        this.unknownSources = unknownSources;
    }

    public Boolean getDeveloperOptions() {
        return developerOptions;
    }

    public void setDeveloperOptions(Boolean developerOptions) {
        this.developerOptions = developerOptions;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public Boolean getRoot() {
        return root;
    }

    public void setRoot(Boolean root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return "Security{" +
                "unknownSources=" + unknownSources +
                ", developerOptions=" + developerOptions +
                ", secure=" + secure +
                ", root=" + root +
                '}';
    }
}