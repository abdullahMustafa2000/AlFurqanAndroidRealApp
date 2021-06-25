package com.islamicApp.AlFurkan.ui.quranSuggestions.ui;

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
import com.islamicApp.AlFurkan.Classes.QuranListenSuggestionsDbOpener;
import com.islamicApp.AlFurkan.ui.quranSuggestions.mpClass.SuggestionsMP;
import com.islamicApp.AlFurkan.Classes.Utilities;
import com.islamicApp.AlFurkan.Modules.QuranListenSuggestionsModel;
import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.ui.quranSuggestions.service.QuranSuggestionsService;

public class QuranSuggetionsMPActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    protected TextView backTv;
    protected TextView suranametv;
    protected TextView reciternametv;
    protected TextView sqCurrentDuration;
    protected TextView sqTotalDuration;
    protected SeekBar sqSeekBar;
    protected ImageView prevSqBtn;
    protected ImageView playPauseSqBtn;
    protected ImageView nextSqBtn;
    Utilities utilities;
    private final Handler mHandler = new Handler();
    private int currentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.setContentView(R.layout.activity_quran_suggetions_m_p);
        initView();
        utilities = new Utilities();
        sqSeekBar.setOnSeekBarChangeListener(this);
        registerReceiver(receiver, new IntentFilter("suggestion_Track"));
        sqSeekBar.setMax(SuggestionsMP.getTotalDuration());
        if (SuggestionsMP.isInitialized()) {
            sqSeekBar.setProgress(0);
            sqSeekBar.setMax(100);
            updateProgressBar();
        } else {
            finish();
            stopService(new Intent(this, QuranSuggestionsService.class));
        }
        Intent intent = getIntent();
        if (intent != null) {
            currentPos = intent.getIntExtra("currentPos",-1);
            reciternametv.setText(quraaNames[currentPos]);
            suranametv.setText("آل عمران");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter("suggestion_Track"));
        SharedPreferences servicePref =
                getSharedPreferences("quranSuggestions", MODE_PRIVATE);
        currentPos = servicePref.getInt("quranPos", -1);
        QuranListenSuggestionsModel suggestionsModel =
                new QuranListenSuggestionsDbOpener(this)
                        .getSuggestionsList()
                        .get(currentPos);
        reciternametv.setText(suggestionsModel.getQareeName());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_tv) {
            startActivity(new Intent(this, HomePage.class));
        } else if (view.getId() == R.id.prev_sq_btn) {
            playPrev();
        } else if (view.getId() == R.id.play_pause_sq_btn) {
            playPause();
        } else if (view.getId() == R.id.next_sq_btn) {
            playNext();
        }
    }

    private void playPrev() {
            selectPrev();
        startService(new Intent(this, QuranSuggestionsService.class)
                .putExtra("actionToken", "playPrevItem"));
    }

    private void playNext() {
            selectNext();
        startService(new Intent(this, QuranSuggestionsService.class)
                .putExtra("actionToken", "playNextItem"));
    }

    public void selectNext() {
        if (new InternetConnection(this).internetIsConnected()) {
            currentPos++;
            if (currentPos > quraaNames.length - 1)
                currentPos = 0;
            reciternametv.setText(quraaNames[currentPos]);
        }
    }

    private void playPause() {
        if (new InternetConnection(this).internetIsConnected())
            startService(new Intent(this, QuranSuggestionsService.class)
                .putExtra("actionToken", "playPause"));
        else {
            Toast.makeText(this, "اتصل بالانترنت ثم عاود المحاوله", Toast.LENGTH_SHORT).show();
        }
    }


    private void initView() {
        backTv = (TextView) findViewById(R.id.back_tv);
        backTv.setOnClickListener(QuranSuggetionsMPActivity.this);
        suranametv = (TextView) findViewById(R.id.suranametv);
        reciternametv = (TextView) findViewById(R.id.reciternametv);
        sqCurrentDuration = (TextView) findViewById(R.id.sq_current_duration);
        sqTotalDuration = (TextView) findViewById(R.id.sq_total_duration);
        sqSeekBar = (SeekBar) findViewById(R.id.sq_seek_bar);
        prevSqBtn = (ImageView) findViewById(R.id.prev_sq_btn);
        prevSqBtn.setOnClickListener(QuranSuggetionsMPActivity.this);
        playPauseSqBtn = (ImageView) findViewById(R.id.play_pause_sq_btn);
        playPauseSqBtn.setOnClickListener(QuranSuggetionsMPActivity.this);
        nextSqBtn = (ImageView) findViewById(R.id.next_sq_btn);
        nextSqBtn.setOnClickListener(QuranSuggetionsMPActivity.this);
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
        int totalDuration = SuggestionsMP.getTotalDuration();
        int currentPosition = utilities.progressToTimer(seekBar.getProgress(), totalDuration);
        updateProgressBar();
        SuggestionsMP.seekTo(currentPosition);
    }

    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = SuggestionsMP.getTotalDuration();
            long currentDuration = SuggestionsMP.getCurrentTime();

            sqTotalDuration.setText(String.valueOf(utilities.milliSecondsToTimer(totalDuration)));
            sqCurrentDuration.setText(String.valueOf(utilities.milliSecondsToTimer(currentDuration)));

            int progress = (int) (utilities.getProgressPercentage(currentDuration, totalDuration));
            sqSeekBar.setProgress(progress);

            mHandler.postDelayed(this, 100);
            setPlayPauseIcon();
        }
    };

    private void setPlayPauseIcon() {
        playPauseSqBtn.setImageResource(SuggestionsMP.isPlaying() ? R.drawable.playing_btn_shape : R.drawable.pause_btn_shape);
    }

    String[] quraaNames = {"ماهر المعيقلي", "عبدالرحمن السديس", "مشاري العفاسي", "فارس عباد" , "أحمد بن علي العجمي"
            , "عبدالباسط عبدالصمد", "ياسر الدوسري", "سعد الغامدي", "محمود خليل الحصري", "محمود علي البنا"};

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("sugAction");
            if (action != null) {
                switch (action) {
                    case "playNextSug":
                    case "sugSuraFinished":
                        selectNext();
                        break;
                    case "playPreviousSug":
                        selectPrev();
                        break;
                    case "closeSug":
                        finish();
                        break;
                }
            }
        }
    };

    private void selectPrev() {
        if (new InternetConnection(this).internetIsConnected()) {
            currentPos--;
            if (currentPos < 0)
                currentPos = quraaNames.length - 1;
            reciternametv.setText(quraaNames[currentPos]);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void finish() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        super.finish();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }
}