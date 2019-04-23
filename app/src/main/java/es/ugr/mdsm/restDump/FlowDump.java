package es.ugr.mdsm.restDump;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FlowDump {

    @SerializedName("mac")
    @Expose
    public String mac;
    @SerializedName("flows")
    @Expose
    public List<GFlow> flows = null;

}