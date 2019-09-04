package com.b7anka.hollywoodtracker.Model;

import androidx.annotation.NonNull;
import io.realm.RealmObject;

public class User extends RealmObject
{
    private int id;
    private String username;
    private String fullName;
    private String email;
    private String password;
    private boolean premium;
    private int movies;
    private int tvShows;
    private int recent;
    private int total;
    private String thumbnail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public int getMovies() {
        return movies;
    }

    public void setMovies(int movies) {
        this.movies = movies;
    }

    public int getTvShows() {
        return tvShows;
    }

    public void setTvShows(int tvShows) {
        this.tvShows = tvShows;
    }

    public int getRecent() {
        return recent;
    }

    public void setRecent(int recent) {
        this.recent = recent;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(@NonNull String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
