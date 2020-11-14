package com.example.onTime.modele;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Classe qui represente le lien entre une morning routine et un trajet
 */
public class MRT implements Parcelable {

    private MorningRoutine morningRoutine;
    private Trajet trajet;

    /**
     * Constructeur d'une MRA
     *
     * @param morningRoutine est une morning routine existante
     * @param trajet        est un trajet existant
     */
    public MRT(MorningRoutine morningRoutine, Trajet trajet) {
        this.morningRoutine = morningRoutine;
        this.trajet = trajet;
    }

    /**
     * Constructeur d'une MRA
     *
     * @param morningRoutine est une morning routine existante
     */
    public MRT(MorningRoutine morningRoutine) {
        this.morningRoutine = morningRoutine;
        this.trajet = null;
    }

    public static final Parcelable.Creator<MRT> CREATOR = new Parcelable.Creator<MRT>() {
        @Override
        public MRT createFromParcel(Parcel in) {
            return new MRT(in);
        }

        @Override
        public MRT[] newArray(int size) {
            return new MRT[size];
        }
    };

    protected MRT(Parcel in) {
        morningRoutine = (MorningRoutine) in.readValue(MorningRoutine.class.getClassLoader());
        trajet = (Trajet) in.readValue(Trajet.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(morningRoutine);
        dest.writeValue(trajet);
    }


    public MorningRoutine getMorningRoutine() {
        return this.morningRoutine;
    }

    public void setMorningRoutine(MorningRoutine morningRoutine) {
        this.morningRoutine = morningRoutine;
    }

    public Trajet getTrajet() {
        return this.trajet;
    }

    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;
    }

    @Override
    public String toString() {
        return morningRoutine + " | " + trajet;
    }
}
