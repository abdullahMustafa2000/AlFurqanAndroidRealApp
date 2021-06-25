package com.islamicApp.AlFurkan.Service;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID = "channel1";
    final static String AUDIO_BECOMING_NOISY = "android.media.AUDIO_BECOMING_NOISY";
    private AudioOutputChangeReceiver audioOutputChangeReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        createMediaPlayerNotificationChannel();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AUDIO_BECOMING_NOISY);
        audioOutputChangeReceiver = new AudioOutputChangeReceiver();
        registerReceiver(audioOutputChangeReceiver, intentFilter);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(audioOutputChangeReceiver);
    }

    private void createMediaPlayerNotificationChannel() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"quran notification", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("quran listening");
            channel.setVibrationPattern(new long[]{ 0 });
            channel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }
    }

    private static class AudioOutputChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionOfIntent = intent.getAction();
            if (actionOfIntent != null && actionOfIntent.equals(AUDIO_BECOMING_NOISY)){
                String status = "callStarted";
                context.sendBroadcast(new Intent("Online_Track")
                        .putExtra("onlineAction", status));

                context.sendBroadcast(new Intent("Azkar_Track")
                        .putExtra("azkarAction", status));

                context.sendBroadcast(new Intent("suggestion_Track")
                        .putExtra("sugAction", status));
            }
        }
    }
}
