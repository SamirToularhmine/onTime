package com.example.onTime.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.onTime.R;
import com.example.onTime.adapters.TrajetAdapter;
import com.example.onTime.modele.Toolbox;
import com.example.onTime.modele.Trajet;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Fragment du choix du trajet
 */
public class EditTFragment extends Fragment implements OnMapReadyCallback {

    private Trajet trajet; // est le trajet
    private int positionTrajet; // est la position du trajet dans la liste des trajets

    /**
     * Enum pour savoir s'il s'agit d'un point d'arrivé ou de départ
     */
    private enum MarkerType {
        DEPART,
        ARRIVEE
    }

    /**
     * Attrivutes pour gérer le placement du point sur la google map
     */
    private GoogleMap map;
    private Marker depart;
    private Marker destination;
    private LatLng coordDepart;
    private LatLng coordDestination;

    public EditTFragment() {
        // Required empty public constructor
    }

    public static EditTFragment newInstance() {
        EditTFragment fragment = new EditTFragment();

        return fragment;
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
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (this.trajet == null) {
            this.trajet = new Trajet("", "", "");
        }

        // remplissage des champs du "formulaire"
        final EditText titreTrajet = view.findViewById(R.id.editTextTitreTrajet);
        titreTrajet.setText(this.trajet.getNom());

        final EditText departTrajet = view.findViewById(R.id.editTextDepartTrajet);
        departTrajet.setText(this.trajet.getAdresseDepart());

        final EditText arriveeTrajet = view.findViewById(R.id.editTextArriveeTrajet);
        arriveeTrajet.setText(this.trajet.getAdresseArrivee());

        titreTrajet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Clear focus here from edittext
                    titreTrajet.clearFocus();
                    Toolbox.hideSoftKeyboard(v);
                    EditTFragment.this.trajet.setNom(titreTrajet.getText().toString());

                    return true;
                }

                return false;
            }
        });

        titreTrajet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    titreTrajet.clearFocus();
                    Toolbox.hideSoftKeyboard(v);
                    EditTFragment.this.trajet.setNom(titreTrajet.getText().toString());
                }
            }
        });

        // set du trajet
        this.trajet.setNom(titreTrajet.getText().toString());
        this.trajet.setAdresseDepart(departTrajet.getText().toString());
        this.trajet.setAdresseArrivee(arriveeTrajet.getText().toString());

        Button retour = view.findViewById(R.id.boutton_retour);

        // bouton retour pour sauvegarder les données
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTFragment.this.trajet.setNom(titreTrajet.getText().toString());
                Toolbox.hideSoftKeyboard(v);
                sauvegarder();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().popBackStack();
            }
        });

    }

    /**
     * Sauvegarde des donénes dans les shared pref
     */
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

    /**
     * Méthode qui gère l'affichage de la google map
     * @param map est la map créée
     */
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        if (trajet != null) { // permet de placer les points dès le début s'ils éxistent
            if (!this.trajet.getAdresseArrivee().equals(""))
                placeMarker(this.trajet.getAdresseArrivee(), MarkerType.ARRIVEE);
            if (!this.trajet.getAdresseDepart().equals(""))
                placeMarker(this.trajet.getAdresseDepart(), MarkerType.DEPART);
        }

        final EditText depart = getView().findViewById(R.id.editTextDepartTrajet);
        final EditText destination = getView().findViewById(R.id.editTextArriveeTrajet);

        depart.setOnFocusChangeListener(new View.OnFocusChangeListener() { // sauvegarde et placement d'un marquer lors de la perte du focus
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditTFragment.this.trajet.setAdresseDepart(depart.getText().toString());
                    depart.clearFocus();
                    Toolbox.hideSoftKeyboard(v);
                    boolean goneWell = placeMarker(depart.getText().toString(), EditTFragment.MarkerType.DEPART); // on déplace les points lors d'un changement de point de départ

                    if (!goneWell) {
                        Toolbox.showToast(getActivity().getApplicationContext(), getString(R.string.probleme_placement_point), Toast.LENGTH_LONG);
                    }
                }
            }
        });

        depart.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) { // sauvegarde et placement d'un marquer lors du clique sur ok
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    depart.clearFocus();
                    Toolbox.hideSoftKeyboard(v);
                    EditTFragment.this.trajet.setAdresseDepart(depart.getText().toString());
                    boolean goneWell = placeMarker(depart.getText().toString(), EditTFragment.MarkerType.DEPART); // on déplace les points lors d'un changement de point de départ

                    if (!goneWell) {
                        Toolbox.showToast(getActivity().getApplicationContext(), getString(R.string.probleme_placement_point), Toast.LENGTH_LONG);
                    }

                    return true;
                }

                return false;
            }
        });

        destination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { // sauvegarde et placement d'un marquer lors de la perte du focus
                if (!hasFocus) {
                    destination.clearFocus();
                    Toolbox.hideSoftKeyboard(v);
                    EditTFragment.this.trajet.setAdresseArrivee(destination.getText().toString());
                    boolean goneWell = placeMarker(destination.getText().toString(), EditTFragment.MarkerType.ARRIVEE);

                    if (!goneWell) {
                        Toolbox.showToast(getActivity().getApplicationContext(), getString(R.string.probleme_placement_point), Toast.LENGTH_LONG);
                    }
                }
            }
        });

        destination.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) { // sauvegarde et placement d'un marquer lors du clique sur ok
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    destination.clearFocus();
                    Toolbox.hideSoftKeyboard(v);
                    EditTFragment.this.trajet.setAdresseArrivee(destination.getText().toString());

                    return true;
                }

                return false;
            }
        });
    }

    /**
     * Méthode qui place un marqueur sur la google map
     * @param name est le nom du marqueur
     * @param type est le type du marqueur, soit un départ soit une arrivée
     * @return true si le placement s'est bien passé, false sinon
     */
    public boolean placeMarker(String name, EditTFragment.MarkerType type) {
        Geocoder findMarker = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());

        try {
            String departInput = name;
            List<Address> resultats = findMarker.getFromLocationName(departInput, 5);

            if (!resultats.isEmpty()) {
                Address a = resultats.get(0);
                LatLng latLng = new LatLng(a.getLatitude(), a.getLongitude());

                if (type == EditTFragment.MarkerType.DEPART) {
                    if (depart != null)
                        this.depart.remove();
                    this.coordDepart = latLng;
                    this.depart = map.addMarker(new MarkerOptions().position(latLng));
                } else if (type == EditTFragment.MarkerType.ARRIVEE) {
                    if (destination != null)
                        this.destination.remove();
                    this.coordDestination = latLng;
                    this.destination = map.addMarker(new MarkerOptions().position(latLng));
                }

                CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);

                if (this.coordDepart != null && this.coordDestination != null) {
                    LatLngBounds.Builder bc = new LatLngBounds.Builder();
                    bc.include(this.coordDepart).include(this.coordDestination);
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 150));
                }else {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
                    map.moveCamera(cameraUpdate);
                    map.animateCamera(zoom);
                }
            } else {
                return false;
            }
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }
}