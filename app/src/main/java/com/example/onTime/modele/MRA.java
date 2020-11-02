package com.example.onTime.modele;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Classe qui represente le lien entre une morning routine et une adresse
 */
public class MRA implements Parcelable {

    private MorningRoutine morningRoutine;
    private Adresse adresse;

    /**
     * Constructeur d'une MRA
     *
     * @param morningRoutine est une morning routine existante
     * @param adresse        est une adresse existante
     */
    public MRA(MorningRoutine morningRoutine, Adresse adresse) {
        this.morningRoutine = morningRoutine;
        this.adresse = adresse;
    }

    /**
     * Constructeur d'une MRA
     *
     * @param morningRoutine est une morning routine existante
     */
    public MRA(MorningRoutine morningRoutine) {
        this.morningRoutine = morningRoutine;
        this.adresse = null;
    }

    public static final Parcelable.Creator<MRA> CREATOR = new Parcelable.Creator<MRA>() {
        @Override
        public MRA createFromParcel(Parcel in) {
            return new MRA(in);
        }

        @Override
        public MRA[] newArray(int size) {
            return new MRA[size];
        }
    };

    protected MRA(Parcel in) {
        morningRoutine = (MorningRoutine) in.readValue(MorningRoutine.class.getClassLoader());
        adresse = (Adresse) in.readValue(Adresse.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(morningRoutine);
        dest.writeValue(adresse);
    }


    public MorningRoutine getMorningRoutine() {
        return this.morningRoutine;
    }

    public void setMorningRoutine(MorningRoutine morningRoutine) {
        this.morningRoutine = morningRoutine;
    }

    public Adresse getAdresse() {
        return this.adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    @Override
    public String toString() {
        return morningRoutine + " | " + adresse;
    }
}
