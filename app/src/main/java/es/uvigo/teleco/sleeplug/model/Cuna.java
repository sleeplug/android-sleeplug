package es.uvigo.teleco.sleeplug.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Cuna implements Serializable {

    private int id;
    private int comando;
    private String hora;

    public Cuna() {
        super();
    }

    public Cuna(int id, int comando, String hora) {
        this.id = id;
        this.comando = comando;
        this.hora = hora;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComando() {
        return comando;
    }

    public void setComando(int comando) {
        this.comando = comando;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Calendar getHoraCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(hora));

        return cal;
    }
}
