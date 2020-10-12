package com.example.onTime.modele;


import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Classe qui sert de boite à outil
 */
 public final class Toolbox {

    private Toolbox() { }

    static public long getSecondesFromHeureMinute(int heures, int minute) {
        return (heures * 3600 + minute * 60);
    }

    static public int getHourFromSecondes(long secondes){
        return (int)(secondes / 3600);
    }

    static public int getminutesFromSecondes(long secondes){
        return (int)(secondes % 3600);
    }

    static public long getSecondesFromEpoch(Date date) {
        return (date.getTime() / 1000);
    }

    /**
     * Méthode qui prend une heure en secondes et qui retourne la date en epoch en secondes de l'heure d'arivee. Il faut ajouter la timezone à l'heure retournée
     * Ex: S'il est 8h et que j'apelle cette fonction avec 39600 (11h en secondes) alors ça retourne la date d'aujourd'hui à 9h en GMT soit 11h dans la bonne timezone
     * @param arrivee est l'heure d'arivee dans cette timezone
     * @return la date en epoch en secondes de l'heure d'arivee. Il faut ajouter la timezone à l'heure retournée.
     */
    static public long getDateFromHeureArrivee(long arrivee) {
        Calendar rightNow = Calendar.getInstance();
        Calendar heureArriveeAjd = new GregorianCalendar(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DAY_OF_MONTH), Toolbox.getHourFromSecondes(arrivee), Toolbox.getminutesFromSecondes(arrivee));
        long secondesFromEpochArivee = Toolbox.getSecondesFromEpoch(heureArriveeAjd.getTime());

        if (rightNow.after(heureArriveeAjd)) { // si on est après l'heure indiquée aujourd'hui alors on passe au lendemain
            return secondesFromEpochArivee + 86400;
        } else {
            return secondesFromEpochArivee; // date ajourd'hui + heure de 11h + 24h en secondes pour demain
        }
    }


}
