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
import com.example.onTime.modele.Adresse;
import com.example.onTime.modele.MRA;
import com.example.onTime.modele.MRManager;
import com.example.onTime.modele.MorningRoutine;
import com.example.onTime.modele.Tache;
import com.example.onTime.modele.Toolbox;
import com.example.onTime.morning_routine.HomeTacheAdapter;
import com.example.onTime.morning_routine.ItemTouchHelperTache;
import com.example.onTime.morning_routine.TacheAdapter;
import com.example.onTime.mra.HomeMorningRoutineAdressAdapter;
import com.example.onTime.mra.ItemTouchHelperMRA;
import com.example.onTime.mra.MorningRoutineAdressAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private MRA mra;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private HomeTacheAdapter tacheAdapter;
    private SharedPreferences sharedPreferences;
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

        String jsonMRManager = this.sharedPreferences.getString("MRManager", "");
        this.mrManager = gson.fromJson(jsonMRManager, MRManager.class);


        if (jsonMRA.equals("")) {
            MorningRoutine mr = new MorningRoutine("qzdqzdqzd");
            Adresse a = new Adresse("Maison-Fac", "Maison", "Fac");

            this.mra = new MRA(mr);
            this.mra.setAdresse(a);
        }else{
            this.mra = gson.fromJson(jsonMRA, MRA.class);
        }

        TextView heureReveil = view.findViewById(R.id.heureReveil);

        if (heureReveil != null) {
            //heureReveil.setText(Toolbox.formaterHeure(Toolbox.getHourFromSecondes(this.mrManager.getHeureArrivee()), Toolbox.getMinutesFromSecondes(this.mrManager.getHeureArrivee())));
        }

        this.recyclerView = view.findViewById(R.id.tache_recyclerview);

        this.layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.tacheAdapter = new HomeTacheAdapter(this.mra.getMorningRoutine().getListeTaches());
        this.recyclerView.setAdapter(this.tacheAdapter);

        if (this.mra.getMorningRoutine().getListeTaches().isEmpty()) {
            this.hideRecyclerView();
        } else {
            this.showRecyclerView();
        }

        CardView cardView = view.findViewById(R.id.card_mr_trajet);
        final MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(HomeFragment.this.getContext());
        final HomeMorningRoutineAdressAdapter adapter = new HomeMorningRoutineAdressAdapter(HomeFragment.this.getContext(), HomeFragment.this.mrManager.getListMRA(), HomeFragment.this);

        alert.setTitle("Choisir une Morning Routine")

                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.choisirMRA(which);
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
        if (this.mra.getMorningRoutine() != null){
            this.titre.setText(this.mra.getMorningRoutine().getNom());
        }else{
            this.titre.setText("Morning routine pas définie");
        }

        if (this.mra.getAdresse() != null)
            this.nomTrajet.setText(this.mra.getAdresse().getNom());
        else
            this.nomTrajet.setText("pas de trajet défini");


        TextView heureArrivee = view.findViewById(R.id.heureArrivee);

        final TimePickerDialog dialog = new TimePickerDialog(
                getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    }
                },
                8,
                30,
                true);

        heureArrivee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    private void onTimeSet(int newHour, int newMinute) {

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

    public void changerCurrentMr(MRA mra) {
        this.mra = mra;
        View view = this.getView();
        //TextView titre = view.findViewById(R.id.titreMorningRoutine);
        if (this.mra.getMorningRoutine() != null){
            this.titre.setText(this.mra.getMorningRoutine().getNom());
        }else{
            this.titre.setText("Morning routine pas définie");
        }

        if (this.mra.getAdresse() != null)
            this.nomTrajet.setText(this.mra.getAdresse().getNom());
        else
            this.nomTrajet.setText("pas de trajet défini");

        this.tacheAdapter = new HomeTacheAdapter(this.mra.getMorningRoutine().getListeTaches());
        this.recyclerView.setAdapter(this.tacheAdapter);

        if (this.mra.getMorningRoutine().getListeTaches().isEmpty()) {
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
        String jsonMRA = this.sharedPreferences.getString("CurrentMRA", "");
        this.mra = gson.fromJson(jsonMRA, MRA.class);

        String jsonMRManager = this.sharedPreferences.getString("MRManager", "");
        this.mrManager = gson.fromJson(jsonMRManager, MRManager.class);
        if (this.mra == null){
            this.nomTrajet.setText("pas de trajet défini");
            this.titre.setText("Morning routine pas définie");
        }else {
            if (this.mra.getMorningRoutine() != null) {
                this.titre.setText(this.mra.getMorningRoutine().getNom());
            } else {
                this.titre.setText("Morning routine pas définie");
            }

            if (this.mra.getAdresse() != null)
                this.nomTrajet.setText(this.mra.getAdresse().getNom());
            else
                this.nomTrajet.setText("pas de trajet défini");
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