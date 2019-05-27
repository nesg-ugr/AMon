package es.ugr.mdsm.deviceInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import java.util.Date;

// https://stackoverflow.com/a/15564021
public class CallReceiver extends BroadcastReceiver {

    private OnIncomingCallReceivedListener mOnIncomingCallReceivedListener;
    private OnIncomingCallAnsweredListener mOnIncomingCallAnsweredListener;
    private OnIncomingCallEndedListener mOnIncomingCallEndedListener;
    private OnOutgoingCallStartedListener mOnOutgoingCallStartedListener;
    private OnOutgoingCallEndedListener mOnOutgoingCallEndedListener;
    private OnMissedCallListener mOnMissedCallListener;
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing


    @Override
    public void onReceive(Context context, Intent intent) {

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
            savedNumber = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
        }
        else if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())){
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }

            onCallStateChanged(context, state, number);
        }
    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                if (mOnIncomingCallReceivedListener != null) {
                    mOnIncomingCallReceivedListener.OnIncomingCallReceived(context, number, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                    if (mOnOutgoingCallStartedListener != null){
                        mOnOutgoingCallStartedListener.OnOutgoingCallStarted(context, savedNumber, callStartTime);
                    }
                }
                else
                {
                    isIncoming = true;
                    callStartTime = new Date();
                    if (mOnIncomingCallAnsweredListener != null){
                        mOnIncomingCallAnsweredListener.OnIncomingCallAnswered(context, savedNumber, callStartTime);
                    }
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                    if (mOnMissedCallListener != null){
                        mOnMissedCallListener.OnMissedCall(context, savedNumber, callStartTime);
                    }
                }
                else if(isIncoming){
                    if (mOnIncomingCallEndedListener != null) {
                        mOnIncomingCallEndedListener.OnIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                    }
                }
                else{
                    if (mOnOutgoingCallEndedListener != null) {
                        mOnOutgoingCallEndedListener.OnOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                    }
                }
                break;
        }
        lastState = state;
    }

    public interface OnIncomingCallReceivedListener{
        void OnIncomingCallReceived(Context context, String number, Date start);
    }
    public interface OnIncomingCallAnsweredListener{
        void OnIncomingCallAnswered(Context context, String number, Date start);
    }
    public interface OnIncomingCallEndedListener{
        void OnIncomingCallEnded(Context context, String number, Date start, Date end);
    }
    public interface OnOutgoingCallStartedListener{
        void OnOutgoingCallStarted(Context context, String number, Date start);
    }
    public interface OnOutgoingCallEndedListener{
        void OnOutgoingCallEnded(Context context, String number, Date start, Date end);
    }
    public interface OnMissedCallListener{
        void OnMissedCall(Context context, String number, Date start);
    }

    public void setOnIncomingCallReceivedListener(OnIncomingCallReceivedListener listener){
        mOnIncomingCallReceivedListener = listener;
    }
    public void setOnIncomingCallAnsweredListener(OnIncomingCallAnsweredListener listener){
        mOnIncomingCallAnsweredListener = listener;
    }
    public void setOnIncomingCallEndedListener(OnIncomingCallEndedListener listener){
        mOnIncomingCallEndedListener = listener;
    }
    public void setOnOutgoingCallStartedListener(OnOutgoingCallStartedListener listener){
        mOnOutgoingCallStartedListener = listener;
    }
    public void setOnOutgoingCallEndedListener(OnOutgoingCallEndedListener listener){
        mOnOutgoingCallEndedListener = listener;
    }
    public void setOnMissedCallListener(OnMissedCallListener listener){
        mOnMissedCallListener = listener;
    }


}
