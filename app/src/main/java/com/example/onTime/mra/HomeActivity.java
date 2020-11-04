package com.example.onTime.mra;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.onTime.R;
import com.example.onTime.modele.Adresse;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.Toolbox;
import com.example.onTime.morning_routine.MorningRoutineActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private MRManager mrManager;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MorningRoutineAdressAdapter morningRoutineAdressAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // initialisation des SharedPreferences
        Context context = getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = this.sharedPreferences.getString("MRManager", "");
        if (json.equals("")) {
            //List<MRA> mras = createMRA(18);
            this.mrManager = new MRManager();
        } else {
            this.mrManager = gson.fromJson(json, MRManager.class);
        }

        // Enregistrement du fait que l'utilisateur a passé l'intro, pour plus ne lui montrer ensuite
        boolean userHasFinishedInitialSetup = this.sharedPreferences.getBoolean("userHasFinishedInitialSetup", false);
        if (!userHasFinishedInitialSetup) {
            this.sharedPreferences.edit().putBoolean("userHasFinishedInitialSetup", true).apply();
        }


        this.recyclerView = findViewById(R.id.morning_routine_adress_recycler_view);

        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.morningRoutineAdressAdapter = new MorningRoutineAdressAdapter(mrManager.getListMRA(), this);
        this.recyclerView.setAdapter(this.morningRoutineAdressAdapter);

        //On sépare chaque ligne de notre liste par un trait
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // drag and drop + swipe
        ItemTouchHelperMRA itemTouchHelperTache = new ItemTouchHelperMRA(this, this.morningRoutineAdressAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperTache);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        TextView heureReveil = findViewById(R.id.heureReveil);
        if (heureReveil != null) {
            heureReveil.setText(Toolbox.formaterHeure(Toolbox.getHourFromSecondes(this.mrManager.getHeureArrivee()), Toolbox.getMinutesFromSecondes(this.mrManager.getHeureArrivee())));
        }
    }

    /*private List<MRA> createMRA(int longeur){
        List<MRA> mra = new ArrayList<>();

        for (int i = 0; i < longeur; i++) {
            mra.add(new MRA(new MorningRoutine("Morning Routine " + i), new Adresse("adresse" + i, "depart" + i, "arrivee" + i)));
        }
        Tache t = new Tache("tache 1", 600);
        mra.get(0).getMorningRoutine().ajouterTache(t);

        return mra;
    }

     */

    public void createMorningRoutine(View view) {
        Intent intent = new Intent(this, MorningRoutineActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        Context context = getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);

        MorningRoutine morningRoutine;
        int position = this.sharedPreferences.getInt("position", -2);

        Gson gson = new Gson();
        String json = this.sharedPreferences.getString("morning_routine", "");
        if (json != "") {
            morningRoutine = gson.fromJson(json, MorningRoutine.class);
            if (position == -1) {
                this.mrManager.ajouterMorningRoutine(morningRoutine);
                morningRoutineAdressAdapter.notifyItemInserted(mrManager.getListMRA().size());
            } else {
                if (position >= 0) {
                    MRA mra = this.mrManager.getListMRA().get(position);
                    mra.setMorningRoutine(morningRoutine);
                    morningRoutineAdressAdapter.notifyItemChanged(position);
                }
            }
        }
        this.sharedPreferences.edit()
                .remove("morning_routine")
                .remove("position")
                .apply();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Context context = getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(this.mrManager);
        editor.putString("MRManager", json);
        editor.apply();

        super.onPause();
    }

}