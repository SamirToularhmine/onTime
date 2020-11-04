package com.example.onTime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.onTime.mra.HomeActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        boolean userHasFinishedInitialSetup = sharedPreferences.getBoolean("userHasFinishedInitialSetup", false);
        if (userHasFinishedInitialSetup) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}