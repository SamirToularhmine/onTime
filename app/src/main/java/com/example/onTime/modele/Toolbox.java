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

    static public long getSecondesFromEpoch(Date date) {
        return (date.getTime() / 1000);
    }

    static public long getDateFromHeureArrivee(long arrivee) {
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR);
        int minutes = rightNow.get(Calendar.MINUTE);
        System.out.println("heure :" + hour);
        System.out.println("minutes :" + minutes);
        long secondsNow = Toolbox.getSecondesFromHeureMinute(hour, minutes);


        Calendar gregorianCalendar = new GregorianCalendar(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DAY_OF_MONTH), 0, 0);
        Date dateajd = gregorianCalendar.getTime();
        long dateEpoch = Toolbox.getSecondesFromEpoch(dateajd);

        Date date = new Date();
        System.out.println(date);


        if (secondsNow < arrivee) {
            System.out.println("ajd");
            return dateEpoch + arrivee;
        } else {
            System.out.println("demain");
            return dateEpoch + arrivee + 86400; // date ajourd'hui + heure de 11h + 24h en secondes pour demain
        }
    }


}
