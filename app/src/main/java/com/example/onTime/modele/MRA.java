package com.example.onTime.modele;

/**
 * Classe qui represente le lien entre une morning routine et une adresse
 */
public class MRA {

    private MorningRoutine morningRoutine;
    private Adresse adresse;

    /**
     * Constructeur d'une MRA
     * @param morningRoutine est une morning routine existante
     * @param adresse est une adresse existante
     */
    public MRA(MorningRoutine morningRoutine, Adresse adresse) {
        this.morningRoutine = morningRoutine;
        this.adresse = adresse;
    }

    /**
     * Constructeur d'une MRA
     * @param morningRoutine est une morning routine existante
     */
    public MRA(MorningRoutine morningRoutine) {
        this.morningRoutine = morningRoutine;
        this.adresse = null;
    }

    public MorningRoutine getMorningRoutine() {
        return this.morningRoutine;
    }

    public void setMorningRoutine(MorningRoutine morningRoutine) {
        this.morningRoutine = morningRoutine;
    }

    public Adresse getAdresse() {
        return this.adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    @Override
    public String toString() {
        return morningRoutine + " | " + adresse ;
    }


}
