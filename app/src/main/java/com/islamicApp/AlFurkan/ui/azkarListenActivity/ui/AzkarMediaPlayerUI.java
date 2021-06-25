package com.islamicApp.AlFurkan.ui.azkarListenActivity.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.islamicApp.AlFurkan.ui.homeActivity.activity.HomePage;
import com.islamicApp.AlFurkan.ui.azkarListenActivity.mpClass.AzkarMediaPlayer;
import com.islamicApp.AlFurkan.Classes.InternetConnection;
import com.islamicApp.AlFurkan.ui.quranListenActivity.mpClass.QuranMediaPlayer;
import com.islamicApp.AlFurkan.Classes.Utilities;
import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.ui.azkarListenActivity.service.AzkarListenService;
import com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService;

public class AzkarMediaPlayerUI extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    protected TextView backBtn;
    protected TextView azkarName;
    protected TextView currentDurationTV;
    protected TextView totalDurationTV;
    protected SeekBar azkarSeekBar;
    protected ImageView prevBtn;
    protected ImageView playPauseBtn;
    protected ImageView nextBtn;

    SharedPreferences preferences;
    String ZEKR_POS = "zekr_pos";
    Handler mHandler = new Handler();
    Utilities utilities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.setContentView(R.layout.activity_media_player);
        initView();
        preferences = getSharedPreferences("azkar_listen_file", MODE_PRIVATE);
        utilities = new Utilities();
        registerReceiver(receiver, new IntentFilter("Azkar_Track"));
        Intent intent = getIntent();
        if (intent!=null) {
            int zekrPos = intent.getIntExtra("zekr_pos", -1);
            azkarName.setText(namesArray[zekrPos]);
            preferences.edit().putInt(ZEKR_POS, zekrPos).apply();
        }
        azkarSeekBar.setOnSeekBarChangeListener(this);
        azkarSeekBar.setMax(AzkarMediaPlayer.getTotalDuration());
        if (AzkarMediaPlayer.isInitialized()) {
            //stop the seek bar
            updateProgressBar();
            azkarSeekBar.setProgress(0);
            azkarSeekBar.setMax(100);
            setPlayPauseImage();
        } else {
            finish();
            stopService(new Intent(this, AzkarListenService.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences servicePref = getSharedPreferences("azkarService", MODE_PRIVATE);
        int zekrPos = servicePref.getInt("zekr_pos", -1);
        preferences.edit().putInt(ZEKR_POS, zekrPos).apply();
        azkarName.setText(namesArray[zekrPos]);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_btn) {
            startActivity(new Intent(this, HomePage.class));
        } else if (view.getId() == R.id.prev_btn) {
            playPrev();
        } else if (view.getId() == R.id.play_pause_btn) {
            playPause();
        } else if (view.getId() == R.id.next_btn) {
            playNext();
        }
    }

    public void playPause() {
        if (AzkarMediaPlayer.isInitialized()) {
            stopQuran();
            startService(new Intent(this, AzkarListenService.class)
                    .putExtra("azkar_action", "play_pause"));
        }
    }

    public void playNext() {
        if (AzkarMediaPlayer.isInitialized()) {
            stopQuran();
            startService(new Intent(this, AzkarListenService.class)
                    .putExtra("azkar_action", "play_next"));
            int itemPosition = preferences.getInt(ZEKR_POS, 0);
            itemPosition++;
            if (itemPosition > namesArray.length - 1)
                itemPosition = 0;
            saveItemData(itemPosition);
            azkarName.setText(namesArray[itemPosition]);
        }
    }

    public void playPrev() {
        if (AzkarMediaPlayer.isInitialized()) {
            stopQuran();
            startService(new Intent(this, AzkarListenService.class)
                    .putExtra("azkar_action", "play_prev"));
            int itemPosition = preferences.getInt(ZEKR_POS, 0);
            itemPosition--;
            if (itemPosition < 0)
                itemPosition = namesArray.length - 1;
            saveItemData(itemPosition);
            azkarName.setText(namesArray[itemPosition]);
        }
    }

    public void setPlayPauseImage() {
        playPauseBtn.setImageResource(AzkarMediaPlayer.isPlaying() ? R.drawable.playing_btn_shape : R.drawable.pause_btn_shape);
    }

    private void saveItemData(int position) {
        preferences.edit().putInt(ZEKR_POS, position).apply();
    }

    public void stopQuran() {
        if (QuranMediaPlayer.isPlaying())
            startService(new Intent(this, QuranService.class)
                    .putExtra("action_token", "stop"));
    }

    private void initView() {
        backBtn = (TextView) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(AzkarMediaPlayerUI.this);
        azkarName = (TextView) findViewById(R.id.azkar_name);
        currentDurationTV = (TextView) findViewById(R.id.current_duration);
        totalDurationTV = (TextView) findViewById(R.id.total_duration);
        azkarSeekBar = (SeekBar) findViewById(R.id.azkar_seek_bar);
        prevBtn = (ImageView) findViewById(R.id.prev_btn);
        prevBtn.setOnClickListener(AzkarMediaPlayerUI.this);
        playPauseBtn = (ImageView) findViewById(R.id.play_pause_btn);
        playPauseBtn.setOnClickListener(AzkarMediaPlayerUI.this);
        nextBtn = (ImageView) findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(AzkarMediaPlayerUI.this);
    }

    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = AzkarMediaPlayer.getTotalDuration();
            long currentDuration = AzkarMediaPlayer.getCurrentTime();

            totalDurationTV.setText(String.valueOf(utilities.milliSecondsToTimer(totalDuration)));
            currentDurationTV.setText(String.valueOf(utilities.milliSecondsToTimer(currentDuration)));

            int progress = (int) (utilities.getProgressPercentage(currentDuration, totalDuration));
            azkarSeekBar.setProgress(progress);

            mHandler.postDelayed(this, 100);
            setPlayPauseImage();
        }
    };

    public String[] namesArray = {"أذكار الصباح", "أذكار المساء", "أذكار الإستيقاظ من النوم",
            "أذكار النوم", "أذكار ما بعد الصلاة", "الحج والعمرة",
            "الرُّقية الشرعية", "الكسوف والخسوف"};

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = AzkarMediaPlayer.getTotalDuration();
        int currentPosition = utilities.progressToTimer(seekBar.getProgress(), totalDuration);
        updateProgressBar();
        AzkarMediaPlayer.seekTo(currentPosition);
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("azkarAction");
            if (status!=null) {
                switch (status) {
                    case "next_from_notification":
                        selectNext();
                        break;
                    case "prev_from_notification":
                        selectPrev();
                        break;
                    case "close_from_notification":
                        finish();
                        break;
                }
            }
        }
    };

    private void selectNext() {
        if (new InternetConnection(this).internetIsConnected()) {
            int itemPosition = preferences.getInt(ZEKR_POS, 0);
            itemPosition++;
            if (itemPosition > namesArray.length - 1)
                itemPosition = 0;
            saveItemData(itemPosition);
            azkarName.setText(namesArray[itemPosition]);
        }
    }

    private void selectPrev() {
        if (new InternetConnection(this).internetIsConnected()) {
            int itemPosition = preferences.getInt(ZEKR_POS, 0);
            itemPosition--;
            if (itemPosition < 0)
                itemPosition = namesArray.length - 1;
            saveItemData(itemPosition);
            azkarName.setText(namesArray[itemPosition]);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void finish() {
        super.finish();
        mHandler.removeCallbacks(mUpdateTimeTask);
    }
}