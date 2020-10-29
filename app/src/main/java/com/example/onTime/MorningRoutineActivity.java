package com.example.onTime;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onTime.modele.Adresse;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;

import java.util.ArrayList;
import java.util.List;

public class MorningRoutineActivity extends AppCompatActivity {

    private ListView listView;
    private MorningRoutine laMorningRoutine;
    private TacheAdapter tacheAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morning_routine);
        laMorningRoutine = new MorningRoutine("Premiere Morning Routine", this.createTache(5));
        TextView nom = (TextView) findViewById(R.id.titreMorningRoutine);
        nom.setText(laMorningRoutine.getNom());

        this.listView = findViewById(R.id.tacheList);
        this.tacheAdapter = new TacheAdapter(this, laMorningRoutine.getListeTaches());
        this.listView.setAdapter(this.tacheAdapter);

    }


    private List<Tache> createTache(int longeur) {
        List<Tache> taches = new ArrayList<>();

        for (int i = 0; i < longeur; i++) {
            taches.add(new Tache("Tache " + i, i * 60));
        }

        return taches;
    }


    public void creerTache(View view) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.ajout_tache, null);

        final EditText nomTache = (EditText) textEntryView.findViewById(R.id.nomtache);
        final NumberPicker duree = (NumberPicker) textEntryView.findViewById(R.id.duree);
        duree.setMinValue(0);
        duree.setMaxValue(60);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Enter the Text:")
                .setView(textEntryView)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Tache t = new Tache(nomTache.getText().toString(),duree.getValue()*60);
                                laMorningRoutine.ajouterTache(t);
                                tacheAdapter.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        });
        alert.show();


    }
}