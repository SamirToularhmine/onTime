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
import com.example.onTime.adapters.MorningRoutineAdressAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

public class ListMRFragment extends Fragment {

    private MRManager mrManager;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MorningRoutineAdressAdapter morningRoutineAdressAdapter;
    private SharedPreferences sharedPreferences;

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

        this.morningRoutineAdressAdapter = new MorningRoutineAdressAdapter(mrManager.getListMRT());
        this.recyclerView.setAdapter(this.morningRoutineAdressAdapter);

        // We use a String here, but any type that can be put in a Bundle is supported
        SavedStateHandle handle = NavHostFragment.findNavController(this).getCurrentBackStackEntry().getSavedStateHandle();
        MutableLiveData<MorningRoutine> liveMorningRoutine = handle.getLiveData("morning_routine");
        final MutableLiveData<Integer> livePosition = handle.getLiveData("position");

        liveMorningRoutine.observe(getViewLifecycleOwner(), new Observer<MorningRoutine>() {
            @Override
            public void onChanged(MorningRoutine mr) {
                if(livePosition.getValue() != null){
                    editMR(mr, livePosition.getValue());
                }
            }
        });

        // drag and drop + swipe
        ItemTouchHelperMRT itemTouchHelperTache = new ItemTouchHelperMRT(getActivity(), this.morningRoutineAdressAdapter, this);
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


    public void creerNouvelleMorningRoutine(View view, MorningRoutine morningRoutine) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("morning_routine", morningRoutine);
        bundle.putInt("position", -1);

        AppCompatActivity activity = (AppCompatActivity) view.getContext();

        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.editMRFragment, bundle);
    }

    public void editMR(MorningRoutine mr, int position) {
        if (position == -1 ){
            Context context = this.getActivity().getApplicationContext();
            this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
            int idMax = this.sharedPreferences.getInt("id_max", 0);
            int newIDMax = idMax + 1;
            this.sharedPreferences.edit()
                    .putInt("id_max", newIDMax)
                    .apply();
            this.mrManager.ajouterMorningRoutine(mr, newIDMax);
            morningRoutineAdressAdapter.notifyItemInserted(mrManager.getListMRT().size());
        }else{
            MRT MRT = this.mrManager.getListMRT().get(position);
            MRT.setMorningRoutine(mr);
            morningRoutineAdressAdapter.notifyItemChanged(position);
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
                morningRoutineAdressAdapter.notifyItemInserted(mrManager.getListMRT().size());
            } else {
                if (position >= 0) {
                    MRT MRT = this.mrManager.getListMRT().get(position);
                    MRT.setMorningRoutine(morningRoutine);
                    morningRoutineAdressAdapter.notifyItemChanged(position);
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



    public void sauvegarder(){
        Context context = this.getActivity().getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(this.mrManager);
        editor.putString("MRManager", json);
        editor.apply();
    }
}