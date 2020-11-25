package com.example.onTime.modele;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
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
    private List<Long> listeHeuresDebutTaches;

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
     * @param trajet est un trajet existant
     */
    public MRT(MorningRoutine morningRoutine, Trajet trajet) {
        this.morningRoutine = morningRoutine;
        this.trajet = trajet;
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
        long travelTimeInMinutes = Toolbox.getTimeOfTravelWithTraffic(dateHeureArrivee, this.trajet.getAdresseDepart(), this.trajet.getAdresseArrivee()) * 60;
        long dateHeureDepartTrajet = dateHeureArrivee - travelTimeInMinutes;
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

    public Trajet getTrajet() {
        return this.trajet;
    }

    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;
        this.listeHeuresDebutTaches = null; // réinitialisation de la liste des temps car les lieux de départ/arrivée ont changé !
    }

    @Override
    public String toString() {
        return morningRoutine + " | " + trajet;
    }

    public int getId() {
        return id;
    }
}
