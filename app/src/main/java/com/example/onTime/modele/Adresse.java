package com.example.onTime.modele;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Classe Adresse composée du nom qui sera affiché et de l'adresse
 */
public class Adresse implements Parcelable{

    private String nom;
    private String adresseDepart;
    private String adresseArrivee;

    /**
     * Constructeur pour le nom et l'adresse
     * @param nom est le nom donné à une adresse
     * @param adresseDepart est l'adresse complète de départ
     * @param adresseArrivee est l'adresse complète d'arrivée
     */
    public Adresse(String nom, String adresseDepart, String adresseArrivee) {
        this.nom = nom;
        this.adresseDepart = adresseDepart;
        this.adresseArrivee = adresseArrivee;
    }

    protected Adresse(Parcel in) {
        nom = in.readString();
        adresseDepart = in.readString();
        adresseArrivee = in.readString();
    }

    public static final Creator<Adresse> CREATOR = new Creator<Adresse>() {
        @Override
        public Adresse createFromParcel(Parcel in) {
            return new Adresse(in);
        }

        @Override
        public Adresse[] newArray(int size) {
            return new Adresse[size];
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

    public String getAdresse() {
        return this.adresseArrivee;
    }

    public void setAdresse(String adresse) {
        this.adresseArrivee = adresse;
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
