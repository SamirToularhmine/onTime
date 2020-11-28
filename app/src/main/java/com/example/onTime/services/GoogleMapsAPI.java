package com.example.onTime.services;

import com.example.onTime.modele.Toolbox;
import com.example.onTime.modele.Trajet;
import com.google.android.gms.maps.model.LatLng;

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

    long arrivalTime;
    LatLng coordDepart, coordArrivee;
    String travelMode;


    public GoogleMapsAPI(long arrivalTime, Trajet trajet, int travelMode) {
        this.arrivalTime = arrivalTime;
        this.coordDepart = trajet.getCoordDepart();
        this.coordArrivee = trajet.getCoordDestination();
        switch (travelMode) {
            case 1: // Vélo
                this.travelMode = "bicycling";
                break;

            case 2: // à pied
                this.travelMode = "walking";
                break;

            default: // voiture
                this.travelMode = "driving";
        }
    }

    /**
     * Méthode qui prend en compte les arguments de la classe pour récupérer l'URL
     * @return Un objet String qui représente l'URL pour interroger l'API Google Maps
     * afin de récupérer le temps de trajet sans prendre en compte le traffic
     */
    private String buildURLWithArrivalTime() {
        StringBuilder res = new StringBuilder()
        .append("https://maps.googleapis.com/maps/api/distancematrix/json")
        .append("?origins=").append(this.coordDepart.latitude+","+this.coordDepart.longitude)
        .append("&destinations=").append(this.coordArrivee.latitude + "," + this.coordArrivee.longitude)
        .append("&arrival_time=").append(Toolbox.getDateFromHeureArrivee(this.arrivalTime))
        .append("&mode=").append(this.travelMode)
        .append("&key=AIzaSyAtz4xXytziDpkNHU12fYqAvjf_0NY5TxY");
        return res.toString();
    }

    /**
     * Méthode qui retourne l'URL pour interroger l'API Google Maps afin de récupérer l'estimation du temps de trajet
     * en prenant en compte le trafic (de façon pessimiste)
     * @param departureTime l'heure de départ sous la forme du nombre de secondes depuis le 1 janvier 1970 (UTC)
     * @return l'URL correspondante sous la forme d'un String
     */
    private String buildURLWithDepartureTimeTraffic(long departureTime) {
        StringBuilder res = new StringBuilder()
            .append("https://maps.googleapis.com/maps/api/distancematrix/json")
            .append("?origins=").append(this.coordDepart.latitude+","+this.coordDepart.longitude)
            .append("&destinations=").append(this.coordArrivee.latitude + "," + this.coordArrivee.longitude)
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
    public long getTravelTimeWithoutTraffic() {
        try {
            URL obj = new URL(buildURLWithArrivalTime());
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
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
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray rows = jsonObject.getJSONArray("rows");
                JSONArray elements = rows.getJSONObject(0).getJSONArray("elements");

                long travelTimeWithoutTraffic = elements.getJSONObject(0).getJSONObject("duration").getLong("value");
                return travelTimeWithoutTraffic;
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return -1; // si erreur
    }

    /**
     * Méthode qui demande à l'API Google Maps le temps nécessaire pour aller du point de départ au point d'arrivée
     * en prenant en compte le trafic, à partir de l'heure de départ donnée en attribut
     * @param departureTime l'heure de départ sous la forme du nombre de secondes depuis le 1er janvier 1970
     * @return le temps de trajet en secondes
     */
    public long getTravelTimeWithTraffic(long departureTime) {
        try {
            URL url = new URL(buildURLWithDepartureTimeTraffic(departureTime));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
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
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray rows = jsonObject.getJSONArray("rows");
                JSONArray elements = rows.getJSONObject(0).getJSONArray("elements");
                long travelTimeWithTraffic = elements.getJSONObject(0).getJSONObject("duration_in_traffic").getLong("value");
                return travelTimeWithTraffic;
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Integer call() throws Exception {
        if (this.travelMode.equals("driving")) { // en voiture, il peut y avoir des bouchons/accidents/poules sur la route
            long tempsTrajetSansTrafic = this.getTravelTimeWithoutTraffic(); // temps de trajet pour arriver à l'heure voulue sans prendre en compte le trafic
            long heureDepart = this.arrivalTime - tempsTrajetSansTrafic;
            long tempsTrajetAvecTrafic = this.getTravelTimeWithTraffic(heureDepart);
            long heureArriveeAvecTrafic = heureDepart + tempsTrajetAvecTrafic;
            while (heureArriveeAvecTrafic > this.arrivalTime) {
                long delta = heureArriveeAvecTrafic - this.arrivalTime;
                if (delta < 60) {
                    break;
                }
                heureDepart = heureDepart - delta; // on retire une minute à l'heure de départ
                tempsTrajetAvecTrafic = this.getTravelTimeWithTraffic(heureDepart);
                heureArriveeAvecTrafic = heureDepart + tempsTrajetAvecTrafic;
            }
            return Toolbox.getMinutesRoundedUpFromSecondes(tempsTrajetAvecTrafic);
        }
        else { // en vélo où à pied, pas de trafic
            return Toolbox.getMinutesRoundedUpFromSecondes(this.getTravelTimeWithoutTraffic());
        }
    }
}
