package com.islamicApp.AlFurkan.ui.quranListenActivity.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class QuranReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent e = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (e == null) {
                return;
            }
            if (e.getAction() != KeyEvent.ACTION_DOWN || e.getRepeatCount() != 0) {
                return;
            }

            String command = "";
            switch (e.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    command = "play_next_sura";
                    break;

                case KeyEvent.KEYCODE_MEDIA_PLAY:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    command = "playPauseSura";
                    break;

                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    command = "play_next_sura";
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    command = "play_previous_sura";
                    break;

                case KeyEvent.KEYCODE_MEDIA_STOP:
                    command = "closesura";
                    break;
            }

            if (!command.equals("")) {
                try {
                    context.sendBroadcast(new Intent("Online_Track")
                            .putExtra("onlineAction", command));
                } catch (Exception ex) {
                    // Starting a service causes the following exception:
                    // java.lang.RuntimeException: Unable to start receiver landau.smp.SMPMediaButtonReceiver:
                    // java.lang.IllegalStateException: Not allowed to start service Intent ...: app is in background
                    // This is even though we call unregisterMediaButtonEventReceiver on service shutdown
                }
                if (isOrderedBroadcast()) {
                    abortBroadcast();
                }
            }
        }
    }
}
