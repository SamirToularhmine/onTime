package com.example.onTime.fragments;


import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.Trajet;
import com.example.onTime.modele.MRT;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.Toolbox;
import com.example.onTime.morning_routine.HomeTacheAdapter;
import com.example.onTime.morning_routine.ItemTouchHelperTache;
import com.example.onTime.morning_routine.TacheAdapter;
import com.example.onTime.mrt.HomeMorningRoutineAdressAdapter;
import com.example.onTime.mrt.ItemTouchHelperMRT;
import com.example.onTime.mrt.MorningRoutineAdressAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private MRT mrt;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private HomeTacheAdapter tacheAdapter;
    private SharedPreferences sharedPreferences;
    private TextView heureArrivee;
    private MRManager mrManager;
    private TextView titre, nomTrajet;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = this.getActivity().getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonMRA = this.sharedPreferences.getString("CurrentMRA", "");
        this.mrt = gson.fromJson(jsonMRA, MRT.class);

        //String jsonMRA = this.sharedPreferences.getString("CurrentMRA", "");
        int idCurrentMRA = this.sharedPreferences.getInt("current_id_MRA", -1);

        String jsonMRManager = this.sharedPreferences.getString("MRManager", "");
        if (!jsonMRManager.equals("")) {
            this.mrManager = gson.fromJson(jsonMRManager, MRManager.class);
        } else {
            this.mrManager = new MRManager();
        }


        if (idCurrentMRA == -1) {
            MorningRoutine mr = new MorningRoutine("");
            Trajet t = new Trajet("", "", ""); // delete àa ???????????

            this.mrt = new MRT(mr, t);
        } else {
            this.mrt = mrManager.getMRAfromId(idCurrentMRA);
        }

        TextView heureReveil = view.findViewById(R.id.heureReveil);

        if (heureReveil != null) {
            //heureReveil.setText(Toolbox.formaterHeure(Toolbox.getHourFromSecondes(this.mrManager.getHeureArrivee()), Toolbox.getMinutesFromSecondes(this.mrManager.getHeureArrivee())));
        }

        this.recyclerView = view.findViewById(R.id.tache_recyclerview);

        this.layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.tacheAdapter = new HomeTacheAdapter(this.mrt.getMorningRoutine().getListeTaches());
        this.recyclerView.setAdapter(this.tacheAdapter);

        if (this.mrt.getMorningRoutine().getListeTaches().isEmpty()) {
            this.hideRecyclerView();
        } else {
            this.showRecyclerView();
        }

        CardView cardView = view.findViewById(R.id.card_mr_trajet);
        final MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(HomeFragment.this.getContext());
        final HomeMorningRoutineAdressAdapter adapter = new HomeMorningRoutineAdressAdapter(HomeFragment.this.getContext(), HomeFragment.this.mrManager.getListMRT(), HomeFragment.this);

        alert.setTitle("Choisir une Morning Routine")

                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.choisirMRT(which);
                        dialog.dismiss();
                    }

                });

        final AlertDialog alertDialog = alert.create();
        cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });

        this.titre = view.findViewById(R.id.titreMorningRoutine);
        this.nomTrajet = view.findViewById(R.id.nom_trajet);
        if (this.mrt.getMorningRoutine() != null){
            this.titre.setText(this.mrt.getMorningRoutine().getNom());
        }else{
            this.titre.setText("Aucune morning routine définie");
        }

        if (this.mrt.getTrajet() != null)
            this.nomTrajet.setText(this.mrt.getTrajet().getNom());
        else
            this.nomTrajet.setText("Aucun de trajet défini");


        this.heureArrivee = view.findViewById(R.id.heureArrivee);

        heureArrivee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);

                int currHeure = HomeFragment.this.mrt != null ? (int) HomeFragment.this.mrt.getHeureArrivee() / 3600 : 0;
                int currMinutes = HomeFragment.this.mrt != null ? (int) (HomeFragment.this.mrt.getHeureArrivee() % 3600) / 60 : 0;

                final MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(currHeure)
                        .setMinute(currMinutes)
                        .setTitleText("Je veux arriver pour").build();

                materialTimePicker.show(getActivity().getSupportFragmentManager(), "fragment_tag");

                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int heure = materialTimePicker.getHour();
                        int minutes = materialTimePicker.getMinute();
                        StringBuilder heureArrivee = new StringBuilder();
                        if(heure < 10){
                            heureArrivee.append("0").append(heure);
                        }else{
                            heureArrivee.append(heure);
                        }
                        heureArrivee.append(":");
                        if(minutes < 10){
                            heureArrivee.append("0").append(minutes);
                        }else{
                            heureArrivee.append(minutes);
                        }
                        heureArrivee.append(" H");
                        HomeFragment.this.heureArrivee.setText(heureArrivee);
                        if(HomeFragment.this.mrt != null){
                            HomeFragment.this.mrt.setHeureArrivee((minutes * 60) + (heure * 3600));
                        }
                    }
                });

                v.setEnabled(true);
            }
        });

    }

    private void hideRecyclerView() {
        View v = this.getView();

        LinearLayout emptyTaches = v.findViewById(R.id.empty_taches);

        this.recyclerView.setVisibility(View.GONE);
        emptyTaches.setVisibility(View.VISIBLE);
    }

    private void showRecyclerView() {
        View v = this.getView();

        LinearLayout emptyTaches = v.findViewById(R.id.empty_taches);

        this.recyclerView.setVisibility(View.VISIBLE);
        emptyTaches.setVisibility(View.GONE);
    }

    public void changerCurrentMr(MRT mrt) {
        this.mrt = mrt;
        //TextView titre = view.findViewById(R.id.titreMorningRoutine);
        if (this.mrt.getMorningRoutine() != null){
            this.titre.setText(this.mrt.getMorningRoutine().getNom());
        }else{
            this.titre.setText("Aucune morning routine définie");
        }

        if (this.mrt.getTrajet() != null)
            this.nomTrajet.setText(this.mrt.getTrajet().getNom());
        else
            this.nomTrajet.setText("Aucun trajet défini");

        this.tacheAdapter = new HomeTacheAdapter(this.mrt.getMorningRoutine().getListeTaches());
        this.recyclerView.setAdapter(this.tacheAdapter);

        if (this.mrt.getMorningRoutine().getListeTaches().isEmpty()) {
            this.hideRecyclerView();
        } else {
            this.showRecyclerView();
        }

    }

    @Override
    public void onResume() {
        Context context = this.getActivity().getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        int idCurrentMRA = this.sharedPreferences.getInt("current_id_MRA", -1);
        //String jsonMRA = this.sharedPreferences.getString("CurrentMRA", "");
        String jsonMRManager = this.sharedPreferences.getString("MRManager", "");
        if (!jsonMRManager.equals("")) {
            this.mrManager = gson.fromJson(jsonMRManager, MRManager.class);
        } else {
            this.mrManager = new MRManager();
        }

        this.mrt = mrManager.getMRAfromId(idCurrentMRA);

        if (this.mrt == null){
            this.nomTrajet.setText("Aucun trajet défini");
            this.titre.setText("Aucune morning routine définie");
        }else {
            if (this.mrt.getMorningRoutine() != null) {
                this.titre.setText(this.mrt.getMorningRoutine().getNom());
            } else {
                this.titre.setText("Aucune morning routine définie");
            }

            if (this.mrt.getTrajet() != null)
                this.nomTrajet.setText(this.mrt.getTrajet().getNom());
            else
                this.nomTrajet.setText("Aucun trajet défini");
        }
        super.onResume();
    }



    /*@Override
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
                this.mrManager.ajouterMorningRoutine(morningRoutine);
                morningRoutineAdressAdapter.notifyItemInserted(mrManager.getListMRA().size());
            } else {
                if (position >= 0) {
                    MRA mra = this.mrManager.getListMRA().get(position);
                    mra.setMorningRoutine(morningRoutine);
                    morningRoutineAdressAdapter.notifyItemChanged(position);
                }
            }
        }
        this.sharedPreferences.edit()
                .remove("morning_routine")
                .remove("position")
                .apply();

        super.onResume();
    }*/
}