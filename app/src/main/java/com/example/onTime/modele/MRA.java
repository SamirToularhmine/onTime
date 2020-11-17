package com.example.onTime.modele;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Classe qui represente le lien entre une morning routine et une adresse
 */
public class MRA implements Parcelable {

    private MorningRoutine morningRoutine;
    private Adresse adresse;
    private long heureArrivee;
    private List<Long> listeHeuresDebutTaches;

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
        this.listeHeuresDebutTaches = null;
    }

    /**
     * Méthode qui va calculer toutes les horaires relatifs à la morning routine, c'est à dire :
     * - L'heure de réveil
     * - L'heure de début de chaque tâche
     * - L'heure où l'utilisateur doit commencer le trajet
     */
    public void calculerToutesLesHoraires() throws ExecutionException, InterruptedException {
        long dateHeureArrivee = Toolbox.getDateFromHeureArrivee(this.heureArrivee);
        long timeOfTravelWithTraffic = Toolbox.getTimeOfTravelWithTraffic(dateHeureArrivee, this.adresse.getAdresseDepart(), this.adresse.getAdresseArrivee());
        long dateHeureDepartTrajet = dateHeureArrivee - timeOfTravelWithTraffic;
        long dateHeureReveil = dateHeureDepartTrajet - this.getTempsTotalTaches();
        this.listeHeuresDebutTaches = new ArrayList<>();
        this.listeHeuresDebutTaches.add(dateHeureReveil);
        long tempsPourSeReveiller = 180; // le temps entre le réveil et la première tâche
        for (Tache tache : this.morningRoutine.getListeTaches()) {
            if (this.listeHeuresDebutTaches.size() == 1) {
                this.listeHeuresDebutTaches.add(this.listeHeuresDebutTaches.get(this.listeHeuresDebutTaches.size()-1) + tache.getDuree() + tempsPourSeReveiller);
            }
            else {
                this.listeHeuresDebutTaches.add(this.listeHeuresDebutTaches.get(this.listeHeuresDebutTaches.size()-1) + tache.getDuree());
            }
        }
        this.listeHeuresDebutTaches.add(dateHeureDepartTrajet);
    }

    /**
     * Méthode qui donne la liste de toutes les horaires relatifs au couple Morning Routine + Trajet
     * Premier élément : heure de réveil
     * Dernier élément : heure où il faut partir de l'adresse de départ
     * Entre les deux : toutes les horaires de début des tâches
     * @return la liste de toutes les horaires comme décrit au dessus
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<Long> getListeHeuresDebutTaches() throws ExecutionException, InterruptedException {
        if (this.listeHeuresDebutTaches == null) {
            this.calculerToutesLesHoraires();
        }
        return this.listeHeuresDebutTaches;
    }

    public Adresse getAdresse() {
        return this.adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
        this.listeHeuresDebutTaches = null; // réinitialisation de la liste des temps car les lieux de départ/arrivée ont changé !
    }

    @Override
    public String toString() {
        return morningRoutine + " | " + adresse;
    }
}
