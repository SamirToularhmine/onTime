package com.example.onTime.modele;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.concurrent.ExecutionException;

/**
 * Classe qui represente le lien entre une morning routine et une adresse
 */
public class MRA implements Parcelable {

    private MorningRoutine morningRoutine;
    private Adresse adresse;
    private long heureArrivee;

    /**
     * Constructeur d'une MRA
     *
     * @param morningRoutine est une morning routine existante
     * @param adresse        est une adresse existante
     */
    public MRA(MorningRoutine morningRoutine, Adresse adresse, long heureArrivee) {
        this.morningRoutine = morningRoutine;
        this.adresse = adresse;
        this.heureArrivee = heureArrivee;
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
        res -= Toolbox.getTimeOfTravelWithTraffic(dateHeureArrivee, this.adresse.getAdresseDepart(), this.adresse.getAdresseArrivee()); // et le temps de trajet
        return res;
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
