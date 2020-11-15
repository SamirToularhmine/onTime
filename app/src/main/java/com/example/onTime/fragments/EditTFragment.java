package com.example.onTime.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.onTime.R;
import com.example.onTime.TrajetAdapter;
import com.example.onTime.modele.Trajet;
import com.google.gson.Gson;

public class EditTFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TrajetAdapter trajetAdapter;
    private Trajet trajet;
    private int positionTrajet;

    public EditTFragment() {
        // Required empty public constructor
    }

    public static EditTFragment newInstance() {
        EditTFragment fragment = new EditTFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert getArguments() != null;
        this.trajet = (Trajet) getArguments().get("trajet");
        this.positionTrajet = getArguments().getInt("position");
        return inflater.inflate(R.layout.fragment_edit_trajet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (this.trajet == null) {
            this.trajet = new Trajet("nom", "départ", "arrivée");
        }

        // remplissage des champs du "formulaire"

        final EditText titreTrajet = view.findViewById(R.id.editTextTitreTrajet);
        titreTrajet.setText(this.trajet.getNom());

        final EditText departTrajet = view.findViewById(R.id.editTextDepartTrajet);
        departTrajet.setText(this.trajet.getAdresseDepart());

        final EditText arriveeTrajet = view.findViewById(R.id.editTextArriveeTrajet);
        arriveeTrajet.setText(this.trajet.getAdresseArrivee());

        titreTrajet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditTFragment.this.trajet.setNom(titreTrajet.getText().toString());
                    sauvegarder();
                }
            }
        });

        departTrajet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditTFragment.this.trajet.setAdresseDepart(departTrajet.getText().toString());
                    sauvegarder();
                }
            }
        });

        arriveeTrajet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditTFragment.this.trajet.setAdresseArrivee(arriveeTrajet.getText().toString());
                    sauvegarder();
                }
            }
        });

        // enregistrement même si l'utilisateur ne fait aucune action
        this.trajet.setNom(titreTrajet.getText().toString());
        this.trajet.setAdresseDepart(departTrajet.getText().toString());
        this.trajet.setAdresseArrivee(arriveeTrajet.getText().toString());
        this.sauvegarder();

    }

    private void sauvegarder() {
        Context context = getActivity().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("onTimePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonTrajet = gson.toJson(this.trajet);
        editor.putString("trajet", jsonTrajet);
        editor.putInt("position", positionTrajet);
        editor.apply();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}