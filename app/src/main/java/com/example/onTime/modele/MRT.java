package com.example.onTime.modele;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Classe qui represente le lien entre une morning routine et un trajet
 */
public class MRT implements Parcelable {

    private MorningRoutine morningRoutine;
    private Trajet trajet;
    private long heureArrivee;
    private int id;

    /**
     * Constructeur d'une MRA
     *
     * @param morningRoutine est une morning routine existante
     * @param trajet        est un trajet existant
     */
    public MRT(MorningRoutine morningRoutine, Trajet trajet, long heureArrivee, int id) {
        this.morningRoutine = morningRoutine;
        this.trajet = trajet;
        this.heureArrivee = heureArrivee;
        this.id = id;
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


    /**
     * Constructeur d'une MRA
     *
     * @param morningRoutine est une morning routine existante
     * @param adresse est une adresse existante
     */
    public MRA(MorningRoutine morningRoutine, Adresse adresse) {
        this.morningRoutine = morningRoutine;
        this.adresse = adresse;
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
        this.morningRoutine = (MorningRoutine) in.readValue(MorningRoutine.class.getClassLoader());
        this.trajet = (Trajet) in.readValue(Trajet.class.getClassLoader());
        this.heureArrivee = in.readLong();
        this.id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.morningRoutine);
        dest.writeValue(this.trajet);
        dest.writeLong(this.heureArrivee);
        dest.writeInt(this.id);
    }


    public MorningRoutine getMorningRoutine() {
        return this.morningRoutine;
    }

    public void setMorningRoutine(MorningRoutine morningRoutine) {
        this.morningRoutine = morningRoutine;
    }

    /**
     * Méthode qui donne la somme du temps des tâches de la morning routine en première position dans la liste
     * @return le temps total pour effectuer les tâches (en secondes)
     */
    public long getTempsTotalTaches() {
        long res = 0;
        for (Tache tache : this.morningRoutine.getListeTaches()) {
            res += tache.getDuree();
        }
        return res;
    }

    public long getHeureArrivee() {
        return this.heureArrivee;
    }

    public void setHeureArrivee(long heureArrivee) {
        this.heureArrivee = heureArrivee;
    }

    /**
     * Méthode qui donne l'heure de réveil en prenant en compte le temps des tâches et le temps de trajet
     * @return l'heure de réveil en secondes par rapport à minuit
     * @throws ExecutionException erreur lors de l'exécution de la requête
     * @throws InterruptedException interruption lors de l'exécution de la requête
     */
    public long getHeureReveil() throws ExecutionException, InterruptedException {
        long res = this.heureArrivee; // on part de l'heure d'arrivée
        res -= this.getTempsTotalTaches(); // on y supprime le temps des tâches
        long dateHeureArrivee = Toolbox.getDateFromHeureArrivee(this.heureArrivee);
        res -= Toolbox.getTimeOfTravelWithTraffic(dateHeureArrivee, this.trajet.getAdresseDepart(), this.trajet.getAdresseArrivee()); // et le temps de trajet
        return res;
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

    public int getId() {
        return id;
    }
}
