package com.example.onTime.modele;

public class TacheHeureDebut {
    private Tache tache;
    private Long heureDebut;

    public TacheHeureDebut(Tache tache, Long heureDebut) {
        this.tache = tache;
        this.heureDebut = heureDebut;
    }

    public Tache getTache() {
        return tache;
    }

    public void setTache(Tache tache) {
        this.tache = tache;
    }

    public Long getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(Long heureDebut) {
        this.heureDebut = heureDebut;
    }
}
