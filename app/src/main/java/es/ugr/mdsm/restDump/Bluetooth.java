package es.ugr.mdsm.restDump;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bluetooth {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("uuid")
    @Expose
    public List<String> uuid = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Bluetooth() {
    }

    /**
     *
     * @param name
     * @param uuid
     */
    public Bluetooth(String name, List<String> uuid) {
        super();
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUuid() {
        return uuid;
    }

    public void setUuid(List<String> uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "Bluetooth{" +
                "name='" + name + '\'' +
                ", uuid=" + uuid +
                '}';
    }
}