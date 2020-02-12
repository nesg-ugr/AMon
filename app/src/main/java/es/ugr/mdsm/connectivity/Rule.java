package es.ugr.mdsm.connectivity;

public class Rule {
    public final static int ICMP = 1;
    public final static int TCP = 6;
    public final static int UDP = 17;


    public int version;
    public int protocol;
    public String daddr; // 1.2.3.4 or example.com
    public int dport;
    public int uid;
    public boolean blocked;

    public Rule(int version, int protocol, String daddr, int dport, int uid, boolean blocked) {
        this.version = version;
        this.protocol = protocol;
        this.daddr = daddr;
        this.dport = dport;
        this.uid = uid;
        this.blocked = blocked;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "version=" + version +
                ", protocol=" + protocol +
                ", daddr='" + daddr + '\'' +
                ", dport=" + dport +
                ", uid=" + uid +
                ", blocked=" + blocked +
                '}';
    }
}
