package com.example.onTime;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start);

        // Enregistrement du fait que l'utilisateur a passé l'intro, pour plus ne lui montrer ensuite
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        boolean userHasFinishedInitialSetup = sharedPreferences.getBoolean("userHasFinishedInitialSetup", false);
        if (!userHasFinishedInitialSetup) {
            sharedPreferences.edit().putBoolean("userHasFinishedInitialSetup", true).apply();
        }
    }

    public void nextSetDestination(View view) {
        Intent intent = new Intent(this, Destination.class);
        startActivity(intent);
    }
}