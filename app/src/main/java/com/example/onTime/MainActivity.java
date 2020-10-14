package com.example.onTime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.onTime.modele.Toolbox;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start);
    }


    public void nextSetDestination(View view) {
        Intent intent = new Intent(this, Destination.class);
        startActivity(intent);

    }
}