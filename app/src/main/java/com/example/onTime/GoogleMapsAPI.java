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

    public int getDepartureTimeWithTraffic(int departureTime) {
        return 0;
    }

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

                this.departureTimeWithoutTraffic = this.arrivalTime - elements.getJSONObject(0).getJSONObject("duration").getInt("value");
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return -1; // si erreur
    }

    @Override
    public Integer call() throws Exception {
        return this.getTravelTimeWithoutTraffic();
    }
}
