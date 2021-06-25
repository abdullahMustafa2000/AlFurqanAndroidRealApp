package com.islamicApp.AlFurkan.ui.homeActivity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.islamicApp.AlFurkan.ui.homeActivity.Fragments.home.HomeFragment;
import com.islamicApp.AlFurkan.ui.homeActivity.Fragments.listening.ListeningFragment;
import com.islamicApp.AlFurkan.ui.homeActivity.Fragments.sebha.SebhaFragment;
import com.islamicApp.AlFurkan.R;

public class HomePage extends AppCompatActivity {

    public final int READ_STORAGE_CODE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_id);
        askForPermissions();
        //to set default fragment
        Intent intent = getIntent();
        if (intent.getStringExtra("From")!=null && intent.getStringExtra("From").equals("openLF")) {
            setFragment(new ListeningFragment());
            bottomNavigationView.setSelectedItemId(R.id.listening_bn);
        }
        else
            setFragment(new HomeFragment());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id= menuItem.getItemId();
                switch (id){
                    case  R.id.homePage_bn:
                        setFragment(new HomeFragment());
                        break;
                    case  R.id.listening_bn:
                        setFragment(new ListeningFragment());
                        break;
                    case  R.id.sebha_bn:
                        setFragment(new SebhaFragment());
                        break;
                }
                return true;
            }
        });

    }

    private void askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(HomePage.this, Manifest.permission.READ_PHONE_STATE)
             != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(HomePage.this, Manifest.permission.READ_PHONE_STATE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
                    builder.setTitle("Storage and Phone State permissions needed")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(HomePage.this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_STORAGE_CODE);
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();
                }else {
                    ActivityCompat.requestPermissions(HomePage.this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_STORAGE_CODE);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_STORAGE_CODE) {
            if ((grantResults.length!=0) && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED){
            }
        }
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

}

