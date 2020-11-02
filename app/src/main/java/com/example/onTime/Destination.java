package com.example.onTime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onTime.modele.Adresse;
import com.example.onTime.modele.Toolbox;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.example.onTime.mra.HomeActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Destination extends AppCompatActivity implements OnMapReadyCallback {

    private static final String PROBLEME_PLACEMENT_POINT = "Problème lors du placement du point !";

    private enum MarkerType {
        DEPART,
        ARRIVEE
    };

    private GoogleMap map;
    private Address depart;
    private Address destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;

        EditText depart = findViewById(R.id.depart);
        final EditText destination = findViewById(R.id.destination);

        depart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    EditText depart = v.findViewById(R.id.depart);
                    boolean goneWell = placeMarker(depart.getText().toString(), MarkerType.DEPART);
                    if(!goneWell){
                        Toolbox.showToast(getApplicationContext(), PROBLEME_PLACEMENT_POINT, Toast.LENGTH_LONG);
                    }
                }
            }
        });

        destination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    boolean goneWell = placeMarker(destination.getText().toString(), MarkerType.ARRIVEE);
                    if(!goneWell){
                        Toolbox.showToast(getApplicationContext(), PROBLEME_PLACEMENT_POINT, Toast.LENGTH_LONG);
                    }
                }
            }
        });
    }

    public boolean placeMarker(String name, MarkerType type){
        Geocoder findMarker = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            String departInput = name;
            List<Address> resultats = findMarker.getFromLocationName(departInput, 5);
            if(!resultats.isEmpty()){
                Address a = resultats.get(0);
                LatLng depart = new LatLng(a.getLatitude(), a.getLongitude());
                map.addMarker(new MarkerOptions()
                        .position(depart)
                        .title("Point sur l'adresse de départ !"));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(depart);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

                map.moveCamera(cameraUpdate);
                map.animateCamera(zoom);

                if(type == MarkerType.DEPART){
                    this.depart = a;
                }else if(type == MarkerType.ARRIVEE){
                    this.destination = a;
                }
            }else{
                return false;
            }
        }catch(IOException ignored){
            return false;
        }
        return true;
    }

    public void goHome(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("depart", this.depart);
        intent.putExtra("destination", this.destination);
        startActivity(intent);
    }
}