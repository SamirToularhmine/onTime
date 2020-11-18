package com.example.onTime.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.onTime.R;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;
import com.example.onTime.morning_routine.ItemTouchHelperTache;
import com.example.onTime.morning_routine.MorningRoutineActivity;
import com.example.onTime.morning_routine.TacheAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

public class EditMRFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TacheAdapter tacheAdapter;
    private MorningRoutine laMorningRoutine;
    private int positionMorningRoutine;

    public EditMRFragment() {
        // Required empty public constructor
    }

    public static EditMRFragment newInstance() {
        EditMRFragment fragment = new EditMRFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.laMorningRoutine = (MorningRoutine) getArguments().get("morning_routine");
        this.positionMorningRoutine = getArguments().getInt("position");
        sauvegarder();
        return inflater.inflate(R.layout.fragment_edit_m_r, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.recyclerView = view.findViewById(R.id.tache_recyclerview);
        if (this.laMorningRoutine == null)
            this.laMorningRoutine = new MorningRoutine("Premiere morning routine");

        this.layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.tacheAdapter = new TacheAdapter(this.laMorningRoutine.getListeTaches(), this);
        this.recyclerView.setAdapter(this.tacheAdapter);

        // On sépare chaque ligne de notre liste par un trait
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        // drag and drop + swipe
        ItemTouchHelperTache itemTouchHelperTache = new ItemTouchHelperTache(getActivity(), this.tacheAdapter, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperTache);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        FloatingActionButton ct = view.findViewById(R.id.floating_action_button);
        ct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(EditMRFragment.this.getContext());
                final View textEntryView = factory.inflate(R.layout.ajout_tache, null);

                final EditText nomTache = textEntryView.findViewById(R.id.nomtachecreate);
                final NumberPicker duree = textEntryView.findViewById(R.id.duree);
                duree.setMinValue(0);
                duree.setMaxValue(60);

                final AlertDialog.Builder alert = new AlertDialog.Builder(EditMRFragment.this.getContext());

                alert.setTitle("Enter the Text:")
                        .setView(textEntryView)
                        .setPositiveButton("Save",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Tache t = new Tache(nomTache.getText().toString(),duree.getValue()*60);
                                        laMorningRoutine.ajouterTache(t);
                                        tacheAdapter.notifyDataSetChanged();
                                        EditMRFragment.this.sauvegarder();
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
        });

        final EditText titre = view.findViewById(R.id.titreMorningRoutine);
        titre.setText(this.laMorningRoutine.getNom());

        //Perdre le focus lorsqu'on clique sur ok lors du changement de titre
        titre.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from edittext
                    titre.clearFocus();
                    EditMRFragment.this.laMorningRoutine.setNom(titre.getText().toString());
                    sauvegarder();
                }
                return false;
            }
        });

        //Enlever le clavier quand on perd le focus
        titre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditMRFragment.this.laMorningRoutine.setNom(titre.getText().toString());
                    sauvegarder();
                }
            }
        });
    }

    public void sauvegarder() {
        Context context = getActivity().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonMorningRoutine = gson.toJson(this.laMorningRoutine);
        editor.putString("morning_routine", jsonMorningRoutine);
        editor.putInt("position", positionMorningRoutine);
        editor.apply();
    }

    @Override
    public void onStop() {
        //this.sauvegarder();
        super.onStop();
    }

}