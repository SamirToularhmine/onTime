package com.example.onTime.mrt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.onTime.R;
import com.example.onTime.modele.MRT;
import com.example.onTime.modele.Trajet;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

   private MRManager mrManager;
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
            List<MRT> MRTS = createMRT(18);
            this.mrManager = new MRManager(16900, MRTS);
        } else {
            this.mrManager = gson.fromJson(json, MRManager.class);
        }

        this.saveMRManager();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        navController.setGraph(R.navigation.nav_graph);

        // navController.navigate(R.id.listMRFragment, bundle);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }

    private List<MRT> createMRT(int longeur){
        List<MRT> MRT = new ArrayList<>();

        for (int i = 0; i < longeur; i++) {
            MRT.add(new MRT(new MorningRoutine("Morning Routine " + i), new Trajet("trajet" + i, "depart" + i, "arrivee" + i), 0));
        }

        /*Tache t = new Tache("tache 1", 600);
        mra.get(0).getMorningRoutine().ajouterTache(t);*/

        return MRT;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            v.clearFocus();
        }
        return super.dispatchTouchEvent(ev);
    }

    public void hideKeyboard(Activity act) {
        if(act!=null)
            ((InputMethodManager)act.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow((act.getWindow().getDecorView().getApplicationWindowToken()), 0);
    }

   /* public void createMorningRoutine(View view) {
        Intent intent = new Intent(this, MorningRoutineActivity.class);
        startActivityForResult(intent, 1);
    }*/

    private void saveMRManager(){
        Context context = getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(this.mrManager);
        editor.putString("MRManager", json);
        editor.apply();
    }
}