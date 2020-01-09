package es.uvigo.teleco.sleeplug.model;

public class Settings {

    private int     id;
    private String  engineSpeed;
    private String  engineTime;
    private boolean notifications;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEngineSpeed() {
        return engineSpeed;
    }

    public void setEngineSpeed(String engineSpeed) {
        this.engineSpeed = engineSpeed;
    }

    public String getEngineTime() {
        return engineTime;
    }

    public void setEngineTime(String engineTime) {
        this.engineTime = engineTime;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }
}
