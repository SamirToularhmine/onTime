package com.example.onTime;

import android.util.Log;

import com.example.onTime.modele.Toolbox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class GoogleMapsAPI implements Callable<Integer> {

    int arrivalTime, departureTimeWithoutTraffic;
    String adresseDepart, adresseArrivee;

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
        StringBuilder res = new StringBuilder()
        .append("https://maps.googleapis.com/maps/api/distancematrix/json")
        .append("?origins=").append(this.adresseDepart.replaceAll(" ", "+"))
        .append("&destinations=").append(this.adresseArrivee.replaceAll(" ", "+"))
        .append("&arrival_time=").append(Toolbox.getDateFromHeureArrivee(this.arrivalTime))
        .append("&key=AIzaSyAtz4xXytziDpkNHU12fYqAvjf_0NY5TxY");
        return res.toString();
    }

    /**
     * Méthode qui retourne l'URL pour interroger l'API Google Maps afin de récupérer l'estimation du temps de trajet
     * en prenant en compte le trafic (de façon pessimiste)
     * @param departureTime l'heure de départ sous la forme du nombre de secondes depuis le 1 janvier 1970 (UTC)
     * @return l'URL correspondante sous la forme d'un String
     */
    private String buildURLWithDepartureTimeTraffic(int departureTime) {
        StringBuilder res = new StringBuilder()
            .append("https://maps.googleapis.com/maps/api/distancematrix/json")
            .append("?origins=").append(this.adresseDepart.replaceAll(" ", "+"))
            .append("&destinations=").append(this.adresseArrivee.replaceAll(" ", "+"))
            .append("&departure_time=").append(departureTime)
            .append("&traffic_model=pessimistic")
            .append("&key=AIzaSyAtz4xXytziDpkNHU12fYqAvjf_0NY5TxY");
        return res.toString();
    }

    /**
     * Méthode qui demande à l'API Google le temps nécessaire pour aller d'un point A à un point B
     * à partir de l'heure d'arrivée (attribut de la classe)
     * @return le temps de trajet en voiture entre les deux adresses (en secondes)
     */
    public int getTravelTimeWithoutTraffic() {
        try {
            URL obj = new URL(buildURLWithArrivalTime());
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            Log.d("responseCode", String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in;
                in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while (true) {
                    if (!((inputLine = in.readLine()) != null)) break;
                    response.append(inputLine);
                }
                in.close();
                Log.d("response", response.toString());
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray rows = jsonObject.getJSONArray("rows");
                JSONArray elements = rows.getJSONObject(0).getJSONArray("elements");

                int travelTimeWithoutTraffic = elements.getJSONObject(0).getJSONObject("duration").getInt("value");
                return travelTimeWithoutTraffic;
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return -1; // si erreur
    }

    public int getTravelTimeWithTraffic(int departureTime) {
        try {
            URL url = new URL(buildURLWithDepartureTimeTraffic(departureTime));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            Log.d("responseCode", String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in;
                in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while (true) {
                    if (!((inputLine = in.readLine()) != null)) break;
                    response.append(inputLine);
                }
                in.close();
                Log.d("response", response.toString());
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray rows = jsonObject.getJSONArray("rows");
                JSONArray elements = rows.getJSONObject(0).getJSONArray("elements");
                int travelTimeWithTraffic = elements.getJSONObject(0).getJSONObject("duration_in_traffic").getInt("value");
                return travelTimeWithTraffic;
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Integer call() throws Exception {
        int travelTimeWithoutTraffic = this.getTravelTimeWithoutTraffic();
        if (travelTimeWithoutTraffic != -1) {
            this.departureTimeWithoutTraffic = this.arrivalTime - travelTimeWithoutTraffic;
        }
        return -1;
    }
}
