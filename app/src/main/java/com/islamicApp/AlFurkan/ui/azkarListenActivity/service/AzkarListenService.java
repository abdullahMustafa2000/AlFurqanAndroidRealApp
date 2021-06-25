package com.islamicApp.AlFurkan.ui.azkarListenActivity.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.islamicApp.AlFurkan.ui.azkarListenActivity.ui.AzkarMediaPlayerUI;
import com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService;
import com.islamicApp.AlFurkan.ui.azkarListenActivity.mpClass.AzkarMediaPlayer;
import com.islamicApp.AlFurkan.Classes.InternetConnection;
import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.ui.azkarListenActivity.reciever.AzkarReceiver;
import com.islamicApp.AlFurkan.Recievers.MyReceiver;
import com.islamicApp.AlFurkan.Service.App;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.islamicApp.AlFurkan.Recievers.MyReceiver.NOTIFICATION_ACTION;
import static com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService.State.PAUSED;
import static com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService.State.PLAYING;
import static com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService.State.STOPPED;

public class AzkarListenService extends Service {

    int icon;
    Notification notification;
    MediaSessionCompat mediaSessionCompat;

    private int position;
    private String name;
    private String link;
    private RemoteControlClient remoteControlClient;
    boolean wasPlaying = false;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String ZEKR_NAME = "zekr_name", ZEKR_POS = "zekr_pos", ZEKR_LINK = "zekr_lik", WAS_PLAYING = "wasPlayng";

    String[] getLink = {
            "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A3%D8%B0%D9%83%D8%A7%D8%B1%20%D8%A7%D9%84%D8%B5%D8%A8%D8%A7%D8%AD.mp3?alt=media&token=dee02ada-885f-4df2-895b-3144d6d7e59e",
            "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A3%D8%B0%D9%83%D8%A7%D8%B1%20%D8%A7%D9%84%D9%85%D8%B3%D8%A7%D8%A1.mp3?alt=media&token=8d705241-36da-4bf7-8e14-1b27ae763ba6",
            "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A3%D8%B0%D9%83%D8%A7%D8%B1%20%D8%A7%D9%84%D8%A5%D8%B3%D8%AA%D9%8A%D9%82%D8%A7%D8%B8%20%D9%85%D9%86%20%D8%A7%D9%84%D9%86%D9%88%D9%85.mp3?alt=media&token=f80aba97-6638-4f88-a588-8fc08e2bee28",
            "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A3%D8%B0%D9%83%D8%A7%D8%B1%20%D8%A7%D9%84%D9%86%D9%88%D9%85.mp3?alt=media&token=fd578cae-8f44-49b5-823f-d9009d55a66b",
            "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A3%D8%B0%D9%83%D8%A7%D8%B1%20%D9%85%D8%A7%20%D8%A8%D8%B9%D8%AF%20%D8%A7%D9%84%D8%B5%D9%84%D8%A7%D8%A9.mp3?alt=media&token=8722e2c6-f963-4161-8a94-c70c324c2f4e",
            "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A7%D9%84%D8%AD%D8%AC%20%D9%88%D8%A7%D9%84%D8%B9%D9%85%D8%B1%D8%A9.mp3?alt=media&token=15fa5e04-478c-4d6a-a523-f00215454ce8",
            "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A7%D9%84%D8%B1%D9%8F%D9%91%D9%82%D9%8A%D8%A9%20%D8%A7%D9%84%D8%B4%D8%B1%D8%B9%D9%8A%D8%A9.mp3?alt=media&token=bdb7576a-6d77-42bc-a2be-256855cc590a",
            "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A7%D9%84%D9%83%D8%B3%D9%88%D9%81%20%D9%88%D8%A7%D9%84%D8%AE%D8%B3%D9%88%D9%81.mp3?alt=media&token=d075b7d4-508e-4f16-82b1-18bf6b622aa2"
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(this, "azkar Service");
        registerReceiver(receiver, new IntentFilter("Azkar_Track"));
        preferences = getSharedPreferences("azkarService", MODE_PRIVATE);
    }

