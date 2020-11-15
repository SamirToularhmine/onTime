package com.example.onTime.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.onTime.ItemTouchHelperTrajet;
import com.example.onTime.R;
import com.example.onTime.TrajetAdapter;
import com.example.onTime.modele.MRT;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Trajet;
import com.example.onTime.morning_routine.ItemTouchHelperTache;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListTFragment extends Fragment {

    private List<Trajet> listeTrajets;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SharedPreferences sharedPreferences;
    private TrajetAdapter trajetAdapter;
    private int position;

    public ListTFragment() {
        // Required empty public constructor
    }

    public static ListTFragment newInstance() {
        ListTFragment fragment = new ListTFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.position = getArguments().getInt("position");
        return inflater.inflate(R.layout.fragment_list_trajets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = this.getActivity().getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = this.sharedPreferences.getString("listeTrajets", "");
        if (json != "") {
            Type type = new TypeToken<List<Trajet>>(){}.getType();
            this.listeTrajets = gson.fromJson(json, type);
        }
        else {
            this.listeTrajets = new ArrayList<>();
            this.listeTrajets.add(new Trajet("Orléans - Marseille", "45000 Orléans", "13000 Marseille"));
            this.listeTrajets.add(new Trajet("Orléans - Paris", "45000 Orléans", "75000 Paris"));
        }

        this.recyclerView = view.findViewById(R.id.trajet_recyclerview);

        this.layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.trajetAdapter = new TrajetAdapter(this.listeTrajets, this.position);
        this.recyclerView.setAdapter(this.trajetAdapter);

        ItemTouchHelperTrajet itemTouchHelperTrajet = new ItemTouchHelperTrajet(getActivity(), this.trajetAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperTrajet);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creerNouveauTrajet(v, new Trajet("Nouveau trajet", "depart", "arrivee"));
            }
        });

    }

    public void creerNouveauTrajet(View view, Trajet trajet) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("trajet", trajet);
        bundle.putInt("position", -1);

        AppCompatActivity activity = (AppCompatActivity) view.getContext();

        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.editTFragment, bundle);
    }

    @Override
    public void onResume() {
        Context context1 = getActivity().getApplicationContext();
        this.sharedPreferences = context1.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);

        Trajet trajet;
        int position = this.sharedPreferences.getInt("position", -2);

        Gson gson = new Gson();
        String json = this.sharedPreferences.getString("trajet", "");
        assert json != null;
        if (!json.equals("")) {
            trajet = gson.fromJson(json, Trajet.class);
            Log.d("Trajet recup sharedPREF", trajet.getNom()+trajet.getAdresseDepart()+trajet.getAdresseArrivee());
            if (position == -1) {
                this.listeTrajets.add(trajet);
                trajetAdapter.notifyItemInserted(this.listeTrajets.size());
            } else {
                if (position >= 0) {
                    this.listeTrajets.set(position, trajet);
                    trajetAdapter.notifyItemChanged(position);
                }
            }
        }
        this.sharedPreferences.edit()
                .remove("trajet")
                .remove("position")
                .apply();

        super.onResume();
    }

    @Override
    public void onStop() {
        Context context = this.getActivity().getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(this.listeTrajets);
        editor.putString("listeTrajets", json);
        editor.apply();

        super.onStop();
    }
}