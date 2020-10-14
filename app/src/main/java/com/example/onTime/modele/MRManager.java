package com.example.onTime.modele;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant toutes les morning routines ainsi que l'heure d'arrivée du prochain trajet.
 */
public class MRManager {
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
        this.heureArrivee = heureArrivee; // 11h en secondes
        this.listMRA = listMRA;
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

    public boolean ajouterMRA(MRA mra){
        return this.listMRA.add(mra);
    }

    public boolean removeMRA(MRA mra){
        return this.listMRA.remove(mra);
    }

    public MRA removeMRA(int index){
        return this.listMRA.remove(index);
    }
}
