package com.islamicApp.AlFurkan.ui.homeActivity.Fragments.sebha;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.islamicApp.AlFurkan.R;

import java.util.Objects;

import at.markushi.ui.CircleButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class SebhaFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    protected Spinner azkarSpinner;
    protected TextView tasbehaCounter;
    protected ImageView minusBtn;
    protected ImageView plusBtn;
    protected CircleButton counterBtn;

    //to calculate and store counters
    protected String sName = "azkarSharedData";
    protected int selected_position = 0;
    //to save counters number
    public SharedPreferences sharedPreferences;
    protected String total_tasbehat = "total_tasbehat";
    protected String tasbeha_target = "tasbeha_counter";
    protected String last_select = "last_select";
    protected TextView totalTasbehTv;
    protected RelativeLayout statisticsView;
    protected LinearLayout statisticsTitleView;
    protected ImageView dropdownImage;
    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sebha, container, false);
        initView(view);
        sharedPreferences = getActivity().
                getSharedPreferences(sName, Context.MODE_PRIVATE);
        tasbehaCounter.setText(String.valueOf(sharedPreferences.getInt(tasbeha_target, 33)));
        //to set tasbehat array on my spinner
        setSpinnerData();
        mediaPlayer = MediaPlayer.create(SebhaFragment.this.getActivity(), R.raw.sebhasound);
        plusBtn.setOnLongClickListener(v -> {
            doubleIncrease();
            return false;
        });
        minusBtn.setOnLongClickListener(v -> {
            doubleDecrease();
            return false;
        });
        //set total tasbeh
        totalTasbehTv.setText(String.valueOf(sharedPreferences.getInt(total_tasbehat, 0)));
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
        ((TextView) parent.getChildAt(0)).setTextSize(24);
        Typeface face = ResourcesCompat.getFont(getActivity(), R.font.almarai);
        ((TextView) parent.getChildAt(0)).setTypeface(face);
        tasbehaCounter.setText(String.valueOf(sharedPreferences.getInt(position + "", sharedPreferences.getInt(tasbeha_target, 33))));
        selected_position = position;
        sharedPreferences.edit().putInt(last_select, position).apply();
        if (sharedPreferences.getInt(position + "", -1) == 0) {
            Toast.makeText(getActivity(), "اضغط للبدأ من جديد", Toast.LENGTH_SHORT).show();
            sharedPreferences.edit().putInt(position+"", sharedPreferences.getInt(tasbeha_target, 33)).apply();
        }
    }

    public void counter() {
        mediaPlayer.start();
        if (sharedPreferences.getInt(selected_position + "", 0) > 0) {
            int tasbehat = sharedPreferences.getInt(selected_position + "", 0) - 1;
            if (tasbehat > 0) {
                tasbehaCounter.setText(String.valueOf(tasbehat));
                sharedPreferences.edit().putInt(selected_position + "", tasbehat).apply();
            } else if (tasbehat == 0) {
                //start vibrating and move to next
                tasbehaCounter.setText(String.valueOf(tasbehat));
                Toast.makeText(getActivity(), "اضغط للبدأ في التاليه", Toast.LENGTH_SHORT).show();
                vibrate();
                sharedPreferences.edit().putInt(selected_position + "", tasbehat).apply();
            }
        } else if (sharedPreferences.getInt(selected_position + "", -1) == 0) {
            selectNext();
        } else if (sharedPreferences.getInt(selected_position + "", -1) < 0) {
            sharedPreferences.edit().putInt(selected_position + "", sharedPreferences.getInt(tasbeha_target, 33)).apply();
        }
        sharedPreferences.edit().putInt(total_tasbehat, sharedPreferences.getInt(total_tasbehat, 0) + 1).apply();
        int total = sharedPreferences.getInt(total_tasbehat, 0);
        totalTasbehTv.setText(String.valueOf(total));
    }

    private void selectNext() {
        selected_position++;
        if (selected_position > 7)
            selected_position = 0;
        int tasbehat = sharedPreferences.getInt(selected_position + "", 0);
        azkarSpinner.setSelection(selected_position);
        tasbehaCounter.setText(String.valueOf(tasbehat));
    }

    public void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((Vibrator) Objects.requireNonNull(getActivity()).getSystemService(Context.VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ((Vibrator) Objects.requireNonNull(getActivity()).getSystemService(Context.VIBRATOR_SERVICE)).vibrate(900);
            }
        }
    }

    public void increaseTask() {
        int newTask = sharedPreferences.getInt(tasbeha_target, 33) + 1;
        tasbehaCounter.setText(String.valueOf(newTask));
        sharedPreferences.edit().putInt(tasbeha_target, newTask).apply();
        for (int i = 0; i < 8; i++) {
            sharedPreferences.edit().putInt(i + "", newTask).apply();
        }
        Toast.makeText(getActivity(), "يتم تحديد هدفك إلى " + newTask + " تسبيحه ", Toast.LENGTH_SHORT).show();
    }

    public void doubleIncrease() {
        int newTask = sharedPreferences.getInt(tasbeha_target, 33) + 10;
        tasbehaCounter.setText(String.valueOf(newTask));
        sharedPreferences.edit().putInt(tasbeha_target, newTask).apply();
        for (int i = 0; i < 8; i++) {
            sharedPreferences.edit().putInt(i + "", newTask).apply();
        }
        Toast.makeText(getActivity(), "يتم تحديد هدفك إلى " + newTask + " تسبيحه ", Toast.LENGTH_SHORT).show();
    }

    public void decreaseTask() {
        int newTask = sharedPreferences.getInt(tasbeha_target, 33) - 1;
        if (newTask < 0) {
            sharedPreferences.edit().putInt(tasbeha_target, sharedPreferences.getInt(tasbeha_target, 33)).apply();
            int task = sharedPreferences.getInt(tasbeha_target, 0);
            tasbehaCounter.setText(String.valueOf(task));
            for (int i = 0; i < 8; i++) {
                sharedPreferences.edit().putInt(i + "", task).apply();
            }
        } else {
            tasbehaCounter.setText(String.valueOf(newTask));
            sharedPreferences.edit().putInt(tasbeha_target, newTask).apply();
            for (int i = 0; i < 8; i++) {
                sharedPreferences.edit().putInt(i + "", newTask).apply();
            }
        }
        Toast.makeText(getActivity(), "يتم تحديد هدفك إلى " + newTask + " تسبيحه ", Toast.LENGTH_SHORT).show();
    }

    public void doubleDecrease() {
        int newTask = sharedPreferences.getInt(tasbeha_target, 33) - 10;
        if (newTask < 0) {
            sharedPreferences.edit().putInt(tasbeha_target, sharedPreferences.getInt(tasbeha_target, 33)).apply();
            int task = sharedPreferences.getInt(tasbeha_target, 0);
            tasbehaCounter.setText(String.valueOf(task));
            for (int i = 0; i < 8; i++) {
                sharedPreferences.edit().putInt(i + "", task).apply();
            }
        } else {
            tasbehaCounter.setText(String.valueOf(newTask));
            sharedPreferences.edit().putInt(tasbeha_target, newTask).apply();
            for (int i = 0; i < 8; i++) {
                sharedPreferences.edit().putInt(i + "", newTask).apply();
            }
        }
        Toast.makeText(getActivity(), "يتم تحديد هدفك إلى " + newTask + " تسبيحه ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void setSpinnerData() {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.alAzkar, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        azkarSpinner.setAdapter(arrayAdapter);
        azkarSpinner.setOnItemSelectedListener(this);
        azkarSpinner.setSelection(sharedPreferences.getInt(last_select, 0));
    }

    private void initView(View rootView) {
        azkarSpinner = (Spinner) rootView.findViewById(R.id.azkar_spinner);
        minusBtn = (ImageView) rootView.findViewById(R.id.minus_btn);
        minusBtn.setOnClickListener(SebhaFragment.this);
        tasbehaCounter = (TextView) rootView.findViewById(R.id.tasbeha_counter);
        plusBtn = (ImageView) rootView.findViewById(R.id.plus_btn);
        plusBtn.setOnClickListener(SebhaFragment.this);
        counterBtn = rootView.findViewById(R.id.counter_btn);
        counterBtn.setOnClickListener(SebhaFragment.this);
        totalTasbehTv = (TextView) rootView.findViewById(R.id.total_tasbeh_tv);
        statisticsView = (RelativeLayout) rootView.findViewById(R.id.statistics_view);
        statisticsTitleView = (LinearLayout) rootView.findViewById(R.id.statistics_title_view);
        statisticsTitleView.setOnClickListener(SebhaFragment.this);
        dropdownImage = (ImageView) rootView.findViewById(R.id.dropdown_image);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.minus_btn) {
            decreaseTask();
        } else if (view.getId() == R.id.plus_btn) {
            increaseTask();
        } else if (view.getId() == R.id.counter_btn) {
            counter();
        } else if (view.getId() == R.id.statistics_title_view) {
            if (statisticsView.getVisibility() == View.GONE) {
                statisticsView.setVisibility(View.VISIBLE);
                dropdownImage.setImageResource(R.drawable.sebha_dropdown_up);
            } else {
                statisticsView.setVisibility(View.GONE);
                dropdownImage.setImageResource(R.drawable.sebha_dropdown_down);
            }
        }
    }
}