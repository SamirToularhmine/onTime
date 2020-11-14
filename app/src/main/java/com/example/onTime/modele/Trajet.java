package com.example.onTime.modele;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Classe Trajet composée du nom qui sera affiché et des adresses de départ et d'arrivée
 */
public class Trajet implements Parcelable{

    private String nom;
    private String adresseDepart;
    private String adresseArrivee;

    /**
     * Constructeur pour le nom et les adresses
     * @param nom est le nom donné à un trajet
     * @param adresseDepart est l'adresse complète de départ
     * @param adresseArrivee est l'adresse complète d'arrivée
     */
    public Trajet(String nom, String adresseDepart, String adresseArrivee) {
        this.nom = nom;
        this.adresseDepart = adresseDepart;
        this.adresseArrivee = adresseArrivee;
    }

    protected Trajet(Parcel in) {
        nom = in.readString();
        adresseDepart = in.readString();
        adresseArrivee = in.readString();
    }

    public static final Creator<Trajet> CREATOR = new Creator<Trajet>() {
        @Override
        public Trajet createFromParcel(Parcel in) {
            return new Trajet(in);
        }

        @Override
        public Trajet[] newArray(int size) {
            return new Trajet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.nom);
        parcel.writeString(this.adresseDepart);
        parcel.writeString(this.adresseArrivee);
    }

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

    @Override
    public String toString() {
        return nom;
    }


}
