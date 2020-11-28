package com.example.onTime.modele;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Classe pour représenter une tache ainsi que sa durée en secondes
 */
public class Tache implements Parcelable {

    private String nom;
    private long duree; // en secondes

    /**
     * Construction d'une tache avec son nom et sa durée
     *
     * @param nom   est le nom de la tâche
     * @param duree est la durée de la tache
     */
    public Tache(String nom, long duree) {
        this.nom = nom;
        this.duree = duree;
    }

    protected Tache(Parcel in) {
        nom = in.readString();
        duree = in.readLong();
    }

    public static final Creator<Tache> CREATOR = new Creator<Tache>() {
        @Override
        public Tache createFromParcel(Parcel in) {
            return new Tache(in);
        }

        @Override
        public Tache[] newArray(int size) {
            return new Tache[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.nom);
        parcel.writeLong(this.duree);
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public long getDuree() {
        return this.duree;
    }

    public void setDuree(long duree) {
        this.duree = duree;
    }

}
