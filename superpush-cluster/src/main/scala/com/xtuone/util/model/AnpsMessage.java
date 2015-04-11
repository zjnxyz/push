package com.xtuone.util.model;

import java.util.Map;

/**
 * Created by Zz on 2015/1/13.
 */
public class AnpsMessage {

    private int serverId;

    private String alert;

    private int badge;

    private String sound;

    private Map<String, String> extras;



    public AnpsMessage() {
        super();
    }

    public AnpsMessage(int serverId, String alert, int badge) {
        super();
        this.serverId = serverId;
        this.alert = alert;
        this.badge = badge;
    }

    public AnpsMessage(int serverId, String alert, int badge, Map<String, String> extras) {
        super();
        this.serverId = serverId;
        this.alert = alert;
        this.badge = badge;
        this.extras = extras;
    }

    public AnpsMessage(int serverId, String alert, int badge, String sound, Map<String, String> extras) {
        super();
        this.serverId = serverId;
        this.alert = alert;
        this.badge = badge;
        this.sound = sound;
        this.extras = extras;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }
    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }
    public int getServerId() {
        return serverId;
    }
    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

}
