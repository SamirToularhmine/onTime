package com.example.onTime.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onTime.R;
import com.example.onTime.modele.MRT;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.item_touch_helpers.ItemTouchHelperMRT;
import com.example.onTime.adapters.MorningRoutineTrajetAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

/**
 * Fragment de la liste des Morning routines
 */
public class ListMRFragment extends Fragment {

    private MRManager mrManager; // le morning routine manager de l'appli
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MorningRoutineTrajetAdapter morningRoutineTrajetAdapter; // adapter pour les MRT
    private SharedPreferences sharedPreferences;

    public ListMRFragment() {
        // Required empty public constructor
    }

    public static ListMRFragment newInstance() {
        return new ListMRFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_m_r, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = this.getActivity().getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonMRManager = this.sharedPreferences.getString("MRManager", "");
        if (!jsonMRManager.equals("")) {
            this.mrManager = gson.fromJson(jsonMRManager, MRManager.class);
        } else {
            this.mrManager = new MRManager();
        }


        this.recyclerView = view.findViewById(R.id.morning_routine_adress_recycler_view);

        this.layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.morningRoutineTrajetAdapter = new MorningRoutineTrajetAdapter(mrManager.getListMRT());
        this.recyclerView.setAdapter(this.morningRoutineTrajetAdapter);

        // We use a String here, but any type that can be put in a Bundle is supported
        SavedStateHandle handle = NavHostFragment.findNavController(this).getCurrentBackStackEntry().getSavedStateHandle();
        MutableLiveData<MorningRoutine> liveMorningRoutine = handle.getLiveData("morning_routine");
        final MutableLiveData<Integer> livePosition = handle.getLiveData("position");

        liveMorningRoutine.observe(getViewLifecycleOwner(), new Observer<MorningRoutine>() {
            @Override
            public void onChanged(MorningRoutine mr) {
                if (livePosition.getValue() != null) {
                    editMR(mr, livePosition.getValue());
                }
            }
        });

        // drag and drop + swipe
        ItemTouchHelperMRT itemTouchHelperTache = new ItemTouchHelperMRT(getActivity(), this.morningRoutineTrajetAdapter, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperTache);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creerNouvelleMorningRoutine(v, new MorningRoutine(""));
            }
        });
    }


    /**
     * Navigation dans le fraglent de création de nouvelle morning routine en passant -1 en position
     *
     * @param view           est la vue actuelle
     * @param morningRoutine est la morning routine a modifier (qui vient d'être créée)
     */
    public void creerNouvelleMorningRoutine(View view, MorningRoutine morningRoutine) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("morning_routine", morningRoutine);
        bundle.putInt("position", -1);

        AppCompatActivity activity = (AppCompatActivity) view.getContext();

        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.editMRFragment, bundle);
    }

    /**
     * Méthode qui pérmet de modifier une morning routine
     *
     * @param mr       est la morning routine
     * @param position est la position de la morning routine dans la liste
     */
    public void editMR(MorningRoutine mr, int position) {
        if (position == -1) {
            Context context = this.getActivity().getApplicationContext();
            this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
            int idMax = this.sharedPreferences.getInt("id_max", 0);
            int newIDMax = idMax + 1;
            this.sharedPreferences.edit()
                    .putInt("id_max", newIDMax)
                    .apply();
            this.mrManager.ajouterMorningRoutine(mr, newIDMax);
            morningRoutineTrajetAdapter.notifyItemInserted(mrManager.getListMRT().size());
        } else {
            MRT MRT = this.mrManager.getListMRT().get(position);
            MRT.setMorningRoutine(mr);
            morningRoutineTrajetAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onResume() {

        Context context1 = getActivity().getApplicationContext();
        this.sharedPreferences = context1.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);

        MorningRoutine morningRoutine;
        int position = this.sharedPreferences.getInt("position", -2);

        Gson gson = new Gson();
        String json = this.sharedPreferences.getString("morning_routine", "");
        if (!json.equals("")) {
            morningRoutine = gson.fromJson(json, MorningRoutine.class);
            if (position == -1) {
                Context context = this.getActivity().getApplicationContext();
                this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
                int idMax = this.sharedPreferences.getInt("id_max", 0);
                int newIDMax = idMax + 1;
                this.sharedPreferences.edit()
                        .putInt("id_max", newIDMax)
                        .apply();
                this.mrManager.ajouterMorningRoutine(morningRoutine, newIDMax);
                morningRoutineTrajetAdapter.notifyItemInserted(mrManager.getListMRT().size());
            } else {
                if (position >= 0) {
                    MRT MRT = this.mrManager.getListMRT().get(position);
                    MRT.setMorningRoutine(morningRoutine);
                    morningRoutineTrajetAdapter.notifyItemChanged(position);
                }
            }
        }
        this.sharedPreferences.edit()
                .remove("morning_routine")
                .remove("position")
                .apply();
        this.sauvegarder();
        super.onResume();
    }

    @Override
    public void onStop() {
        this.sauvegarder();
        super.onStop();
    }

    public void sauvegarder() {
        Context context = this.getActivity().getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(this.mrManager);
        editor.putString("MRManager", json);
        editor.apply();
    }
}