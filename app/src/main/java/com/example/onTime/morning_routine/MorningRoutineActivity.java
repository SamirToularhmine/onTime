package com.example.onTime.morning_routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.onTime.R;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;
import com.google.gson.Gson;

public class MorningRoutineActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TacheAdapter tacheAdapter;
    private MorningRoutine laMorningRoutine;
    private int positionMorningRoutine;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morning_routine);

        this.recyclerView = findViewById(R.id.tache_recyclerview);


        Intent i = getIntent();
        this.positionMorningRoutine = i.getIntExtra("position", -1);
        this.laMorningRoutine = i.getParcelableExtra("morning_routine");
        if (this.laMorningRoutine == null)
            this.laMorningRoutine = new MorningRoutine("Premiere morning routine");

        final EditText titre = findViewById(R.id.titreMorningRoutine);
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

       /* //Perdre le focus lorsqu'on clique sur ok lors du changement de titre
        titre.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from edittext
                    titre.clearFocus();
                    MorningRoutineActivity.this.laMorningRoutine.setNom(titre.getText().toString());
                    MorningRoutineActivity.this.sauvegarder();
                }
                return false;
            }
        });

        //Enlever le clavier quand on perd le focus
        titre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                    MorningRoutineActivity.this.laMorningRoutine.setNom(titre.getText().toString());
                    MorningRoutineActivity.this.sauvegarder();
                }
            }
        });*/

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            final EditText titre = findViewById(R.id.titreMorningRoutine);
            titre.clearFocus();
        }
        return super.dispatchTouchEvent(ev);
    }

    /*private List<Tache> createTache(int longeur) {
        List<Tache> taches = new ArrayList<>();

        for (int i = 0; i < longeur; i++) {
            taches.add(new Tache("Tache " + i, i * 60));
        }

        return taches;
    }*/

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void finish() {
        Intent i = new Intent();
        i.putExtra("morning_routine", this.laMorningRoutine);
        i.putExtra("position", positionMorningRoutine);
        setResult(RESULT_OK, i);
        super.finish();
    }

   /* @Override
    public void onPause() {
        this.sauvegarder();
        super.onPause();
    }*/
}