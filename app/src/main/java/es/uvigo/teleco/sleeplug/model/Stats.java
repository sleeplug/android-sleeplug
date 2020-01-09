package es.uvigo.teleco.sleeplug.model;

import java.util.Date;

public class Stats {

    private Date day;
    private Date timeWorking;

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Date getTimeWorking() {
        return timeWorking;
    }

    public void setTimeWorking(Date timeWorking) {
        this.timeWorking = timeWorking;
    }
}
