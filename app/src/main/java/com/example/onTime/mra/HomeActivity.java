package com.example.onTime.mra;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.onTime.R;
import com.example.onTime.fragments.ListMRFragment;
import com.example.onTime.fragments.NextAlarmHeaderFragment;
import com.example.onTime.modele.Adresse;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

   private MRManager mrManager;
    /*private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MorningRoutineAdressAdapter morningRoutineAdressAdapter;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        List<MRA> mras = createMRA(18);
        this.mrManager = new MRManager(39600, mras);

        Bundle bundle = new Bundle();
        bundle.putParcelable("mr_manager", this.mrManager);
        NextAlarmHeaderFragment nextAlarmHeaderFragment = NextAlarmHeaderFragment.newInstance();
        nextAlarmHeaderFragment.setArguments(bundle);

        ListMRFragment listMRFragment = ListMRFragment.newInstance();
        listMRFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.header_fragment, nextAlarmHeaderFragment)
                .commit();

       getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragment, listMRFragment)
                .commit();

        /*List<MRA> mras = createMRA(18);
        this.mrManager = new MRManager(39600, mras);

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
        if(heureReveil != null){
            heureReveil.setText(Toolbox.formaterHeure(Toolbox.getHourFromSecondes(this.mrManager.getHeureArrivee()), Toolbox.getMinutesFromSecondes(this.mrManager.getHeureArrivee())));
        }

        */
        BottomNavigationView menu = findViewById(R.id.bottom_navigation);
        menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.page_1: {
                        Fragment nextAlarmHeaderFragment = NextAlarmHeaderFragment.newInstance();
                        Fragment listMRFragment = ListMRFragment.newInstance();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.header_fragment, nextAlarmHeaderFragment);
                        transaction.replace(R.id.content_fragment, listMRFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    }
                    case R.id.page_2: {
                        /* Fragment editMRHeaderFragment = EditMRHeaderFragment.newInstance();
                        Fragment editMRFragment = EditMRFragment.newInstance();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.header_fragment, editMRHeaderFragment);
                        transaction.replace(R.id.content_fragment, editMRFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();*/
                        break;
                    }
                }
                return true;
            }
        });
    }

    private List<MRA> createMRA(int longeur){
        List<MRA> mra = new ArrayList<>();

        for (int i = 0; i < longeur; i++) {
            mra.add(new MRA(new MorningRoutine("Morning Routine " + i), new Adresse("adresse" + i, "depart" + i, "arrivee" + i)));
        }
        Tache t = new Tache("tache 1", 600);
        mra.get(0).getMorningRoutine().ajouterTache(t);

        return mra;
    }

    /*public void createMorningRoutine(View view) {
        Intent intent = new Intent(this, MorningRoutineActivity.class);
        startActivityForResult(intent, 1);
    }
*/
}