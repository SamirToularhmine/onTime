package com.example.onTime.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.onTime.R;
import com.example.onTime.activities.IntroActivity;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Fragment des paramètres
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private SwitchCompat switchNotificationsChaqueTache;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Récupération des sharedPreferences de l'application
        Context context = this.getActivity().getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);

        // Récupération des éléments graphiques du fragment
        this.switchNotificationsChaqueTache = view.findViewById(R.id.switchNotificationsDebutTaches);
        Button boutonSupprimerDonnees = view.findViewById(R.id.boutonSupprimerDonnees);
        Spinner spinnerChoixMoyenLocomotion = view.findViewById(R.id.spinnermoyenlocomotion);
        Button boutonTutoriel = view.findViewById(R.id.boutonTutoriel);

        // Mise à jour de l'état du switch en fonction de la valeur sauvegaardée dans les sharedPreferences
        this.switchNotificationsChaqueTache.setChecked(this.sharedPreferences.getBoolean("notifyOnEachTaskStart", true));

        // Association du listener qui va écouter les changements d'états du switch
        switchNotificationsChaqueTache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putBoolean("notifyOnEachTaskStart", SettingsFragment.this.switchNotificationsChaqueTache.isChecked()).apply();
            }
        });

        //Boutton pour supprimer les données
        boutonSupprimerDonnees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsFragment.this.showDeleteDataConfirmDialog();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.riding_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChoixMoyenLocomotion.setAdapter(adapter);

        spinnerChoixMoyenLocomotion.setSelection(this.sharedPreferences.getInt("ridingMethod", 0));

        spinnerChoixMoyenLocomotion.setOnItemSelectedListener(this);

        //Bouton pour aller au tutoriel
        boutonTutoriel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), IntroActivity.class);
                getActivity().startActivity(i);
            }
        });
    }

    /**
     * Lors de la supression des données, on supprime toutes les shared pref sauf userHasFinishedInitialSetup
     */
    private void showDeleteDataConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.supprimer_donnees)
                .setMessage(R.string.etes_vous_sur)
                .setPositiveButton(R.string.supprimer, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sharedPreferences.edit()
                        .remove("CurrentMRA")
                        .remove("current_id_MRA")
                        .remove("MRManager")
                        //.remove("userHasFinishedInitialSetup")
                        .remove("morning_routine")
                        .remove("position")
                        .remove("trajet")
                        .remove("id_max")
                        .remove("listeTrajets")
                        .remove("listeTachesRec")
                        .remove("notifyOnEachTaskStart").apply();
                Toast.makeText(getContext(), getResources().getString(R.string.settings_toast_data_removed), Toast.LENGTH_LONG).show();
            }
        })
                .setNegativeButton(R.string.annuler, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Rien
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Méthodes pour le spinner de choix du moyen de locomotion

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        this.sharedPreferences.edit().putInt("ridingMethod", i).apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // rien
    }
}