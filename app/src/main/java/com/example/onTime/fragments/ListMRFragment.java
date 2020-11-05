package com.example.onTime.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.onTime.R;
import com.example.onTime.modele.Adresse;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.Toolbox;
import com.example.onTime.morning_routine.MorningRoutineActivity;
import com.example.onTime.mra.HomeActivity;
import com.example.onTime.mra.ItemTouchHelperMRA;
import com.example.onTime.mra.MorningRoutineAdressAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ListMRFragment extends Fragment {

    private MRManager mrManager;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MorningRoutineAdressAdapter morningRoutineAdressAdapter;

    public ListMRFragment() {
        // Required empty public constructor
    }

    public static ListMRFragment newInstance() {
        ListMRFragment fragment = new ListMRFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mrManager = (MRManager) getArguments().get("mr_manager");
        return inflater.inflate(R.layout.fragment_list_m_r, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.recyclerView = view.findViewById(R.id.morning_routine_adress_recycler_view);

        this.layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.morningRoutineAdressAdapter = new MorningRoutineAdressAdapter(mrManager.getListMRA(), this);
        this.recyclerView.setAdapter(this.morningRoutineAdressAdapter);

        //On sépare chaque ligne de notre liste par un trait
        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);*/

        // drag and drop + swipe
        ItemTouchHelperMRA itemTouchHelperTache = new ItemTouchHelperMRA(getActivity(), this.morningRoutineAdressAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperTache);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MorningRoutineActivity.class);
                ListMRFragment.this.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MorningRoutine morningRoutine = data.getParcelableExtra("morning_routine");
        int position = data.getIntExtra("position", -1);

        if (position == -1 ){
            this.mrManager.ajouterMorningRoutine(morningRoutine);
            morningRoutineAdressAdapter.notifyItemInserted(mrManager.getListMRA().size());
        }else{
            MRA mra = this.mrManager.getListMRA().get(position);
            mra.setMorningRoutine(morningRoutine);
            morningRoutineAdressAdapter.notifyItemChanged(position);
        }
    }
}