    public String[] namesArray = {"أذكار الصباح", "أذكار المساء", "أذكار الإستيقاظ من النوم",
            "أذكار النوم", "أذكار ما بعد الصلاة", "الحج والعمرة",
            "الرُّقية الشرعية", "الكسوف والخسوف"};

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra("azkar_action");
        if (action != null)
        switch (action) {
            case "start_azkar":
                position = intent.getIntExtra("position", 0);
                name = namesArray[position];
                link = getLink[position];
                startNewSound(name, link, true);
                break;
            case "play_pause":
                playPause();
                break;

            case "play_next":
                playNext();
                break;

            case "play_prev":
                playPrev();
                break;

            case "stop":
                if (AzkarMediaPlayer.isPlaying()) {
                    AzkarMediaPlayer.stopMusic();
                    updateState(STOPPED);
                }
                break;

            case "pauseAzkar":
                pauseAzkar();
                break;
        }
        return START_NOT_STICKY;
    }

    private void saveItemData(String name, String link, int position) {
        editor = preferences.edit();
        editor.putString(ZEKR_NAME, name);
        editor.putString(ZEKR_LINK, link);
        editor.putInt(ZEKR_POS, position);
        editor.apply();
    }

    private void playNext() {
        if (new InternetConnection(this).internetIsConnected()) {
            position = preferences.getInt(ZEKR_POS, 0);
            position++;
            if (position > namesArray.length - 1)
                position = 0;
            name = namesArray[position];
            link = getLink[position];
            startNewSound(name, link, false);
        } else {
            Toast.makeText(this, "اتصل بالانترنت ثم عاود المحاوله", Toast.LENGTH_SHORT).show();
        }
    }

    private void playPrev() {
        if (new InternetConnection(this).internetIsConnected()) {
            position = preferences.getInt(ZEKR_POS, 0);
            position--;
            if (position < 0)
                position = namesArray.length - 1;
            name = namesArray[position];
            link = getLink[position];
            startNewSound(name, link, false);
        } else {
            Toast.makeText(this, "اتصل بالانترنت ثم عاود المحاوله", Toast.LENGTH_SHORT).show();
        }
    }

    private void playPause() {
        if (new InternetConnection(this).internetIsConnected())
        if (AzkarMediaPlayer.isInitialized()) {
            if (AzkarMediaPlayer.isPlaying()) {
                AzkarMediaPlayer.pauseMusic();
                icon = R.drawable.pause_not;
                updateState(PAUSED);
            }else {
                AzkarMediaPlayer.startPlaying();
                icon = R.drawable.play_pause;
                updateState(PLAYING);
            }
            showNotification(icon, preferences.getString(ZEKR_NAME,""));
        }
        else {
            Toast.makeText(this, "اتصل بالانترنت ثم عاود المحاوله", Toast.LENGTH_SHORT).show();
        }
    }
    //"اتصل بالانترنت ثم عاود المحاوله"

    public void startNewSound(String name, String link, boolean fromStart) {
        if (new InternetConnection(this).internetIsConnected()) {
            try {
                AzkarMediaPlayer.startNewTrack(link, this, fromStart, position);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            icon = R.drawable.play_pause;
            showNotification(icon, name);
            registerRemoteControl();
            setNotification();
            updateState(PLAYING);
            saveItemData(name, link, position);
        } else {
            Toast.makeText(this, "اتصل بالانترنت ثم عاود المحاوله", Toast.LENGTH_SHORT).show();
        }
    }

    public void replayZekr() {
        startNewSound(preferences.getString(ZEKR_NAME,""), getLink[preferences.getInt(ZEKR_POS,0)], false);
    }

    public void closeAzkar() {
        AzkarMediaPlayer.stopMusic();
        stopForeground(true);
        stopSelf();
        stopService(new Intent(this, AzkarListenService.class));
        updateState(STOPPED);
        onDestroy();
    }

    public void showNotification(int playIcon, String title) {
        Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.app_logo);

        Intent playPauseIntent = new Intent(this, MyReceiver.class);
        playPauseIntent.putExtra(NOTIFICATION_ACTION, "play_pause_notification");
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(this, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent replayIntent = new Intent(this, MyReceiver.class);
        replayIntent.putExtra(NOTIFICATION_ACTION, "replay_from_notification");
        PendingIntent replaySongPendingIntent = PendingIntent.getBroadcast(this, 1, replayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playNext = new Intent(this, MyReceiver.class);
        playNext.putExtra(NOTIFICATION_ACTION, "next_from_notification");
        PendingIntent playNextPendingIntent = PendingIntent.getBroadcast(this, 2, playNext, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playPrev = new Intent(this, MyReceiver.class);
        playPrev.putExtra(NOTIFICATION_ACTION, "prev_from_notification");
        PendingIntent playPrevPendingIntent = PendingIntent.getBroadcast(this, 3, playPrev, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent closeIntent = new Intent(this, MyReceiver.class);
        closeIntent.putExtra(NOTIFICATION_ACTION, "close_from_notification");
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 4, closeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.app_logo)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setVibrate(null)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setLargeIcon(bitmap)
                .setVibrate(new long[]{0L})
                .addAction(R.drawable.close_noti, "close", closePendingIntent)
                .addAction(R.drawable.prev, "play_prev", playPrevPendingIntent)
                .addAction(playIcon, "playPause", playPausePendingIntent)
                .addAction(R.drawable.next, "play_next", playNextPendingIntent)
                .addAction(R.drawable.replay_noti, "replay", replaySongPendingIntent)
                .setContentIntent(PendingIntent.getActivity(this,0, azkarIntent(this),PendingIntent.FLAG_UPDATE_CURRENT))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2, 3, 4).setMediaSession(mediaSessionCompat.getSessionToken()))
                .build();
        startForeground(1, notification);
    }

    public Intent azkarIntent (final Context context) {
        return new Intent(context, AzkarMediaPlayerUI.class).putExtra("zekr_pos", position);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("azkarAction");
            if (action!=null)
            switch (action) {
                case "play_pause_notification":
                    playPause();
                    break;
                case "replay_from_notification":
                case "AzkarFinished":
                    replayZekr();
                    break;
                case "next_from_notification":
                    playNext();
                    break;
                case "prev_from_notification":
                    playPrev();
                    break;
                case "close_from_notification":
                    closeAzkar();
                    break;
                case "callStarted":
                    if (AzkarMediaPlayer.isPlaying())
                    pauseAzkar();
                    break;
                case "callEnded":
                    playAfterPause();
                    break;
            }
        }
    };

    private void playAfterPause() {
        wasPlaying = preferences.getBoolean(WAS_PLAYING,false);
        if (wasPlaying && AzkarMediaPlayer.isInitialized()) {
            AzkarMediaPlayer.startPlaying();
            icon = R.drawable.play_pause;
            showNotification(icon, preferences.getString(ZEKR_NAME,""));
            updateState(PLAYING);
            wasPlaying = false;
            preferences.edit().putBoolean(WAS_PLAYING, wasPlaying).apply();
        }
    }

    private void pauseAzkar() {
        if (AzkarMediaPlayer.isPlaying()){
            AzkarMediaPlayer.pauseMusic();
            icon = R.drawable.pause_not;
            showNotification(icon, preferences.getString(ZEKR_NAME,""));
            updateState(STOPPED);
            wasPlaying = true;
            preferences.edit().putBoolean(WAS_PLAYING, wasPlaying).apply();
        }
    }

    private void  updateState(QuranService.State newState) {

        if (remoteControlClient == null) {
            return;
        }
        switch (newState) {
            case PLAYING:
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                break;
            case PAUSED:
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                break;
            case STOPPED:
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);
        }
    }

    private void setNotification() {
        RemoteControlClient.MetadataEditor e = remoteControlClient.editMetadata(true);
        Bitmap cover = BitmapFactory.decodeResource(getResources(),
                R.drawable.app_logo);
        e.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, name);
        e.putString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, "");
        e.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, cover);
        e.apply();
    }

    private void registerRemoteControl() {
        ComponentName eventReceiver = new ComponentName(this, AzkarReceiver.class);
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        audioManager.registerMediaButtonEventReceiver(eventReceiver);

        // build the PendingIntent for the remote control client
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(eventReceiver);
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0);
        // create and register the remote control client
        remoteControlClient = new RemoteControlClient(mediaPendingIntent);
        audioManager.registerRemoteControlClient(remoteControlClient);
        remoteControlClient.setTransportControlFlags(
                RemoteControlClient.FLAG_KEY_MEDIA_NEXT |
                        RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                        RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                        RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                        RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                        RemoteControlClient.FLAG_KEY_MEDIA_STOP
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        stopForeground(true);
        AzkarMediaPlayer.pauseMusic();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e){
            //exception
        }
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(1);
        }
    }

}
