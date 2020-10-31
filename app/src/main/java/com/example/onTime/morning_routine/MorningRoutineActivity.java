package com.example.onTime.morning_routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.onTime.R;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;

import java.util.ArrayList;
import java.util.List;

public class MorningRoutineActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TacheAdapter tacheAdapter;
    private MorningRoutine laMorningRoutine;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morning_routine);

        this.recyclerView = findViewById(R.id.tache_recyclerview);


        Intent i = getIntent();
        this.laMorningRoutine = i.getParcelableExtra("morning_routine");
        if (this.laMorningRoutine == null)
            this.laMorningRoutine = new MorningRoutine("Premiere Mornign routine", this.createTache(10));

        TextView titre = findViewById(R.id.titreMorningRoutine);
        titre.setText(this.laMorningRoutine.getNom());

        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.tacheAdapter = new TacheAdapter(this.laMorningRoutine.getListeTaches());
        this.recyclerView.setAdapter(this.tacheAdapter);

        // On sépare chaque ligne de notre liste par un trait
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // drag and drop + swipe
        ItemTouchHelperTache itemTouchHelperTache = new ItemTouchHelperTache(this, this.tacheAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperTache);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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

        final EditText nomTache = textEntryView.findViewById(R.id.nomtachecreate);
        final NumberPicker duree = textEntryView.findViewById(R.id.duree);
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