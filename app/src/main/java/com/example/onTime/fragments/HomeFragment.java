package com.example.onTime.fragments;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onTime.NotificationBroadcast;
import com.example.onTime.R;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.TacheHeureDebut;
import com.example.onTime.modele.Toolbox;
import com.example.onTime.modele.Trajet;
import com.example.onTime.modele.MRT;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.adapters.HomeTacheAdapter;
import com.example.onTime.adapters.HomeMorningRoutineAdressAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment {

    private MRT mrt;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private HomeTacheAdapter tacheAdapter;
    private SharedPreferences sharedPreferences;
    private TextView heureReveil, heureArrivee;
    private MRManager mrManager;
    private TextView titre, nomTrajet;
    private List<TacheHeureDebut> listeTachesHeuresDebut;

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
        this.createNotificationChannel();

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

        this.heureReveil = view.findViewById(R.id.heureReveil);

        if (this.heureReveil != null) {
            //heureReveil.setText(Toolbox.formaterHeure(Toolbox.getHourFromSecondes(this.mrManager.getHeureArrivee()), Toolbox.getMinutesFromSecondes(this.mrManager.getHeureArrivee())));
        }

        this.recyclerView = view.findViewById(R.id.tache_recyclerview);

        this.layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.updateMapTachesHeuresDebut(); // à placer avant la déclaration du tacheAdapter

        this.tacheAdapter = new HomeTacheAdapter(this.listeTachesHeuresDebut);
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

        this.setHeureArrivee(view);

        this.setButtonReveil(view);



    }

    private void setHeureArrivee(View view){
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
                        HomeFragment.this.updateHeureReveil();
                        HomeFragment.this.updateMapTachesHeuresDebut();
                        HomeFragment.this.tacheAdapter.setListeTachesHeuresDebut(HomeFragment.this.listeTachesHeuresDebut);
                        HomeFragment.this.tacheAdapter.notifyDataSetChanged();
                    }
                });

                v.setEnabled(true);
            }
        });
    }

    private void setButtonReveil(View view){
        Button buttonSetAlarm = view.findViewById(R.id.setAlarm);

        buttonSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MRT laMrt = HomeFragment.this.mrt;
                try {

                    long heureReveil = laMrt.getHeureReveil();

                    long heuredepuisminuit = Toolbox.getHeureFromEpoch(heureReveil);
                    int h = Toolbox.getHourFromSecondes(heuredepuisminuit);
                    int m = Toolbox.getMinutesFromSecondes(heuredepuisminuit);
                    setAlarm(h,m);
                    createNotifs(heureReveil);

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    Toast.makeText(HomeFragment.this.getContext(), "PAS DE MORNING ROUTINE DÉFINIE", Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    private void createNotifs(long heureReveilEpoch){
        long decallageProchaineTache = 180 ;

        for (Tache tache : this.mrt.getMorningRoutine().getListeTaches()) {
            Intent intent = new Intent(getActivity(), NotificationBroadcast.class);
            intent.putExtra("CONTEXTE", tache.getNom());
            intent.putExtra("ID", (int) decallageProchaineTache);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), (int)decallageProchaineTache, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, heureReveilEpoch * 1000 + decallageProchaineTache * 1000, pendingIntent);
            decallageProchaineTache += tache.getDuree();
        }

    }

    private void createNotificationChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            CharSequence name = "onTimeChannel";
            String description = "Channel for onTIme";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = null;
            channel = new NotificationChannel("notifyOnTime", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =  getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setAlarm(int heures, int minutes){
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_HOUR, heures);
        i.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        i.putExtra(AlarmClock.EXTRA_MESSAGE, "Created by onTime");
        startActivity(i);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.updateHeureReveil();
        this.updateMapTachesHeuresDebut();
    }

    private void updateHeureReveil() {
        try {
            long heureReveil = Toolbox.getHeureFromEpoch(this.mrt.getHeureReveil());
            String heures = String.valueOf(Toolbox.getHourFromSecondes(heureReveil));
            String minutes = String.valueOf(Toolbox.getMinutesFromSecondes(heureReveil));
            if (minutes.length() == 1) {
                minutes = "0" + minutes;
            }
            this.heureReveil.setText(heures+":"+minutes);

        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(getContext(), "Vérifiez votre connexion Internet", Toast.LENGTH_LONG).show();
        }
    }

    private void updateMapTachesHeuresDebut() {
        try {
            List<Tache> listeTaches = this.mrt.getMorningRoutine().getListeTaches();
            List<Long> listeHeuresDebutTaches = this.mrt.getListeHeuresDebutTaches();
            this.listeTachesHeuresDebut = new ArrayList<>();
            for (int i = 0; i < listeTaches.size(); i++) {
                this.listeTachesHeuresDebut.add(new TacheHeureDebut(listeTaches.get(i), listeHeuresDebutTaches.get(i)));
            }
        } catch (ExecutionException | InterruptedException e) {

        }
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

        if (this.mrt.getTrajet() != null) {
            this.nomTrajet.setText(this.mrt.getTrajet().getNom());
            this.updateMapTachesHeuresDebut();
        }
        else
            this.nomTrajet.setText("Aucun trajet défini");

        this.tacheAdapter = new HomeTacheAdapter(this.listeTachesHeuresDebut);
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



}