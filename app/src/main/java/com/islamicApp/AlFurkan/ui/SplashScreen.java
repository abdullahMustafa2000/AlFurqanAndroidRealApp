package com.islamicApp.AlFurkan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.islamicApp.AlFurkan.ui.homeActivity.activity.HomePage;
import com.islamicApp.AlFurkan.R;

public class SplashScreen extends AppCompatActivity {

    int splashTime = 800;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.splash_);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, HomePage.class));
                finish();
            }
        }, splashTime);
    }
}
