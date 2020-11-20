package com.example.onTime.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.R;
import com.example.onTime.modele.Adresse;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.Toolbox;
import com.example.onTime.morning_routine.HomeTacheAdapter;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment {

    private MRA mra;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private HomeTacheAdapter tacheAdapter;
    private SharedPreferences sharedPreferences;
    private TextView heureReveil, heureArrivee;

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
        this.mra = gson.fromJson(jsonMRA, MRA.class);


        Tache t = new Tache("Tache 1", 12);
        List<Tache> taches = new ArrayList<>();
        taches.add(t);

        MorningRoutine mr = new MorningRoutine("qzdqzdqzd", taches);
        Adresse a = new Adresse("Maison-Face", "Semoy", "Orléans");

        this.mra = new MRA(mr);
        this.mra.setAdresse(a);

        this.heureReveil = view.findViewById(R.id.heureReveil);

        if(this.heureReveil != null){
            //heureReveil.setText(Toolbox.formaterHeure(Toolbox.getHourFromSecondes(this.mrManager.getHeureArrivee()), Toolbox.getMinutesFromSecondes(this.mrManager.getHeureArrivee())));
        }

        this.recyclerView = view.findViewById(R.id.tache_recyclerview);

        this.layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.tacheAdapter = new HomeTacheAdapter(this.mra.getMorningRoutine().getListeTaches());
        this.recyclerView.setAdapter(this.tacheAdapter);

        if(this.mra.getMorningRoutine().getListeTaches().isEmpty()){
            this.hideRecyclerView();
        }else{
            this.showRecyclerView();
        }

        TextView titre = view.findViewById(R.id.titreMorningRoutine);
        titre.setText(this.mra.getMorningRoutine().getNom());

        TextView nomTrajet = view.findViewById(R.id.nom_trajet);
        nomTrajet.setText(this.mra.getAdresse().getNom());

        this.heureArrivee = view.findViewById(R.id.heureArrivee);

        /*final TimePickerDialog dialog = new TimePickerDialog(
                getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    }
                },
                8,
                30,
                true);*/

        heureArrivee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);

                int currHeure = (int) HomeFragment.this.mra.getHeureArrivee() / 3600;
                int currMinutes = (int) (HomeFragment.this.mra.getHeureArrivee() % 3600) / 60;

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
                        String heureArrivee = heure == 0 && minutes == 0 ? "00H00" : heure + "H" + minutes;
                        HomeFragment.this.heureArrivee.setText(heureArrivee);
                        HomeFragment.this.mra.setHeureArrivee((minutes * 60) + (heure * 3600));
                        HomeFragment.this.updateTempsDebutTaches();
                    }
                });

                v.setEnabled(true);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        this.updateTempsDebutTaches();
    }

    private void updateTempsDebutTaches() {
        try {
            Toast.makeText(getView().getContext(), "Calcul du temps des tâches...", Toast.LENGTH_SHORT).show();
            List<Long> listeHeuresDebutTaches = this.mra.getListeHeuresDebutTaches();
            Long secondesEntreMinuitEtReveil = Toolbox.getHeureFromEpoch(listeHeuresDebutTaches.get(0));
            Toast.makeText(getView().getContext(), String.valueOf(secondesEntreMinuitEtReveil), Toast.LENGTH_SHORT).show();
            int heures = Toolbox.getHourFromSecondes(secondesEntreMinuitEtReveil);
            int minutes = Toolbox.getMinutesFromSecondes(secondesEntreMinuitEtReveil);
            String affichageHeureReveil = heures + ":" + minutes;
            this.heureReveil.setText(affichageHeureReveil);
            Toast.makeText(getView().getContext(), "Calcul réussi " + affichageHeureReveil, Toast.LENGTH_SHORT).show();
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(getView().getContext(), "Erreur lors de la récupération du temps des tâches", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideRecyclerView(){
        View v = this.getView();

        LinearLayout emptyTaches = v.findViewById(R.id.empty_taches);

        this.recyclerView.setVisibility(View.GONE);
        emptyTaches.setVisibility(View.VISIBLE);
    }

    private void showRecyclerView(){
        View v = this.getView();

        LinearLayout emptyTaches = v.findViewById(R.id.empty_taches);

        this.recyclerView.setVisibility(View.VISIBLE);
        emptyTaches.setVisibility(View.GONE);
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