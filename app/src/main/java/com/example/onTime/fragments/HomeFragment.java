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
import com.example.onTime.modele.TacheHeureDebut;
import com.example.onTime.modele.Toolbox;
import com.example.onTime.modele.Trajet;
import com.example.onTime.modele.MRT;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.adapters.HomeTacheAdapter;
import com.example.onTime.adapters.HomeMorningRoutineAdressAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

/**
 * Fragment de la page d'acceuil
 */
public class HomeFragment extends Fragment {

    private MRT mrt; // est la morning routine trajet actuelle
    private RecyclerView recyclerView; // recycler view pour les t??ches
    private RecyclerView.LayoutManager layoutManager; // manager de layout
    private HomeTacheAdapter tacheAdapter; // adapter pour gerer les taches
    private SharedPreferences sharedPreferences;
    private TextView heureArrivee; //text voew de l'heure d'arrivee
    private MRManager mrManager; // est le morning routine manager global
    private MaterialButton heureReveil; // est l'heure de r??veil
    private TextView nomTrajet, titre; // les textveix
    private List<TacheHeureDebut> listeTachesHeuresDebut; // la liste des t??ches avec mes heures de d??but
    private int travelMode; // le type de trajet
    private int wakeUpTime;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = this.getActivity().getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonMRA = this.sharedPreferences.getString("CurrentMRA", ""); // on r??cup??re la MRA active
        this.mrt = gson.fromJson(jsonMRA, MRT.class);
        this.travelMode = this.sharedPreferences.getInt("ridingMethod", 0);

        int idCurrentMRA = this.sharedPreferences.getInt("current_id_MRA", -1); // l'id de la MRA active

        String jsonMRManager = this.sharedPreferences.getString("MRManager", ""); // on r??cupere le MRManager
        if (!jsonMRManager.equals("")) {
            this.mrManager = gson.fromJson(jsonMRManager, MRManager.class);
        } else {
            this.mrManager = new MRManager();
        }

        if (idCurrentMRA == -1) {
            MorningRoutine mr = new MorningRoutine("");
            Trajet t = new Trajet("", "", "", null, null); // delete ??a ???????????

            this.mrt = new MRT(mr, t);
        } else {
            this.mrt = mrManager.getMRAfromId(idCurrentMRA); // on r??cupere la MRT depuis le mrManager
        }

        this.updateMapTachesHeuresDebut(); // ?? placer avant la d??claration du tacheAdapter

        this.tacheAdapter = new HomeTacheAdapter(this.listeTachesHeuresDebut, this.mrt);

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        this.recyclerView = rootView.findViewById(R.id.tache_recyclerview);

        this.layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.recyclerView.setAdapter(this.tacheAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.createNotificationChannel();

        this.heureReveil = view.findViewById(R.id.setAlarm);

        if (this.mrt.getMorningRoutine().getListeTaches().isEmpty()) {
            this.hideRecyclerView();
        } else {
            this.showRecyclerView();
        }

        CardView cardView = view.findViewById(R.id.card_mr_trajet);
        final MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(HomeFragment.this.getContext());
        final HomeMorningRoutineAdressAdapter adapter = new HomeMorningRoutineAdressAdapter(HomeFragment.this.getContext(), HomeFragment.this.mrManager.getListMRT(), HomeFragment.this);

        alert.setTitle(R.string.choisir_morning_routine)

                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeNotifs();
                        adapter.choisirMRT(which);
                        HomeFragment.this.updateHeureReveil();
                        HomeFragment.this.updateMapTachesHeuresDebut();
                        HomeFragment.this.tacheAdapter.setListeTachesHeuresDebut(HomeFragment.this.listeTachesHeuresDebut);
                        HomeFragment.this.tacheAdapter.notifyDataSetChanged();
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
        if (this.mrt.getMorningRoutine() != null) {
            this.titre.setText(this.mrt.getMorningRoutine().getNom());
        } else {
            this.titre.setText(R.string.aucune_mr_definie);
        }

        if (this.mrt.getTrajet() != null)
            this.nomTrajet.setText(this.mrt.getTrajet().getNom());
        else
            this.nomTrajet.setText(R.string.acun_trajet_defini);

        this.setHeureArrivee(view);
        this.setButtonReveil();
    }

    /**
     * M??thode qui set l'heure d'arriv??e avec sous le format HH:MM
     */
    private void initDisplayHeureArrivee() {
        if (this.mrt != null) {
            if (this.mrt.getHeureArrivee() != 0) {
                long heuredepuisminuit = Toolbox.getHeureFromEpoch(this.mrt.getHeureArrivee());
                int h = Toolbox.getHourFromSecondes(heuredepuisminuit);
                int m = Toolbox.getMinutesFromSecondes(heuredepuisminuit);
                String minutes = m < 10 ? "0" + m : String.valueOf(m);
                String affichage = h + ":" + minutes;
                this.heureArrivee.setText(affichage);
            } else {
                this.heureArrivee.setText("00:00");
            }
        }
    }

