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
    private long heureReveil;
    private List<Long> listeHeuresDebutTaches;
    private long heureDebutTrajet;

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
        this.resetHeureReveilDebutTaches();
        this.resetHeureDebutTrajet();
    }

    /**
     * Constructeur d'une MRA
     *
     * @param morningRoutine est une morning routine existante
     */
    public MRT(MorningRoutine morningRoutine) {
        this.morningRoutine = morningRoutine;
        this.trajet = null;
        this.resetHeureReveilDebutTaches();
        this.resetHeureDebutTrajet();
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
        this.resetHeureReveilDebutTaches();
        this.resetHeureDebutTrajet();
    }


    protected MRT(Parcel in) {
        morningRoutine = (MorningRoutine) in.readValue(MorningRoutine.class.getClassLoader());
        trajet = (Trajet) in.readValue(Trajet.class.getClassLoader());
        heureArrivee = in.readLong();
        id = in.readInt();
        heureReveil = in.readLong();
        if (in.readByte() == 0x01) {
            listeHeuresDebutTaches = new ArrayList<Long>();
            in.readList(listeHeuresDebutTaches, Long.class.getClassLoader());
        } else {
            listeHeuresDebutTaches = null;
        }
        heureDebutTrajet = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(morningRoutine);
        dest.writeValue(trajet);
        dest.writeLong(heureArrivee);
        dest.writeInt(id);
        dest.writeLong(heureReveil);
        if (listeHeuresDebutTaches == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(listeHeuresDebutTaches);
        }
        dest.writeLong(heureDebutTrajet);
    }

    @SuppressWarnings("unused")
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


    public MorningRoutine getMorningRoutine() {
        return this.morningRoutine;
    }

    public void setMorningRoutine(MorningRoutine morningRoutine) {
        this.morningRoutine = morningRoutine;
        this.resetHeureReveilDebutTaches();
    }

    /**
     * Méthode qui donne la somme du temps des tâches de la morning routine en première position dans la liste
     * @return le temps total pour effectuer les tâches (en secondes)
     */
    private long getTempsTotalTaches() {
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
        this.resetHeureReveilDebutTaches();
        this.resetHeureDebutTrajet();
    }

    private void resetHeureReveilDebutTaches() {
        this.heureReveil = -1;
        this.listeHeuresDebutTaches = null;
    }

    private void resetHeureDebutTrajet() {
        this.heureDebutTrajet = -1;
    }

    /**
     * Méthode qui calcule l'heure de réveil et chaque heure de début des tâches
     */
    private void calculerHeureReveilTaches() {
        this.heureReveil = this.heureDebutTrajet - this.getTempsTotalTaches();
        this.listeHeuresDebutTaches = new ArrayList<>();
        long tempsPourSeReveiller = 180; // le temps entre le réveil et la première tâche

        if (!this.morningRoutine.getListeTaches().isEmpty()) { // on calcule les heures de début des tâches que si y'a des tâches dans la liste de tâches !!!
            Tache tachePrecedante = this.morningRoutine.getListeTaches().get(0);
            this.listeHeuresDebutTaches.add(this.heureReveil + tempsPourSeReveiller + tachePrecedante.getDuree());

            for (int i  = 1; i < this.morningRoutine.getListeTaches().size(); i++) {
                Tache tache = this.morningRoutine.getListeTaches().get(i);
                this.listeHeuresDebutTaches.add(this.listeHeuresDebutTaches.get(this.listeHeuresDebutTaches.size() - 1) + tache.getDuree());
            }
        }
    }

    /**
     * Méthode qui calcule l'heure où l'utilisateur doit débuter son trajet pour être à l'heure à destination
     */
    private void calculerHeureDebutTrajet() throws ExecutionException, InterruptedException {
        long dateHeureArrivee = Toolbox.getDateFromHeureArrivee(this.heureArrivee);
        long travelTimeInMinutes;
        if (this.trajet != null) {
            // long travelTimeInMinutes = Toolbox.getTimeOfTravelWithTraffic(dateHeureArrivee, this.trajet.getAdresseDepart(), this.trajet.getAdresseArrivee()) * 60;
            travelTimeInMinutes = 60 * 60;
        } else {
            travelTimeInMinutes = 0;
        }
        this.heureDebutTrajet = dateHeureArrivee - travelTimeInMinutes;
    }

    /**
     * Méthode qui donne la liste de toutes les horaires relatifs au couple Morning Routine + Trajet
     * Premier élément : heure de réveil
     * Dernier élément : heure où il faut partir de l'adresse de départ
     * Entre les deux : toutes les horaires de début des tâches
     * @return la liste de toutes les horaires comme décrit au dessus
     */
    public List<Long> getListeHeuresDebutTaches() throws ExecutionException, InterruptedException {
        if (this.listeHeuresDebutTaches == null) {
            if (this.heureDebutTrajet == -1) {
                this.calculerHeureDebutTrajet();
            }
            this.calculerHeureReveilTaches();
        }
        return this.listeHeuresDebutTaches;
    }

    public long getHeureDebutTrajet() throws ExecutionException, InterruptedException {
        if (this.heureDebutTrajet == -1) {
            this.calculerHeureDebutTrajet();
        }
        return this.heureDebutTrajet;
    }

    public long getHeureReveil() throws ExecutionException, InterruptedException {
        if (this.heureReveil == -1) {
            if (this.heureDebutTrajet == -1) {
                this.calculerHeureDebutTrajet();
            }
            this.calculerHeureReveilTaches();
        }
        return this.heureReveil;
    }

    public Trajet getTrajet() {
        return this.trajet;
    }

    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;
        this.resetHeureReveilDebutTaches();
        this.resetHeureDebutTrajet();
    }

    @Override
    public String toString() {
        return morningRoutine + " | " + trajet;
    }

    public int getId() {
        return id;
    }
}
