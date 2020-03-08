package es.ugr.mdsm.restDump;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Usb {

    public static String HOST_TYPE = "HOST";
    public static String ACCESSORY_TYPE = "ACC";

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("manufacturer")
    @Expose
    public String manufacturer;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("usb_class")
    @Expose
    public Integer usbClass;
    @SerializedName("usb_subclass")
    @Expose
    public Integer usbSubClass;


    /**
     * No args constructor for use in serialization
     *
     */
    public Usb() {
    }

    /**
     *
     * @param usbClass
     * @param name
     * @param type
     * @param manufacturer
     */
    public Usb(String name, String manufacturer, String type, Integer usbClass, Integer usbSubClass) {
        super();
        this.name = name;
        this.manufacturer = manufacturer;
        this.type = type;
        this.usbClass = usbClass;
        this.usbSubClass = usbSubClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getUsbClass() {
        return usbClass;
    }

    public void setUsbClass(Integer usbClass) {
        this.usbClass = usbClass;
    }

    public Integer getUsbSubClass() {
        return usbSubClass;
    }

    public void setUsbSubClass(Integer usbSubClass) {
        this.usbSubClass = usbSubClass;
    }

    @Override
    public String toString() {
        return "Usb{" +
                "name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", type='" + type + '\'' +
                ", usbClass=" + usbClass +
                ", usbSubClass=" + usbSubClass +
                '}';
    }
}