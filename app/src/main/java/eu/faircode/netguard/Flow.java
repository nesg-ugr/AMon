package eu.faircode.netguard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Flow {
    public int Uid;
    public long Time;
    public int Version;
    public int Protocol;
    public String SAddr;
    public int SPort;
    public String DAddr;
    public int DPort;
    public long Sent;
    public long Received;

    private static DateFormat formatter = SimpleDateFormat.getDateTimeInstance();

    @Override
    public String toString() {
        return formatter.format(new Date(Time).getTime()) +
                " v" + Version + " p" + Protocol +
                " " + SAddr + "/" + SPort +
                " " + DAddr + "/" + DPort +
                " uid " + Uid +
                " out " + Sent + " in " + Received;
    }
}
