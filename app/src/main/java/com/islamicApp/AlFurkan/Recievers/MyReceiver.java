package com.islamicApp.AlFurkan.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    public static final String  NOTIFICATION_ACTION = "notification_action";
    @Override
    public void onReceive(Context context, Intent intent) {

        String status = intent.getStringExtra(NOTIFICATION_ACTION);

        if (status != null) {
            context.sendBroadcast(new Intent("Online_Track")
                    .putExtra("onlineAction", status));

            context.sendBroadcast(new Intent("Azkar_Track")
                    .putExtra("azkarAction", status));

            context.sendBroadcast(new Intent("suggestion_Track")
                    .putExtra("sugAction", status));
        }
    }
}
