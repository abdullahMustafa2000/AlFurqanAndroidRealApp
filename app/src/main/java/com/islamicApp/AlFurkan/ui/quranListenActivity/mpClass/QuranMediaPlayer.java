package com.islamicApp.AlFurkan.ui.quranListenActivity.mpClass;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.islamicApp.AlFurkan.Recievers.MyReceiver;

import java.io.IOException;

import static com.islamicApp.AlFurkan.Recievers.MyReceiver.NOTIFICATION_ACTION;

public class QuranMediaPlayer {

    private static MediaPlayer mMediaPlayer;
    private static String mCurrentMusicUri;
    private static boolean isPlaying;

    private static int mPauseDuration = 0;

    public static void startNewTrack(String uri, final Context context) throws IOException {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        mCurrentMusicUri = uri;

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(uri);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.prepare();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Intent intent = new Intent(context, MyReceiver.class);
                intent.putExtra(NOTIFICATION_ACTION, "SuraFinished");
                context.sendBroadcast(intent);
            }
        });
        isPlaying = true;
    }


    public static void startPlaying() {
        if (mMediaPlayer == null || mCurrentMusicUri == null) {
            try {
                throw new RuntimeException("Start player.");
            }catch (Exception e){
            }

        }

        mMediaPlayer.seekTo(mPauseDuration);
        mMediaPlayer.start();
        isPlaying = true;
    }

    public static boolean isInitialized() {
        return mMediaPlayer != null;
    }

    public static void pauseMusic() {
        if (mMediaPlayer == null) return;

        mPauseDuration = mMediaPlayer.getCurrentPosition();
        mMediaPlayer.pause();
        isPlaying = false;
    }

    public static boolean isPaused(){
        return isInitialized() && !isPlaying;
    }

    public static void stopMusic() {
        mPauseDuration = 0;
        mCurrentMusicUri = null;
        if (mMediaPlayer == null) return;

        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;

        isPlaying = false;
    }

    public static boolean isPlaying() {
        return isInitialized() && isPlaying;
    }

    public static int getTotalDuration() {
        return isInitialized() ? mMediaPlayer.getDuration() : 0;
    }

    public static int getCurrentTime() {
        return isInitialized() ? mMediaPlayer.getCurrentPosition() : 0;
    }

    public static void seekTo(int seekTo) {
        if (isInitialized()) {
            if (mMediaPlayer.getDuration() < seekTo)
                try {
                    throw new RuntimeException("Seek to duration cannot exceed to audio file duration.");
                }catch (Exception e){
                }
            mPauseDuration = seekTo;
            mMediaPlayer.seekTo(seekTo);
        }
    }
}
