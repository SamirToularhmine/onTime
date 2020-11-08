package com.example.onTime.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.onTime.R;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.morning_routine.ItemTouchHelperTache;
import com.example.onTime.morning_routine.TacheAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        this.tacheAdapter = new TacheAdapter(this.laMorningRoutine.getListeTaches());
        this.recyclerView.setAdapter(this.tacheAdapter);

        // On sépare chaque ligne de notre liste par un trait
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // drag and drop + swipe
        ItemTouchHelperTache itemTouchHelperTache = new ItemTouchHelperTache(getActivity(), this.tacheAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperTache);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
                }
            }
        });
    }

    private void saveMR(){
        NavHostFragment.findNavController(this).getPreviousBackStackEntry().getSavedStateHandle().set("morning_routine", this.laMorningRoutine);
        NavHostFragment.findNavController(this).getPreviousBackStackEntry().getSavedStateHandle().set("position", this.positionMorningRoutine);
    }

    @Override
    public void onPause() {
        saveMR();
        super.onPause();
    }
}