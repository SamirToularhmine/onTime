package com.example.onTime.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.onTime.R;

/**
 * Activity du splash screen lors du chargement de l'app
 */
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        boolean userHasFinishedInitialSetup = sharedPreferences.getBoolean("userHasFinishedInitialSetup", false);

        if (userHasFinishedInitialSetup) {
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            startActivity(new Intent(this, IntroActivity.class));
        }
        finish();
    }
}