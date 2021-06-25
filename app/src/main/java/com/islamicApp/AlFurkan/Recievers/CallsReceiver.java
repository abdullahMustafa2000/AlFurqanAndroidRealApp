package com.islamicApp.AlFurkan.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class CallsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = "";
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                .equals(TelephonyManager.EXTRA_STATE_OFFHOOK)
                ||intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            status = "callStarted";
        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                .equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            status = "callEnded";
        }

        context.sendBroadcast(new Intent("Online_Track")
                .putExtra("onlineAction", status));

        context.sendBroadcast(new Intent("Azkar_Track")
                .putExtra("azkarAction", status));

        context.sendBroadcast(new Intent("suggestion_Track")
                .putExtra("sugAction", status));

    }
}
