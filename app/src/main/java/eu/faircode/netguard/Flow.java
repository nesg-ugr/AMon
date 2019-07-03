package eu.faircode.netguard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Flow {
    public int Uid;
    public String PackageName;
    public long Time;
    public long Duration;
    public int Protocol;
    public String SAddr;
    public int SPort;
    public String DAddr;
    public int DPort;
    public long Sent;
    public long Received;
    public int SentPackets;
    public int ReceivedPackets;
    public int TcpFlags;
    public int Tos;
    public boolean NewFlow;
    public boolean Finished;

    private static DateFormat formatter = SimpleDateFormat.getDateTimeInstance();

    public Object[] toArray(){
        return new Object[]{PackageName,Time,Duration,Protocol,SAddr,SPort,DAddr,DPort,Sent,Received,SentPackets,ReceivedPackets,TcpFlags,Tos,NewFlow,Finished};
    }
    @Override
    public String toString() {
        return formatter.format(new Date(Time).getTime()) +
                " for " + Duration + " ms " +
                " p" + Protocol +
                " " + SAddr + "/" + SPort +
                " " + DAddr + "/" + DPort +
                " uid " + Uid +
                " packageName " + PackageName +
                " Bytes " + " out " + Sent + " in " + Received +
                " Packets " + " out " + SentPackets + " in " + ReceivedPackets +
                " Flags " + TcpFlags +
                " ToS " + Tos +
                " NewFlow " + NewFlow +
                " Finished " + Finished;
    }
}
