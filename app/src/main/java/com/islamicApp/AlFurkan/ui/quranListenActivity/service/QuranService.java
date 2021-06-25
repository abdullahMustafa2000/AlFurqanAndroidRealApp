package com.islamicApp.AlFurkan.ui.quranListenActivity.service;

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
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.islamicApp.AlFurkan.ui.quranListenActivity.activity.QuranListenActivity;
import com.islamicApp.AlFurkan.Classes.InternetConnection;
import com.islamicApp.AlFurkan.ui.quranListenActivity.mpClass.QuranMediaPlayer;
import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.Recievers.MyReceiver;
import com.islamicApp.AlFurkan.ui.quranListenActivity.reciever.QuranReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static com.islamicApp.AlFurkan.Recievers.MyReceiver.NOTIFICATION_ACTION;
import static com.islamicApp.AlFurkan.Service.App.CHANNEL_ID;
import static com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService.State.PAUSED;
import static com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService.State.PLAYING;
import static com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService.State.STOPPED;

public class QuranService extends Service {


    int icon;
    Notification notification;
    MediaSessionCompat mediaSessionCompat;

    ArrayList<String> recordedSuras;
    private String qareeLink;
    private int suraPos;
    private String suraName;
    private String qareeName;
    private String suraLink;
    private RemoteControlClient remoteControlClient;
    private boolean wasPlaying = true;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String QAREE_NAME = "qareeName";
    String SURA_NAME = "suraName";
    String SURA_POS = "suraPos";
    String SURA_LINK = "suraLink";
    String WAS_PLAYING = "wasPlaying";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(this, "tag");
        registerReceiver(receiver, new IntentFilter("Online_Track"));
        recordedSuras = new ArrayList<>();
        preferences = getSharedPreferences("quranService", MODE_PRIVATE);
    }

    public void saveItemData(int position, String suraName, String suraLink) {
        editor = preferences.edit();
        editor.putInt(SURA_POS, position);
        editor.putString(SURA_NAME, suraName);
        editor.putString(SURA_LINK, suraLink);
        editor.apply();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getSurasArray();
        String soundAction = intent.getStringExtra("action_token");
        if (soundAction != null)
        switch (soundAction){
            case "startSura":
                suraName = intent.getStringExtra("suraName");
                suraLink = intent.getStringExtra("suraLink");
                qareeName = intent.getStringExtra("qareeName");
                startNewSura(suraLink, suraName);
                qareeLink = intent.getStringExtra("qareeLink");
                recordedSuras = intent.getStringArrayListExtra("recordedSuras");
                suraPos = recordedSuras.indexOf(suraName);
                saveItemData(suraPos, suraName, suraLink);
                preferences.edit().putString(QAREE_NAME, qareeName).apply();
                break;
            case "playPauseSura":
                playPause();
                break;
            case "play_next_sura":
                playNext();
                break;
            case "play_previous_sura":
                playPrev();
                break;
            case "stop":
                if (QuranMediaPlayer.isPlaying()) {
                    QuranMediaPlayer.pauseMusic();
                    updateState(STOPPED);
                }
                break;
            case "pauseQuran":
                pauseQuran();
                break;
        }

        return START_NOT_STICKY;
    }

    private void startNewSura(String suraLink, String suraName) {
        if (isInternetConnected(this)) {
            try {
                QuranMediaPlayer.startNewTrack(suraLink, this);
                icon = R.drawable.play_pause;
                showNotification(icon, suraName, qareeName);
                registerRemoteControl();
                setNotification();
                updateState(PLAYING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "اتصل بالانترنت ثم عاود المحاوله", Toast.LENGTH_SHORT).show();
        }
    }

    private void playPause() {
        if (new InternetConnection(this).internetIsConnected()) {
            if (QuranMediaPlayer.isInitialized()) {
                if (QuranMediaPlayer.isPlaying()) {
                    QuranMediaPlayer.pauseMusic();
                    icon = R.drawable.pause_not;
                    updateState(PAUSED);
                } else {
                    QuranMediaPlayer.startPlaying();
                    icon = R.drawable.play_pause;
                    updateState(PLAYING);
                }
                showNotification(icon, preferences.getString(SURA_NAME, "")
                        , preferences.getString(QAREE_NAME, ""));
            }
        } else {
            Toast.makeText(this, "اتصل بالانترنت ثم عاود المحاوله", Toast.LENGTH_SHORT).show();
        }
    }

    private void playNext() {
        if (new InternetConnection(this).internetIsConnected()) {
            suraPos = preferences.getInt(SURA_POS, 0);
            suraPos++;
            if (suraPos > recordedSuras.size() - 1)
                suraPos = 0;
            suraName = recordedSuras.get(suraPos);
            suraLink = qareeLink + getSuraLink(suraName);
            startNewSura(suraLink, suraName);
            saveItemData(suraPos, suraName, suraLink);
        } else {
            Toast.makeText(this, "اتصل بالانترنت ثم عاود المحاوله", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSuraLink(String suraName) {
        int suraPos = getSurasArray().indexOf(suraName) + 1;
        if (suraPos < 10) {
           return "/00" + suraPos + ".mp3";
        } else if (suraPos < 100) {
            return "/0" + suraPos + ".mp3";
        } else {
            return "/" + suraPos + ".mp3";
        }
    }

    private void playPrev() {
        if (new InternetConnection(this).internetIsConnected()) {
            suraPos = preferences.getInt(SURA_POS, 0);
            suraPos--;
            if (suraPos < 0)
                suraPos = 0;
            suraName = recordedSuras.get(suraPos);
            suraLink = qareeLink + getSuraLink(suraName);
            startNewSura(suraLink, suraName);
            saveItemData(suraPos, suraName, suraLink);
        } else {
            Toast.makeText(this, "اتصل بالانترنت ثم عاود المحاوله", Toast.LENGTH_SHORT).show();
        }
    }

    public void replaySura() {
        startNewSura(suraLink, suraName);
    }

    public void closeSura() {
        QuranMediaPlayer.stopMusic();
        stopForeground(true);
        stopService(new Intent(this, QuranService.class));
        updateState(STOPPED);
        onDestroy();
    }

    public boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    public void showNotification(int playIcon, String suraName, String qareeName) {
        Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.app_logo);

        Intent playPauseIntent = new Intent(this, MyReceiver.class);
        playPauseIntent.putExtra(NOTIFICATION_ACTION,"playPauseSura");
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(this, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent replayIntent = new Intent(this, MyReceiver.class);
        replayIntent.putExtra(NOTIFICATION_ACTION, "replaysura");
        PendingIntent replaySongPendingIntent = PendingIntent.getBroadcast(this, 1, replayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playNext = new Intent(this, MyReceiver.class);
        playNext.putExtra(NOTIFICATION_ACTION, "play_next_sura");
        PendingIntent playNextPendingIntent = PendingIntent.getBroadcast(this, 2, playNext, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playPrev = new Intent(this, MyReceiver.class);
        playPrev.putExtra(NOTIFICATION_ACTION, "play_previous_sura");
        PendingIntent playPrevPendingIntent = PendingIntent.getBroadcast(this, 3, playPrev, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent closeIntent = new Intent(this, MyReceiver.class);
        closeIntent.putExtra(NOTIFICATION_ACTION, "closesura");
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 4, closeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(suraName)
                .setContentText(qareeName)
                .setSmallIcon(R.drawable.app_logo)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setLargeIcon(bitmap)
                .addAction(R.drawable.close_noti, "close", closePendingIntent)
                .addAction(R.drawable.prev, "play_prev", playPrevPendingIntent)
                .addAction(playIcon, "playPause", playPausePendingIntent)
                .addAction(R.drawable.next, "play_next", playNextPendingIntent)
                .addAction(R.drawable.replay_noti, "replay", replaySongPendingIntent)
                .setContentIntent(PendingIntent.getActivity(this,0, newLauncherIntent(this),PendingIntent.FLAG_UPDATE_CURRENT))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2, 3, 4).setMediaSession(mediaSessionCompat.getSessionToken()))
                .build();
        startForeground(1, notification);
    }


    public Intent newLauncherIntent (final Context context) {
        return new Intent(context, QuranListenActivity.class)
                .putStringArrayListExtra("surasArray", recordedSuras)
                .putExtra("qareeName", qareeName)
                .putExtra("suraName", suraName);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("onlineAction");
            if (action!=null)
            switch (action) {
                case "playPauseSura":
                    playPause();
                    break;
                case "replaysura":
                    replaySura();
                    break;
                case "play_next_sura":
                case "SuraFinished":
                    playNext();
                    break;
                case "play_previous_sura":
                    playPrev();
                    break;
                case "closesura":
                    closeSura();
                    break;
                case "callStarted":
                    if (QuranMediaPlayer.isPlaying())
                        pauseQuran();
                    break;
                case "callEnded":
                    playAfterPause();
                    break;
            }
        }
    };

    private void playAfterPause() {
        wasPlaying = preferences.getBoolean(WAS_PLAYING,false);
        if (wasPlaying && QuranMediaPlayer.isInitialized()) {
            QuranMediaPlayer.startPlaying();
            icon = R.drawable.play_pause;
            showNotification(icon, preferences.getString(SURA_NAME,"")
                    , preferences.getString(QAREE_NAME,""));
            updateState(PLAYING);
            wasPlaying = false;
            preferences.edit().putBoolean(WAS_PLAYING, wasPlaying).apply();
        }
    }

    private void pauseQuran() {
        if (QuranMediaPlayer.isPlaying()){
            QuranMediaPlayer.pauseMusic();
            icon = R.drawable.pause_not;
            showNotification(icon, preferences.getString(SURA_NAME,""),
                    preferences.getString(QAREE_NAME,""));
            updateState(PAUSED);
            wasPlaying = true;
            preferences.edit().putBoolean(WAS_PLAYING, wasPlaying).apply();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        stopForeground(true);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(1);
        }
        try {
            unregisterReceiver(receiver);
        } catch (Exception e){}
    }

    public ArrayList<String> getSurasArray() {
        ArrayList<String> surasArray = new ArrayList<>();
        final String[] qrnName = {"الفاتحه", "البقرة", "آل عمران", "النساء", "المائدة", "الأنعام", "الأعراف", "الأنفال", "التوبة", "يونس", "هود"
                , "يوسف", "الرعد", "إبراهيم", "الحجر", "النحل", "الإسراء", "الكهف", "مريم", "طه", "الأنبياء", "الحج", "المؤمنون"
                , "النّور", "الفرقان", "الشعراء", "النّمل", "القصص", "العنكبوت", "الرّوم", "لقمان", "السجدة", "الأحزاب", "سبأ"
                , "فاطر", "يس", "الصافات", "ص", "الزمر", "غافر", "فصّلت", "الشورى", "الزخرف", "الدّخان", "الجاثية", "الأحقاف"
                , "محمد", "الفتح", "الحجرات", "ق", "الذاريات", "الطور", "النجم", "القمر", "الرحمن", "الواقعة", "الحديد", "المجادلة"
                , "الحشر", "الممتحنة", "الصف", "الجمعة", "المنافقون", "التغابن", "الطلاق", "التحريم", "الملك", "القلم", "الحاقة", "المعارج"
                , "نوح", "الجن", "المزّمّل", "المدّثر", "القيامة", "الإنسان", "المرسلات", "النبأ", "النازعات", "عبس", "التكوير", "الإنفطار"
                , "المطفّفين", "الإنشقاق", "البروج", "الطارق", "الأعلى", "الغاشية", "الفجر", "البلد", "الشمس", "الليل", "الضحى", "الشرح"
                , "التين", "العلق", "القدر", "البينة", "الزلزلة", "العاديات", "القارعة", "التكاثر", "العصر",
                "الهمزة", "الفيل", "قريش", "الماعون", "الكوثر", "الكافرون", "النصر", "المسد", "الإخلاص", "الفلق", "الناس"};
        Collections.addAll(surasArray, qrnName);
        return surasArray;
    }

    public enum State {
        PLAYING,
        PAUSED,
        STOPPED,
    }

    private void registerRemoteControl() {
        ComponentName eventReceiver = new ComponentName(this, QuranReceiver.class);
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

    private void updateState(State newState) {

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
        e.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, suraName);
        e.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, qareeName);
        e.putString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, qareeName);
        e.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, "AlFurkan");
        e.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, cover);
        e.putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, QuranMediaPlayer.getTotalDuration());
        e.apply();
    }

}