    /**
     * M??thode qui set calcule et set l'heure d'arriv??e
     *
     * @param view est la view actuelle
     */
    private void setHeureArrivee(View view) {
        this.heureArrivee = view.findViewById(R.id.heureArrivee);

        this.initDisplayHeureArrivee();

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
                        .setTitleText(R.string.je_veux_arriver_pour).build();

                materialTimePicker.show(getActivity().getSupportFragmentManager(), "fragment_tag");

                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int heure = materialTimePicker.getHour();
                        int minutes = materialTimePicker.getMinute();
                        StringBuilder heureArrivee = new StringBuilder();
                        if (heure < 10) {
                            heureArrivee.append("0").append(heure);
                        } else {
                            heureArrivee.append(heure);
                        }
                        heureArrivee.append(":");
                        if (minutes < 10) {
                            heureArrivee.append("0").append(minutes);
                        } else {
                            heureArrivee.append(minutes);
                        }
                        HomeFragment.this.heureArrivee.setText(heureArrivee);
                        if (HomeFragment.this.mrt != null) {
                            HomeFragment.this.mrt.setHeureArrivee((minutes * 60) + (heure * 3600));
                            HomeFragment.this.mrManager.setHeureArrivee((minutes * 60) + (heure * 3600));
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

    /**
     * M??thode pour lancer le r??veil et kes notifs
     */
    private void setButtonReveil() {
        this.heureReveil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MRT laMrt = HomeFragment.this.mrt;
                try {
                    long heureReveil = laMrt.getHeureReveil(HomeFragment.this.travelMode, HomeFragment.this.wakeUpTime);
                    long heuredepuisminuit = Toolbox.getHeureFromEpoch(heureReveil);
                    int h = Toolbox.getHourFromSecondes(heuredepuisminuit);
                    int m = Toolbox.getMinutesFromSecondes(heuredepuisminuit);
                    setAlarm(h, m);
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
                    boolean shouldNotif = sharedPreferences.getBoolean("notifyOnEachTaskStart", true);
                    if (shouldNotif)
                        removeNotifs();
                    createNotifs(heureReveil);

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    Toast.makeText(HomeFragment.this.getContext(), R.string.aucune_mr_definie, Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    /**
     * M??thode qui cr??e les notifs pour le future, elle reture le offset de la time zone car il
     * faut passer un temps en GMT
     *
     * @param heureReveilEpoch est l'heure de r??veil sous format epoch en secondes
     */
    private void createNotifs(long heureReveilEpoch) {
        long decallageProchaineTache = this.wakeUpTime;

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
        heureReveilEpoch -= (offsetInMillis / 1000);
        int id = 0;
        for (Tache tache : this.mrt.getMorningRoutine().getListeTaches()) {
            Intent intent = new Intent(getActivity(), NotificationBroadcast.class);
            intent.putExtra("CONTEXTE", tache.getNom());
            intent.putExtra("ID", (int) decallageProchaineTache);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), id, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            long l = heureReveilEpoch * 1000 + decallageProchaineTache * 1000; // *1000 car on passe de secondes ?? millisecondes
            alarmManager.set(AlarmManager.RTC_WAKEUP, l, pendingIntent);
            decallageProchaineTache += tache.getDuree();
            id++;
        }
    }


    /**
     * Supprime toutes les notifs qui sont set avec cette t??che
     */
    private void removeNotifs() {
        int id = 0;
        if (mrt != null && this.mrt.getMorningRoutine() != null && this.mrt.getMorningRoutine().getListeTaches() != null) {
            for (Tache tache : this.mrt.getMorningRoutine().getListeTaches()) {
                Intent intent = new Intent(getActivity(), NotificationBroadcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                id++;
            }
        }
    }

    /**
     * Cr??ation du channel pour envoyer des notifacaitons obligatoire ?? partir de la version OREO
     * Inutile dans les versions ant??rieures
     */
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            CharSequence name = "onTimeChannel";
            String description = "Channel for onTIme";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyOnTime", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * M??thode qui set le r??veil
     *
     * @param heures  l'heure du r??veil
     * @param minutes minutes du r??veil
     */
    private void setAlarm(int heures, int minutes) {
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_HOUR, heures);
        i.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        i.putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.creee_par_on_time));
        startActivity(i);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.updateHeureReveil();
        this.updateMapTachesHeuresDebut();
    }

    private void updateAdapterListeTaches() {
        this.tacheAdapter.setListeTachesHeuresDebut(HomeFragment.this.listeTachesHeuresDebut);
        this.tacheAdapter.notifyDataSetChanged();
    }

    private void updateHeureReveil() {
        if (this.mrt != null && this.mrt.getMorningRoutine() != null && this.mrt.getMorningRoutine().getListeTaches().size() > 0) {
            try {
                long heureReveil = Toolbox.getHeureFromEpoch(this.mrt.getHeureReveil(this.travelMode, this.wakeUpTime));
                String heures = String.valueOf(Toolbox.getHourFromSecondes(heureReveil));
                String minutes = String.valueOf(Toolbox.getMinutesFromSecondes(heureReveil));
                if (minutes.length() == 1) {
                    minutes = "0" + minutes;
                }
                String affichage = heures + ":" + minutes;
                this.heureReveil.setText(affichage);
            } catch (InterruptedException | ExecutionException e) {
                Toast.makeText(this.getContext(), R.string.impossible_maj_heure_reveil, Toast.LENGTH_LONG).show();
            }
        } else {
            this.heureReveil.setText("--:--");
        }

    }

    private void updateMapTachesHeuresDebut() {
        if (this.mrt != null && this.mrt.getMorningRoutine() != null && this.mrt.getMorningRoutine().getListeTaches().size() > 0) {
            try {
                List<Tache> listeTaches = this.mrt.getMorningRoutine().getListeTaches();
                List<Long> listeHeuresDebutTaches = this.mrt.getListeHeuresDebutTaches(this.travelMode, this.wakeUpTime);
                this.listeTachesHeuresDebut = new ArrayList<>();
                for (int i = 0; i < listeTaches.size(); i++) {
                    this.listeTachesHeuresDebut.add(new TacheHeureDebut(listeTaches.get(i), listeHeuresDebutTaches.get(i)));
                }
            } catch (ExecutionException | InterruptedException ignored) {

            }
        } else {
            if (this.heureReveil != null)
                this.heureReveil.setText("--:--");
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

    /**
     * M??thode qui permet de changer la MRT actuellemetn s??l??ction??ne
     *
     * @param mrt est la nouvelle MRT
     */
    public void changerCurrentMr(MRT mrt) {
        this.mrt = mrt;
        this.tacheAdapter.setMrt(mrt);
        this.mrt.setHeureArrivee(this.mrManager.getHeureArrivee());
        if (this.mrt.getMorningRoutine() != null) {
            this.titre.setText(this.mrt.getMorningRoutine().getNom());
        } else {
            this.titre.setText(R.string.aucune_mr_definie);
        }

        if (this.mrt.getTrajet() != null) {
            this.nomTrajet.setText(this.mrt.getTrajet().getNom());
            this.updateMapTachesHeuresDebut();
        } else
            this.nomTrajet.setText(R.string.acun_trajet_defini);

        this.tacheAdapter = new HomeTacheAdapter(this.listeTachesHeuresDebut, this.mrt);
        this.recyclerView.setAdapter(this.tacheAdapter);

        if (this.mrt.getMorningRoutine().getListeTaches().isEmpty()) {
            this.hideRecyclerView();
        } else {
            this.showRecyclerView();
            this.updateAdapterListeTaches();
        }

    }

    /**
     * On sauvegarde les donn??es quand on change de fragment
     */
    @Override
    public void onPause() {
        super.onPause();
        this.sharedPreferences = this.getActivity().getApplicationContext().getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(this.mrManager);
        this.sharedPreferences.edit().putString("MRManager", json).apply();
    }

    /**
     * On r??cup??re les donn??es stock??es dans les shared pref
     */
    @Override
    public void onResume() {
        Context context = this.getActivity().getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        int idCurrentMRA = this.sharedPreferences.getInt("current_id_MRA", -1);
        //String jsonMRA = this.sharedPreferences.getString("CurrentMRA", "");

        this.wakeUpTime = this.sharedPreferences.getInt("wakeUpTime", 180);

        String jsonMRManager = this.sharedPreferences.getString("MRManager", "");
        if (!jsonMRManager.equals("")) {
            this.mrManager = gson.fromJson(jsonMRManager, MRManager.class);
        } else {
            this.mrManager = new MRManager();
        }

        this.mrt = mrManager.getMRAfromId(idCurrentMRA);

        if (this.mrt != null) {
            this.mrt.setHeureArrivee(this.mrManager.getHeureArrivee());
            this.updateHeureReveil();
            this.updateMapTachesHeuresDebut();
        }

        this.initDisplayHeureArrivee();

        if (this.mrt == null) {
            this.nomTrajet.setText(R.string.acun_trajet_defini);
            this.titre.setText(R.string.aucune_mr_definie);
        } else {
            if (this.mrt.getMorningRoutine() != null) {
                this.titre.setText(this.mrt.getMorningRoutine().getNom());
                this.updateMapTachesHeuresDebut();
                this.updateAdapterListeTaches();
            } else {
                this.titre.setText(R.string.aucune_mr_definie);
            }

            if (this.mrt.getTrajet() != null)
                this.nomTrajet.setText(this.mrt.getTrajet().getNom());
            else
                this.nomTrajet.setText(R.string.acun_trajet_defini);
        }

        this.travelMode = this.sharedPreferences.getInt("ridingMethod", 0);

        super.onResume();
    }


}