package com.example.onTime;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.onTime.modele.Toolbox;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleMapsAPI googleMapsAPI = new GoogleMapsAPI(
                1602536400,
                "2 chemin du Bourg, Vienne en Val",
                "6 rue Léonard de Vinci, 45067 Orléans");

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Integer> future = executorService.submit(googleMapsAPI);

        try {
            Log.d("time_pas_traffic", String.valueOf(future.get()));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}