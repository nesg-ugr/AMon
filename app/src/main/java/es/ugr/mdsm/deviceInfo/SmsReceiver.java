package es.ugr.mdsm.deviceInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;

// WIP

public class SmsReceiver extends BroadcastReceiver {

    private OnSmsReceivedListener mOnSmsReceivedListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())){
            return;
        }
        if (mOnSmsReceivedListener != null) {
            mOnSmsReceivedListener.OnSmsReceived(context, intent);
        }
    }

    public void setOnSmsReceivedListener(OnSmsReceivedListener listener){
         mOnSmsReceivedListener= listener;
    }

    public interface OnSmsReceivedListener{
        void OnSmsReceived(Context context, Intent intent);
    }

}
