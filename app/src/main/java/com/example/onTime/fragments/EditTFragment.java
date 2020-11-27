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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EditTFragment extends Fragment implements OnMapReadyCallback {

    private Trajet trajet;
    private int positionTrajet;


    private enum MarkerType {
        DEPART,
        ARRIVEE
    }

    private GoogleMap map;
    private Marker depart ;
    private Marker destination ;
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





        // enregistrement même si l'utilisateur ne fait aucune action
        this.trajet.setNom(titreTrajet.getText().toString());
        this.trajet.setAdresseDepart(departTrajet.getText().toString());
        this.trajet.setAdresseArrivee(arriveeTrajet.getText().toString());

        Button retour = view.findViewById(R.id.boutton_retour);

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

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        final EditText depart = getView().findViewById(R.id.editTextDepartTrajet);
        final EditText destination = getView().findViewById(R.id.editTextArriveeTrajet);
        depart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    EditTFragment.this.trajet.setAdresseDepart(depart.getText().toString());
                    depart.clearFocus();
                    Toolbox.hideSoftKeyboard(v);
                    boolean goneWell = placeMarker(depart.getText().toString(), EditTFragment.MarkerType.DEPART);
                    if(!goneWell){
                        Toolbox.showToast(getActivity().getApplicationContext(), getString(R.string.probleme_placement_point), Toast.LENGTH_LONG);
                    }

                }
            }
        });



        depart.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    depart.clearFocus();
                    Toolbox.hideSoftKeyboard(v);
                    EditTFragment.this.trajet.setAdresseDepart(depart.getText().toString());
                    return true;
                }
                return false;
            }
        });


        destination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    destination.clearFocus();
                    Toolbox.hideSoftKeyboard(v);

                    EditTFragment.this.trajet.setAdresseArrivee(destination.getText().toString());
                    boolean goneWell = placeMarker(destination.getText().toString(), EditTFragment.MarkerType.ARRIVEE);
                    if(!goneWell){
                        Toolbox.showToast(getActivity().getApplicationContext(), getString(R.string.probleme_placement_point), Toast.LENGTH_LONG);
                    }
                }
            }
        });

        destination.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
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

    public boolean placeMarker(String name, EditTFragment.MarkerType type){
        Geocoder findMarker = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        try {
            String departInput = name;
            List<Address> resultats = findMarker.getFromLocationName(departInput, 5);
            if(!resultats.isEmpty()){
                Address a = resultats.get(0);
                LatLng latLng = new LatLng(a.getLatitude(), a.getLongitude());

                if(type == EditTFragment.MarkerType.DEPART){
                    if (depart != null)
                        this.depart.remove();
                    this.coordDepart = latLng;
                    this.depart = map.addMarker(new MarkerOptions().position(latLng));
                }else if(type == EditTFragment.MarkerType.ARRIVEE){
                    if (destination != null)
                        this.destination.remove();
                    this.coordDestination = latLng;
                    this.destination = map.addMarker(new MarkerOptions().position(latLng));
                }


                CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);


                if (this.coordDepart != null && this.coordDestination != null){
                    double latitude = (this.coordDepart.latitude + this.coordDestination.latitude) / 2;
                    double longitude = (this.coordDepart.longitude + this.coordDestination.longitude) / 2;
                    latLng = new LatLng(latitude,longitude);
                }

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);

                map.moveCamera(cameraUpdate);


                map.animateCamera(zoom);
            }else{
                return false;
            }
        }catch(IOException ignored){
            return false;
        }
        return true;
    }




}