package es.ugr.mdsm.deviceInfo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;

// WIP
public class PremiumTelephonyChecker {

    private static final String TAG = "DeviceInfo.PremiumCheck";
    private SmsReceiver mSmsReceiver;
    private CallReceiver mCallReceiver;

    public PremiumTelephonyChecker(Context context) {
        IntentFilter smsIntentFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        mSmsReceiver = new SmsReceiver();
        mSmsReceiver.setOnSmsReceivedListener(new SmsReceiver.OnSmsReceivedListener() {
            @Override
            public void OnSmsReceived(Context context, Intent intent) {
                handlePremiumSms();
            }
        });
        IntentFilter callIntentFilter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        mCallReceiver = new CallReceiver();
        mCallReceiver.setOnOutgoingCallStartedListener(new CallReceiver.OnOutgoingCallStartedListener() {
            @Override
            public void OnOutgoingCallStarted(Context context, String number, Date start) {
                if (number == null) return;
                switch (number.substring(0,3)){
                    case "803":
                    case "806":
                    case "807":
                    case "905":
                    case "907":
                        Log.d(TAG, "Outgoing call to premium number");
                        handlePremiumCall(number);
                        break;

                }
            }
        });
        context.registerReceiver(mSmsReceiver,smsIntentFilter);
        context.registerReceiver(mCallReceiver, callIntentFilter);

    }

    public void end(Context context){
        context.unregisterReceiver(mCallReceiver);
        context.unregisterReceiver(mSmsReceiver);
    }

    private void handlePremiumSms(){

    }

    private void handlePremiumCall(String number){

    }
}
