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
    @SerializedName("rooted")
    @Expose
    private Boolean rooted;
    @SerializedName("debugging")
    @Expose
    private Boolean debugging;

    /**
     * No args constructor for use in serialization
     *
     */
    public Security() {
    }

    /**
     *
     * @param developerOptions
     * @param rooted
     * @param secure
     * @param unknownSources
     */
    public Security(Boolean unknownSources, Boolean developerOptions, Boolean secure, Boolean rooted, Boolean debugging) {
        super();
        this.unknownSources = unknownSources;
        this.developerOptions = developerOptions;
        this.secure = secure;
        this.rooted = rooted;
        this.debugging = debugging;
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

    public Boolean getRooted() {
        return rooted;
    }

    public void setRooted(Boolean rooted) {
        this.rooted = rooted;
    }

    public Boolean getDebugging() {
        return debugging;
    }

    public void setDebugging(Boolean debugging) {
        this.debugging = debugging;
    }

    @Override
    public String toString() {
        return "Security{" +
                "unknownSources=" + unknownSources +
                ", developerOptions=" + developerOptions +
                ", secure=" + secure +
                ", rooted=" + rooted +
                ", debugging=" + debugging +
                '}';
    }
}