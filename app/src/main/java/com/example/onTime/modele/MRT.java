package com.example.onTime.modele;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
     * @param trajet         est un trajet existant
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
        this.heureArrivee = 0;
        this.resetHeureReveilDebutTaches();
        this.resetHeureDebutTrajet();
    }


    /**
     * Constructeur d'une MRA
     *
     * @param morningRoutine est une morning routine existante
     * @param trajet         est un trajet existant
     */
    public MRT(MorningRoutine morningRoutine, Trajet trajet) {
        this.morningRoutine = morningRoutine;
        this.trajet = trajet;
        this.heureArrivee = 0;
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
     * M??thode qui donne la somme du temps des t??ches de la morning routine en premi??re position dans la liste
     *
     * @return le temps total pour effectuer les t??ches (en secondes)
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
     * M??thode qui calcule l'heure de r??veil et chaque heure de d??but des t??ches
     */
    private void calculerHeureReveilTaches(int wakeUpTime) {
        this.heureReveil = this.heureDebutTrajet - this.getTempsTotalTaches() - wakeUpTime;

        this.listeHeuresDebutTaches = new ArrayList<>();

        long decallage = wakeUpTime;

        if (!this.morningRoutine.getListeTaches().isEmpty()) { // on calcule les heures de d??but des t??ches que si y'a des t??ches dans la liste de t??ches !!!
            for (int i = 0; i < this.morningRoutine.getListeTaches().size(); i++) {
                Tache tache = this.morningRoutine.getListeTaches().get(i);
                this.listeHeuresDebutTaches.add(this.heureReveil + decallage);
                decallage += tache.getDuree();
            }
        }
    }

    /**
     * M??thode qui calcule l'heure o?? l'utilisateur doit d??buter son trajet pour ??tre ?? l'heure ?? destination
     */
    private void calculerHeureDebutTrajet(int travelMode) throws ExecutionException, InterruptedException {
        long dateHeureArrivee = Toolbox.getDateFromHeureArrivee(this.heureArrivee); // ajouter la timezone
        long travelTimeInMinutes;
        if (this.trajet != null) {
            travelTimeInMinutes = Toolbox.getTimeOfTravelWithTraffic(dateHeureArrivee, this.trajet, travelMode) * 60;
            // travelTimeInMinutes = 60 * 60;
        } else {
            travelTimeInMinutes = 0;
        }
        this.heureDebutTrajet = dateHeureArrivee - travelTimeInMinutes;
    }

    /**
     * M??thode qui donne la liste de toutes les horaires relatifs au couple Morning Routine + Trajet
     * Premier ??l??ment : heure de r??veil
     * Dernier ??l??ment : heure o?? il faut partir de l'adresse de d??part
     * Entre les deux : toutes les horaires de d??but des t??ches
     *
     * @return la liste de toutes les horaires comme d??crit au dessus
     */
    public List<Long> getListeHeuresDebutTaches(int travelMode, int wakeUpTime) throws ExecutionException, InterruptedException {
        if (this.listeHeuresDebutTaches == null) {
            if (this.heureDebutTrajet == -1) {
                this.calculerHeureDebutTrajet(travelMode);
            }
            this.calculerHeureReveilTaches(wakeUpTime);
        }
        return this.listeHeuresDebutTaches;
    }

    public long getHeureDebutTrajet(int travelMode) throws ExecutionException, InterruptedException {
        if (this.heureDebutTrajet == -1) {
            this.calculerHeureDebutTrajet(travelMode);
        }
        return this.heureDebutTrajet;
    }

    public long getHeureReveil(int travelMode, int wakeUpTime) throws ExecutionException, InterruptedException {
        if (this.heureReveil == -1) {
            if (this.heureDebutTrajet == -1) {
                this.calculerHeureDebutTrajet(travelMode);
            }
            this.calculerHeureReveilTaches(wakeUpTime);
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
