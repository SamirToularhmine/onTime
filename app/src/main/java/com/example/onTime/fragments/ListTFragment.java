package com.example.onTime.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onTime.item_touch_helpers.ItemTouchHelperTrajet;
import com.example.onTime.R;
import com.example.onTime.adapters.TrajetAdapter;
import com.example.onTime.modele.Trajet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class  ListTFragment extends Fragment {

    private List<Trajet> listeTrajets;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SharedPreferences sharedPreferences;
    private TrajetAdapter trajetAdapter;
    private int position;
    private boolean onlyShowList; // false si l'utilisateur veut associer un trajet à une morning routine, true sinon

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
       if(getArguments() != null && getArguments().containsKey("position")){
           this.position = getArguments().getInt("position");
           this.onlyShowList = false; // si on peut récupérer l'argument position cela signifie qu'il faut montrer les boutons "sélectionner"
       }else{
           this.position = -1;
           this.onlyShowList = true;
       }
        return inflater.inflate(R.layout.fragment_list_trajets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = this.getActivity().getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = this.sharedPreferences.getString("listeTrajets", "");
        if (!json.equals("")) {
            Type type = new TypeToken<List<Trajet>>(){}.getType();
            this.listeTrajets = gson.fromJson(json, type);
        }
        else {
            this.listeTrajets = new ArrayList<>();
        }

        this.recyclerView = view.findViewById(R.id.trajet_recyclerview);

        this.layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.trajetAdapter = new TrajetAdapter(this.listeTrajets, this.position, this.onlyShowList);
        this.recyclerView.setAdapter(this.trajetAdapter);

        ItemTouchHelperTrajet itemTouchHelperTrajet = new ItemTouchHelperTrajet(getActivity(), this.trajetAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperTrajet);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creerNouveauTrajet(v, new Trajet("", "", ""));
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