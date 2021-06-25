package com.islamicApp.AlFurkan.ui.quranSuggestions.service;

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
import com.islamicApp.AlFurkan.ui.quranSuggestions.ui.QuranSuggetionsMPActivity;
import com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService;
import com.islamicApp.AlFurkan.Classes.InternetConnection;
import com.islamicApp.AlFurkan.Classes.QuranListenSuggestionsDbOpener;
import com.islamicApp.AlFurkan.ui.quranSuggestions.mpClass.SuggestionsMP;
import com.islamicApp.AlFurkan.Modules.QuranListenSuggestionsModel;
import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.Recievers.MyReceiver;
import com.islamicApp.AlFurkan.ui.quranSuggestions.reciever.SuggestionsReciever;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.islamicApp.AlFurkan.Recievers.MyReceiver.NOTIFICATION_ACTION;
import static com.islamicApp.AlFurkan.Service.App.CHANNEL_ID;
import static com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService.State.PAUSED;
import static com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService.State.PLAYING;
import static com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService.State.STOPPED;


public class QuranSuggestionsService extends Service {

    private int icon;
    private MediaSessionCompat mediaSessionCompat;
    private RemoteControlClient remoteControlClient;
    List<QuranListenSuggestionsModel> quranModelList;
    private int position;
    private boolean wasPlaying;
    SharedPreferences preferences;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(this, "tag");
        quranModelList = new ArrayList<>();
        registerReceiver(receiver, new IntentFilter("suggestion_Track"));
        QuranListenSuggestionsDbOpener suggestionsClass = new QuranListenSuggestionsDbOpener(this);
        quranModelList.addAll(suggestionsClass.getSuggestionsList());
        preferences = getSharedPreferences("quranSuggestions", MODE_PRIVATE);
    }

    public void saveItemData(int position) {
        preferences.edit().putInt("quranPos", position).apply();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra("actionToken");
        if (action != null) {

            switch (action) {
                case "startNewSura":
                    position = intent.getIntExtra("selectedPos",-1);
                    try {
                        startNewSound(quranModelList.get(position).getSuraLink(),
                                quranModelList.get(position).getSuraName(),
                                quranModelList.get(position).getQareeName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "playNextItem":
                    playNext();
                    break;
                case "playPrevItem":
                    playPrevious();
                    break;
                case "playPause":
                    playPause();
                    break;
                case "stop":
                    if (SuggestionsMP.isPlaying()) {
                        SuggestionsMP.stopMusic();
                        updateState(STOPPED);
                    }
                    break;

                case "pauseQuran":
                    pauseQuran();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private void playNext() {
        if (new InternetConnection(this).internetIsConnected()) {
            position++;
            if (position > quranModelList.size() - 1)
                position = 0;
            try {
                startNewSound(quranModelList.get(position).getSuraLink(),
                        quranModelList.get(position).getSuraName(),
                        quranModelList.get(position).getQareeName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "اتصل بالانترنت ثم عاود المحاوله", Toast.LENGTH_SHORT).show();
        }
    }

    private void playPause() {
        if (new InternetConnection(this).internetIsConnected()) {
            if (SuggestionsMP.isPlaying()) {
                SuggestionsMP.pauseMusic();
                icon = R.drawable.pause_not;
                updateState(PAUSED);
            } else {
                SuggestionsMP.startPlaying();
                icon = R.drawable.play_pause;
                updateState(PLAYING);
            }
            showNotification(icon, quranModelList.get(position).getSuraName()
                    , quranModelList.get(position).getQareeName());
        } else {
            Toast.makeText(this, "اتصل بالانترنت ثم عاود المحاوله", Toast.LENGTH_SHORT).show();
        }
    }

    private void playPrevious() {
        if (new InternetConnection(this).internetIsConnected()) {
            position--;
            if (position < 0)
                position = quranModelList.size() - 1;

            try {
                startNewSound(quranModelList.get(position).getSuraLink(),
                        quranModelList.get(position).getSuraName(),
                        quranModelList.get(position).getQareeName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "اتصل بالانترنت ثم عاود المحاوله", Toast.LENGTH_SHORT).show();
        }
    }

    private void startNewSound(String suraLink, String suraName, String qareeName) throws IOException {
        if (new InternetConnection(this).internetIsConnected()) {
            SuggestionsMP.startNewTrack(suraLink, this);
            icon = R.drawable.play_pause;
            showNotification(icon, suraName, qareeName);
            registerRemoteControl();
            setNotification();
            updateState(PLAYING);
            saveItemData(position);
        } else {
            Toast.makeText(this, "اتصل بالانترنت ثم عاود المحاوله", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateState(QuranService.State newState) {

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
        e.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, quranModelList.get(position).getSuraName());
        e.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, quranModelList.get(position).getQareeName());
        e.putString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, quranModelList.get(position).getQareeName());
        e.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, "AlFurkan");
        e.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, cover);
        e.putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, SuggestionsMP.getTotalDuration());
        e.apply();
    }

    private void registerRemoteControl() {
        ComponentName eventReceiver = new ComponentName(this, SuggestionsReciever.class);
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

    public void showNotification(int playIcon, String suraName, String qareeName) {
        Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.app_logo);

        Intent playPauseIntent = new Intent(this, MyReceiver.class);
        playPauseIntent.putExtra(NOTIFICATION_ACTION,"playPauseSug");
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(this, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent replayIntent = new Intent(this, MyReceiver.class);
        replayIntent.putExtra(NOTIFICATION_ACTION, "replaySug");
        PendingIntent replaySongPendingIntent = PendingIntent.getBroadcast(this, 1, replayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playNext = new Intent(this, MyReceiver.class);
        playNext.putExtra(NOTIFICATION_ACTION, "playNextSug");
        PendingIntent playNextPendingIntent = PendingIntent.getBroadcast(this, 2, playNext, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playPrev = new Intent(this, MyReceiver.class);
        playPrev.putExtra(NOTIFICATION_ACTION, "playPreviousSug");
        PendingIntent playPrevPendingIntent = PendingIntent.getBroadcast(this, 3, playPrev, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent closeIntent = new Intent(this, MyReceiver.class);
        closeIntent.putExtra(NOTIFICATION_ACTION, "closeSug");
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 4, closeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(suraName)
                .setContentText(qareeName)
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
                .setContentIntent(PendingIntent.getActivity(this, 0, newLauncherIntent(this), PendingIntent.FLAG_UPDATE_CURRENT))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2, 3, 4).setMediaSession(mediaSessionCompat.getSessionToken()))
                .build();
        startForeground(1, notification);
    }

    private Intent newLauncherIntent(Context context) {
        return new Intent(context, QuranSuggetionsMPActivity.class)
                .putExtra("currentPos", position);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("sugAction");
            if (action != null) {
                switch (action) {
                    case "playPauseSug":
                        playPause();
                        break;
                    case "replaySug":
                        replaySura();
                        break;
                    case "playNextSug":
                    case "sugSuraFinished":
                        playNext();
                        break;
                    case "playPreviousSug":
                        playPrevious();
                        break;
                    case "closeSug":
                        closeSura();
                        break;
                    case "callStarted":
                        if (SuggestionsMP.isPlaying())
                            pauseQuran();
                        break;
                    case "callEnded":
                        playAfterPause();
                        break;
                }
            }
        }
    };

    private void playAfterPause() {
        if (wasPlaying) {
            SuggestionsMP.startPlaying();
            icon = R.drawable.play_pause;
            showNotification(icon, quranModelList.get(position).getSuraName()
                    , quranModelList.get(position).getQareeName());
            updateState(PLAYING);
        }
    }

    private void pauseQuran() {
        SuggestionsMP.pauseMusic();
        wasPlaying = true;
        updateState(PAUSED);
        icon = R.drawable.pause_not;
        showNotification(icon, quranModelList.get(position).getSuraName()
                , quranModelList.get(position).getQareeName());
    }

    private void closeSura() {
        SuggestionsMP.stopMusic();
        stopForeground(true);
        stopSelf();
        stopService(new Intent(this, QuranSuggestionsService.class));
        unregisterReceiver(receiver);
        updateState(STOPPED);
        onDestroy();
    }

    private void replaySura() {
        try {
            startNewSound(quranModelList.get(position).getSuraLink(),
                    quranModelList.get(position).getSuraName(),
                    quranModelList.get(position).getQareeName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e){}
        super.onDestroy();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(1);
        }
    }
}
