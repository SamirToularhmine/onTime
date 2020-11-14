package com.example.onTime.modele;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Classe représentant toutes les morning routines ainsi que l'heure d'arrivée du prochain trajet.
 */
public class MRManager implements Parcelable {
    private long heureArrivee;
    private List<MRT> listMRT;

    /**
     * Constructeur par défaut d'un MRManager qui crée une ArrayList et met l'heure d'arrivée à 11h
     */
    public MRManager() {
        this.heureArrivee = 39600; // 11h en secondes
        this.listMRT = new ArrayList<>();
    }

    public MRManager(long heureArrivee, List<MRT> listMRT) {
        this.heureArrivee = heureArrivee;
        this.listMRT = listMRT;
    }

    public static final Parcelable.Creator<MRManager> CREATOR = new Parcelable.Creator<MRManager>() {
        @Override
        public MRManager createFromParcel(Parcel in) {
            return new MRManager(in);
        }

        @Override
        public MRManager[] newArray(int size) {
            return new MRManager[size];
        }
    };

    protected MRManager(Parcel in) {
        heureArrivee = in.readLong();
        if (in.readByte() == 0x01) {
            listMRT = new ArrayList<>();
            in.readList(listMRT, MRT.class.getClassLoader());
        } else {
            listMRT = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(heureArrivee);
        if (listMRT == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(listMRT);
        }
    }

    /**
     * Méthode qui donne la somme du temps des tâches de la morning routine en première position dans la liste
     * @return le temps total pour effectuer les tâches (en secondes)
     */
    public long getTempsTotalTaches() {
        long res = 0;
        for (Tache tache : this.listMRT.get(0).getMorningRoutine().getListeTaches()) {
            res += tache.getDuree();
        }
        return res;
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
        MRT MRT = this.listMRT.get(0);
        long dateHeureArrivee = Toolbox.getDateFromHeureArrivee(this.heureArrivee);
        res -= Toolbox.getTimeOfTravelWithTraffic(dateHeureArrivee, MRT.getTrajet().getAdresseDepart(), MRT.getTrajet().getAdresseArrivee()); // et le temps de trajet
        return res;
    }

    public long getHeureArrivee() {
        return this.heureArrivee;
    }

    public void setHeureArrivee(long heureArrivee) {
        this.heureArrivee = heureArrivee;
    }

    public List<MRT> getListMRT() {
        return listMRT;
    }

    public boolean ajouterMRA(MRT MRT) {
        return this.listMRT.add(MRT);
    }

    public boolean ajouterMorningRoutine(MorningRoutine morningRoutine) {
        MRT MRT = new MRT(morningRoutine, null);
        return this.listMRT.add(MRT);
    }

    public boolean removeMRA(MRT MRT) {
        return this.listMRT.remove(MRT);
    }

    public MRT removeMRA(int index) {
        return this.listMRT.remove(index);
    }


}
