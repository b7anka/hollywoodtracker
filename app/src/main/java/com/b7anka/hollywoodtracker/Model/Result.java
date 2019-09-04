
package com.b7anka.hollywoodtracker.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("season")
    @Expose
    private Integer season;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("episode")
    @Expose
    private Integer episode;
    @SerializedName("timewatched")
    @Expose
    private String timewatched;
    @SerializedName("completed")
    @Expose
    private Boolean completed;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("premium")
    @Expose
    private Integer premium;
    @SerializedName("recentlywatched")
    @Expose
    private Integer recentlywatched;
    @SerializedName("movies")
    @Expose
    private Integer movies;
    @SerializedName("tvshows")
    @Expose
    private Integer tvshows;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("code")
    @Expose
    private String code;
    private String password;

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getEpisode() {
        return episode;
    }

    public void setEpisode(Integer episode) {
        this.episode = episode;
    }

    public String getTimewatched() {
        return timewatched;
    }

    public void setTimewatched(String timewatched) {
        this.timewatched = timewatched;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPremium() {
        return premium;
    }

    public void setPremium(Integer premium) {
        this.premium = premium;
    }

    public Integer getRecentlywatched() {
        return recentlywatched;
    }

    public void setRecentlywatched(Integer recentlywatched) {
        this.recentlywatched = recentlywatched;
    }

    public Integer getMovies() {
        return movies;
    }

    public void setMovies(Integer movies) {
        this.movies = movies;
    }

    public Integer getTvshows() {
        return tvshows;
    }

    public void setTvshows(Integer tvshows) {
        this.tvshows = tvshows;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
