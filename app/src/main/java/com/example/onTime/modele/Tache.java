package com.example.onTime.modele;

/**
 * Classe pour représenter une tache ainsi que sa durée en secondes
 */
public class Tache {

    private String nom;
    private long duree; // en secondes

    /**
     * Construction d'une tache avec son nom et sa durée
     * @param nom est le nom de la tâche
     * @param duree est la durée de la tache
     */
    public Tache(String nom, long duree) {
        this.nom = nom;
        this.duree = duree;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public long getDuree() {
        return this.duree;
    }

    public void setDuree(long duree) {
        this.duree = duree;
    }
}
