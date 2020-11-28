package com.example.onTime.modele;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Classe Trajet composée du nom qui sera affiché et des adresses de départ et d'arrivée
 */
public class Trajet implements Parcelable {

    private String nom;
    private String adresseDepart;
    private String adresseArrivee;
    private LatLng coordDepart;
    private LatLng coordDestination;

    /**
     * Constructeur pour le nom et les adresses
     *
     * @param nom            est le nom donné à un trajet
     * @param adresseDepart  est l'adresse complète de départ
     * @param adresseArrivee est l'adresse complète d'arrivée
     */
    public Trajet(String nom, String adresseDepart, String adresseArrivee, LatLng coordDepart, LatLng coordDestination) {
        this.nom = nom;
        this.adresseDepart = adresseDepart;
        this.adresseArrivee = adresseArrivee;
        this.coordDepart = coordDepart;
        this.coordDestination = coordDestination;
    }

    protected Trajet(Parcel in) {
        nom = in.readString();
        adresseDepart = in.readString();
        adresseArrivee = in.readString();
        coordDepart = (LatLng) in.readValue(LatLng.class.getClassLoader());
        coordDestination = (LatLng) in.readValue(LatLng.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nom);
        dest.writeString(adresseDepart);
        dest.writeString(adresseArrivee);
        dest.writeValue(coordDepart);
        dest.writeValue(coordDestination);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Trajet> CREATOR = new Parcelable.Creator<Trajet>() {
        @Override
        public Trajet createFromParcel(Parcel in) {
            return new Trajet(in);
        }

        @Override
        public Trajet[] newArray(int size) {
            return new Trajet[size];
        }
    };

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresseDepart() {
        return adresseDepart;
    }

    public void setAdresseDepart(String adresseDepart) {
        this.adresseDepart = adresseDepart;
    }

    public String getAdresseArrivee() {
        return adresseArrivee;
    }

    public void setAdresseArrivee(String adresseArrivee) {
        this.adresseArrivee = adresseArrivee;
    }

    public LatLng getCoordDepart() {
        return coordDepart;
    }

    public void setCoordDepart(LatLng coordDepart) {
        this.coordDepart = coordDepart;
    }

    public LatLng getCoordDestination() {
        return coordDestination;
    }

    public void setCoordDestination(LatLng coordDestination) {
        this.coordDestination = coordDestination;
    }

    @Override
    public String toString() {
        return nom;
    }


}
