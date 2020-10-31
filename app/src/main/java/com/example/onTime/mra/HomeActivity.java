package com.example.onTime.mra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.onTime.R;
import com.example.onTime.modele.Adresse;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Toolbox;
import com.example.onTime.morning_routine.MorningRoutineActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private MRManager mrManager;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MorningRoutineAdressAdapter morningRoutineAdressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        List<MRA> mras = createMRA(18);
        this.mrManager = new MRManager(39600, mras);

        this.recyclerView = findViewById(R.id.morning_routine_adress_recycler_view);

        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.morningRoutineAdressAdapter = new MorningRoutineAdressAdapter(mrManager.getListMRA());
        this.recyclerView.setAdapter(this.morningRoutineAdressAdapter);

        //On sépare chaque ligne de notre liste par un trait
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // drag and drop + swipe
        ItemTouchHelperMRA itemTouchHelperTache = new ItemTouchHelperMRA(this, this.morningRoutineAdressAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperTache);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        TextView heureReveil = findViewById(R.id.heureReveil);
        if(heureReveil != null){
            heureReveil.setText(Toolbox.formaterHeure(Toolbox.getHourFromSecondes(this.mrManager.getHeureArrivee()), Toolbox.getMinutesFromSecondes(this.mrManager.getHeureArrivee())));
        }
    }

    private List<MRA> createMRA(int longeur){
        List<MRA> mra = new ArrayList<>();

        for (int i = 0; i < longeur; i++) {
            mra.add(new MRA(new MorningRoutine("Morning Routine " + i), new Adresse("adresse" + i, "depart" + i, "arrivee" + i)));
        }

        return mra;
    }

    public void createMorningRoutine(View view) {
        Intent intent = new Intent(this, MorningRoutineActivity.class);
        startActivity(intent);

    }
}