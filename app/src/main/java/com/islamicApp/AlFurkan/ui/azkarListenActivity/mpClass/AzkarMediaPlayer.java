package com.islamicApp.AlFurkan.ui.azkarListenActivity.mpClass;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import com.islamicApp.AlFurkan.ui.azkarListenActivity.ui.AzkarMediaPlayerUI;
import com.islamicApp.AlFurkan.Recievers.MyReceiver;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.islamicApp.AlFurkan.Recievers.MyReceiver.NOTIFICATION_ACTION;

public class AzkarMediaPlayer {

    private static MediaPlayer mMediaPlayer;
    private static String mCurrentMusicUri;
    private static boolean isPlaying;

    private static int mPauseDuration = 0;

    public static void startNewTrack(final String uri, final Context context, final boolean itemClicked, final int position) throws ExecutionException, InterruptedException, IOException {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        mCurrentMusicUri = uri;

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(uri);
        mMediaPlayer.prepareAsync();

        mMediaPlayer.setOnPreparedListener(mp -> {
            mMediaPlayer.start();
            if (itemClicked)
                context.startActivity(new Intent(context, AzkarMediaPlayerUI.class).putExtra("zekr_pos",position).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        });

        mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
            Intent intent = new Intent(context, MyReceiver.class);
            intent.putExtra(NOTIFICATION_ACTION, "AzkarFinished");
            context.sendBroadcast(intent);
        });
        isPlaying = true;
    }

    public static void startPlaying() {
        if (mMediaPlayer == null || mCurrentMusicUri == null) {
            throw new RuntimeException("Start player.");
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
                throw new RuntimeException("Seek to duration cannot exceed to audio file duration.");
            mPauseDuration = seekTo;
            mMediaPlayer.seekTo(seekTo);
        }
    }
}
