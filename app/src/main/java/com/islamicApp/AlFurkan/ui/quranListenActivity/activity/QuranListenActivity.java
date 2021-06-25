package com.islamicApp.AlFurkan.ui.quranListenActivity.activity;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.islamicApp.AlFurkan.ui.homeActivity.activity.HomePage;
import com.islamicApp.AlFurkan.Classes.InternetConnection;
import com.islamicApp.AlFurkan.ui.quranListenActivity.mpClass.QuranMediaPlayer;
import com.islamicApp.AlFurkan.Classes.Utilities;
import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService;
import java.util.ArrayList;
import java.util.Objects;

public class QuranListenActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    protected TextView backBtn;
    protected TextView suraNameTv;
    protected TextView reciterNameTv;
    protected TextView currentDurationTv;
    protected TextView totalDurationTv;
    protected SeekBar quranSeekBar;
    protected ImageView prevQuranBtn;
    protected ImageView playPauseQuranBtn;
    protected ImageView nextQuranBtn;

    Utilities utilities;
    Handler mHandler = new Handler();
    SharedPreferences preferences;
    private final String SURA_POS = "sura_pos";
    private final String ARRAY_SIZE = "surasArray_size";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.setContentView(R.layout.activity_quran_media_player_u_i);
        initView();
        utilities = new Utilities();
        preferences = getSharedPreferences("quranMediaPlayerUI", MODE_PRIVATE);
        registerReceiver(receiver, new IntentFilter("Online_Track"));
        Intent bundle = getIntent();
        if (bundle != null) {
            ArrayList<String> surasArr = new ArrayList<>(Objects.requireNonNull(bundle.getStringArrayListExtra("surasArray")));
            preferences.edit().putInt(ARRAY_SIZE, surasArr.size()).apply();
            String qareeName = bundle.getStringExtra("qareeName");
            String suraName = bundle.getStringExtra("suraName");
            reciterNameTv.setText(qareeName);
            suraNameTv.setText(suraName);
            for (int i=0; i< surasArr.size(); i++)
                preferences.edit().putString(i+"", surasArr.get(i)).apply();
            preferences.edit().putInt(SURA_POS, surasArr.indexOf(suraName)).apply();
        }
        quranSeekBar.setOnSeekBarChangeListener(this);
        quranSeekBar.setMax(QuranMediaPlayer.getTotalDuration());
        if (QuranMediaPlayer.isInitialized()) {
            updateProgressBar();
            quranSeekBar.setProgress(0);
            quranSeekBar.setMax(100);
            setPlayPauseIcon();
        } else {
            finish();
            stopService(new Intent(this, QuranService.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences servicePref = getSharedPreferences("quranService", MODE_PRIVATE);
        preferences.edit().putInt(SURA_POS, servicePref.getInt("suraPos", -1)).apply();
        suraNameTv.setText(servicePref.getString("suraName", ""));
    }

    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private void setPlayPauseIcon() {
        playPauseQuranBtn.setImageResource(QuranMediaPlayer.isPlaying() ? R.drawable.playing_btn_shape : R.drawable.pause_btn_shape);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = QuranMediaPlayer.getTotalDuration();
            long currentDuration = QuranMediaPlayer.getCurrentTime();

            totalDurationTv.setText(String.valueOf(utilities.milliSecondsToTimer(totalDuration)));
            currentDurationTv.setText(String.valueOf(utilities.milliSecondsToTimer(currentDuration)));

            int progress = (int) (utilities.getProgressPercentage(currentDuration, totalDuration));
            quranSeekBar.setProgress(progress);

            mHandler.postDelayed(this, 100);
            setPlayPauseIcon();
        }
    };


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.homePage_btn_quran) {
            startActivity(new Intent(this, HomePage.class));
        } else if (view.getId() == R.id.prev_quran_btn) {
            playPrev();
        } else if (view.getId() == R.id.play_pause_quran_btn) {
            playPause();
        } else if (view.getId() == R.id.next_quran_btn) {
            playNext();
        }
    }

    public void playNext() {
        startService(new Intent(QuranListenActivity.this, QuranService.class).
                putExtra("action_token", "play_next_sura"));
        selectNext();
    }

    public void playPrev() {
        selectPrev();
        startService(new Intent(QuranListenActivity.this, QuranService.class).
                putExtra("action_token", "play_previous_sura"));
    }

    public void playPause() {
        if (new InternetConnection(this).internetIsConnected())
        startService(new Intent(this, QuranService.class)
                .putExtra("action_token", "playPauseSura"));
        else {
            Toast.makeText(this, "اتصل بالانترنت ثم عاود المحاوله", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        backBtn = (TextView) findViewById(R.id.homePage_btn_quran);
        backBtn.setOnClickListener(QuranListenActivity.this);
        suraNameTv = (TextView) findViewById(R.id.suranametv_quran);
        reciterNameTv = (TextView) findViewById(R.id.reciter_name_tv_quran);
        currentDurationTv = (TextView) findViewById(R.id.current_duration_quran);
        totalDurationTv = (TextView) findViewById(R.id.total_duration_quran);
        quranSeekBar = (SeekBar) findViewById(R.id.quran_seek_bar);
        prevQuranBtn = (ImageView) findViewById(R.id.prev_quran_btn);
        prevQuranBtn.setOnClickListener(QuranListenActivity.this);
        playPauseQuranBtn = (ImageView) findViewById(R.id.play_pause_quran_btn);
        playPauseQuranBtn.setOnClickListener(QuranListenActivity.this);
        nextQuranBtn = (ImageView) findViewById(R.id.next_quran_btn);
        nextQuranBtn.setOnClickListener(QuranListenActivity.this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = QuranMediaPlayer.getTotalDuration();
        int currentPosition = utilities.progressToTimer(seekBar.getProgress(), totalDuration);
        updateProgressBar();
        QuranMediaPlayer.seekTo(currentPosition);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("onlineAction");
            if (action!=null) {
                switch (action) {
                    case "SuraFinished":
                    case "play_next_sura":
                        selectNext();
                        break;
                    case "play_previous_sura":
                        selectPrev();
                        break;
                    case "closesura":
                        finish();
                        break;
                }
            }
        }
    };

    private void selectPrev() {
        if (new InternetConnection(this).internetIsConnected()) {
            int currentSuraPos = preferences.getInt(SURA_POS, -1);
            currentSuraPos--;
            if (currentSuraPos < 0)
                currentSuraPos = 0;
            suraNameTv.setText(preferences.getString(currentSuraPos + "", ""));
            preferences.edit().putInt(SURA_POS, currentSuraPos).apply();
        }
    }

    private void selectNext() {
        if (new InternetConnection(this).internetIsConnected()) {
            int currentSuraPos = preferences.getInt(SURA_POS, -1);
            currentSuraPos++;
            if (currentSuraPos > preferences.getInt(ARRAY_SIZE, -1) - 1)
                currentSuraPos = 0;
            suraNameTv.setText(preferences.getString(currentSuraPos + "", ""));
            preferences.edit().putInt(SURA_POS, currentSuraPos).apply();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomePage.class).putExtra("From", "openLF"));
    }

    @Override
    public void finish() {
        super.finish();
        mHandler.removeCallbacks(mUpdateTimeTask);
    }
}