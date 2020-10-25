package com.example.onTime;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.onTime.modele.Adresse;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;

import java.util.ArrayList;
import java.util.List;

public class MorningRoutineActivity extends AppCompatActivity {

    private ListView listView;
    private MorningRoutine laMorningRoutine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morning_routine);
        laMorningRoutine = new MorningRoutine("Premiere Morning Routine", this.createTache(5));
        TextView nom =(TextView)findViewById(R.id.titreMorningRoutine);
        nom.setText(laMorningRoutine.getNom());

        this.listView = findViewById(R.id.tacheList);

        this.listView.setAdapter(new TacheAdapter(this, laMorningRoutine.getListeTaches()));

    }


    private List<Tache> createTache(int longeur){
        List<Tache> taches = new ArrayList<>();

        for (int i = 0; i < longeur; i++) {
            taches.add(new Tache("Tache "+ i, i*60));
        }

        return taches;
    }
}