package com.example.onTime.modele;


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


}
