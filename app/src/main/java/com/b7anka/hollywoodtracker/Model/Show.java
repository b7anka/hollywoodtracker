package com.b7anka.hollywoodtracker.Model;

import androidx.annotation.NonNull;
import io.realm.RealmObject;

public class Show extends RealmObject
{
    private int id;
    private int idUser;
    private String title;
    private String watchedTime;
    private int episode;
    private int season;
    private String type;
    private boolean completed;
    private String thumbnail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWatchedTime() {
        return watchedTime;
    }

    public void setWatchedTime(String watchedTime) {
        this.watchedTime = watchedTime;
    }

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(@NonNull String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
