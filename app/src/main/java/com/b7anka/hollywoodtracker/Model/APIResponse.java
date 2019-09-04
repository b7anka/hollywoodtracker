
package com.b7anka.hollywoodtracker.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class APIResponse {

    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("last_activity")
    @Expose
    private long lastActivity;
    @SerializedName("videos_watched")
    @Expose
    private int videosWatched;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    public int getVideosWatched() {
        return videosWatched;
    }

    public void setVideosWatched(int videosWatched) {
        this.videosWatched = videosWatched;
    }
}
