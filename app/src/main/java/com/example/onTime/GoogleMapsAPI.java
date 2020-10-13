package com.example.onTime;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class GoogleMapsAPI implements Callable<Integer> {

    int arrivalTime; // Le nombre de secondes entre minuit et l'heure d'arrivée voulue par l'utilisateur
    String adresseDepart, adresseArrivee; // Les adresses de départ et d'arrivée

    public GoogleMapsAPI(int arrivalTime, String adresseDepart, String adresseArrivee) {
        this.arrivalTime = arrivalTime;
        this.adresseDepart = adresseDepart;
        this.adresseArrivee = adresseArrivee;
    }

    /**
     * Méthode qui prend en compte les arguments de la classe pour récupérer l'URL
     * @return Un objet String qui représente l'URL pour interroger l'API Google Maps
     * afin de récupérer le temps de trajet sans prendre en compte le traffic
     */
    private String buildURLWithArrivalTime() {
        StringBuilder res = new StringBuilder();
        res.append("https://maps.googleapis.com/maps/api/distancematrix/json");
        res.append("?origins=").append(adresseDepart.replaceAll(" ", "+"));
        res.append("&destinations=").append(adresseArrivee.replaceAll(" ", "+"));
        res.append("&arrival_time=").append(this.getEpochOfArrivalTime());
        res.append("&key=AIzaSyAtz4xXytziDpkNHU12fYqAvjf_0NY5TxY");
        return res.toString();
    }

    public int getEpochOfArrivalTime() {

    }

    public static int getDepartureTimeWithTraffic() {
    }

    public Integer call() {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
            Request request = new Request.Builder()
                .url(this.buildURLWithArrivalTime())
                .method("GET", null)
                .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
