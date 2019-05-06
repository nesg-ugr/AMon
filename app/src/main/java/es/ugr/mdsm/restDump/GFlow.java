package es.ugr.mdsm.restDump;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GFlow {

    @SerializedName("packageName")
    @Expose
    private String packageName;
    @SerializedName("time")
    @Expose
    private Long time;
    @SerializedName("duration")
    @Expose
    private Long duration;
    @SerializedName("protocol")
    @Expose
    private Integer protocol;
    @SerializedName("saddr")
    @Expose
    private String saddr;
    @SerializedName("sport")
    @Expose
    private Integer sport;
    @SerializedName("daddr")
    @Expose
    private String daddr;
    @SerializedName("dport")
    @Expose
    private Integer dport;
    @SerializedName("sentBytes")
    @Expose
    private Long sentBytes;
    @SerializedName("receivedBytes")
    @Expose
    private Long receivedBytes;
    @SerializedName("sentPackets")
    @Expose
    private Integer sentPackets;
    @SerializedName("receivedPackets")
    @Expose
    private Integer receivedPackets;
    @SerializedName("tcpFlags")
    @Expose
    private Integer tcpFlags;
    @SerializedName("ToS")
    @Expose
    private Integer toS;

    /**
     * No args constructor for use in serialization
     *
     */
    public GFlow() {
    }

    /**
     *
     * @param saddr
     * @param protocol
     * @param packageName
     * @param dport
     * @param sport
     * @param daddr
     * @param time
     * @param duration
     * @param sentBytes
     * @param receivedBytes
     * @param toS
     * @param receivedPackets
     * @param tcpFlags
     * @param sentPackets
     */
    public GFlow(String packageName, Long time, Long duration, Integer protocol, String saddr, Integer sport, String daddr, Integer dport, Long sentBytes, Long receivedBytes, Integer sentPackets, Integer receivedPackets, Integer tcpFlags, Integer toS) {
        super();
        this.packageName = packageName;
        this.time = time;
        this.duration = duration;
        this.protocol = protocol;
        this.saddr = saddr;
        this.sport = sport;
        this.daddr = daddr;
        this.dport = dport;
        this.sentBytes = sentBytes;
        this.receivedBytes = receivedBytes;
        this.sentPackets = sentPackets;
        this.receivedPackets = receivedPackets;
        this.tcpFlags = tcpFlags;
        this.toS = toS;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getProtocol() {
        return protocol;
    }

    public void setProtocol(Integer protocol) {
        this.protocol = protocol;
    }

    public String getSaddr() {
        return saddr;
    }

    public void setSaddr(String saddr) {
        this.saddr = saddr;
    }

    public Integer getSport() {
        return sport;
    }

    public void setSport(Integer sport) {
        this.sport = sport;
    }

    public String getDaddr() {
        return daddr;
    }

    public void setDaddr(String daddr) {
        this.daddr = daddr;
    }

    public Integer getDport() {
        return dport;
    }

    public void setDport(Integer dport) {
        this.dport = dport;
    }

    public Long getSentBytes() {
        return sentBytes;
    }

    public void setSentBytes(Long sentBytes) {
        this.sentBytes = sentBytes;
    }

    public Long getReceivedBytes() {
        return receivedBytes;
    }

    public void setReceivedBytes(Long receivedBytes) {
        this.receivedBytes = receivedBytes;
    }

    public Integer getSentPackets() {
        return sentPackets;
    }

    public void setSentPackets(Integer sentPackets) {
        this.sentPackets = sentPackets;
    }

    public Integer getReceivedPackets() {
        return receivedPackets;
    }

    public void setReceivedPackets(Integer receivedPackets) {
        this.receivedPackets = receivedPackets;
    }

    public Integer getTcpFlags() {
        return tcpFlags;
    }

    public void setTcpFlags(Integer tcpFlags) {
        this.tcpFlags = tcpFlags;
    }

    public Integer getToS() {
        return toS;
    }

    public void setToS(Integer toS) {
        this.toS = toS;
    }

    @Override
    public String toString() {
        return "GFlow{" +
                "packageName='" + packageName + '\'' +
                ", time=" + time +
                ", duration=" + duration +
                ", protocol=" + protocol +
                ", saddr='" + saddr + '\'' +
                ", sport=" + sport +
                ", daddr='" + daddr + '\'' +
                ", dport=" + dport +
                ", sentBytes=" + sentBytes +
                ", receivedBytes=" + receivedBytes +
                ", sentPackets=" + sentPackets +
                ", receivedPackets=" + receivedPackets +
                ", tcpFlags=" + tcpFlags +
                ", toS=" + toS +
                '}';
    }
}