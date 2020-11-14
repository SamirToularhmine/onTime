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
    private List<MRA> listMRA;

    /**
     * Constructeur par défaut d'un MRManager qui crée une ArrayList et met l'heure d'arrivée à 11h
     */
    public MRManager() {
        this.heureArrivee = 39600; // 11h en secondes
        this.listMRA = new ArrayList<>();
    }

    public MRManager(long heureArrivee, List<MRA> listMRA) {
        this.heureArrivee = heureArrivee;
        this.listMRA = listMRA;
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
            listMRA = new ArrayList<>();
            in.readList(listMRA, MRA.class.getClassLoader());
        } else {
            listMRA = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(heureArrivee);
        if (listMRA == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(listMRA);
        }
    }

    public long getHeureArrivee() {
        return this.heureArrivee;
    }

    public void setHeureArrivee(long heureArrivee) {
        this.heureArrivee = heureArrivee;
    }

    public List<MRA> getListMRA() {
        return listMRA;
    }

    public boolean ajouterMRA(MRA mra) {
        return this.listMRA.add(mra);
    }

    public boolean ajouterMorningRoutine(MorningRoutine morningRoutine) {
        MRA mra = new MRA(morningRoutine, null, 0);
        return this.listMRA.add(mra);
    }

    public boolean removeMRA(MRA mra) {
        return this.listMRA.remove(mra);
    }

    public MRA removeMRA(int index) {
        return this.listMRA.remove(index);
    }


}
