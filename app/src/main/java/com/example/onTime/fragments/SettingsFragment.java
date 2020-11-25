package com.example.onTime.fragments;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;

import com.example.onTime.R;

import java.util.Objects;

public class SettingsFragment extends Fragment {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Mise à jour de l'état du switch en fonction de la valeur sauvegaardée dans les sharedPreferences
        this.switchNotificationsChaqueTache.setChecked(this.sharedPreferences.getBoolean("notifyOnEachTaskStart", false));

        // Association du listener qui va écouter les changements d'états du switch
        switchNotificationsChaqueTache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putBoolean("notifyOnEachTaskStart", SettingsFragment.this.switchNotificationsChaqueTache.isChecked()).apply();
            }
        });

        // TODO : On click listener du bouton pour afficher une pop up de confirmation avant de supprimer toutes les données utilisateur
        boutonSupprimerDonnees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsFragment.this.showDeleteDataConfirmDialog();
            }
        });
    }

    private void showDeleteDataConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setTitle("Supprimer mes données")
                .setMessage("Etes-vous sûr ?")
                .setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
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
                        .remove("notifyOnEachTaskStart").apply();

            }
        })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Rien
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}