package com.b7anka.hollywoodtracker.ViewModels;

import android.app.Application;
import com.b7anka.hollywoodtracker.Helpers.RealmManager;
import com.b7anka.hollywoodtracker.Model.Show;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.realm.RealmResults;

public class ShowsViewModel extends AndroidViewModel
{
    private RealmManager realmManager;
    private List<Show> shows;
    private Show show;

    public ShowsViewModel(@NonNull Application application)
    {
        super(application);
        realmManager = new RealmManager(getApplication().getApplicationContext());
    }

    public void insertShow(Show show)
    {
        realmManager.createShow(show);
    }

    public void updateShow(Show show)
    {
        realmManager.updateShow(show);
    }

    public void deleteShow(Show show)
    {
        realmManager.deleteShow(show);
    }

    public void deleteAllShows(int userId)
    {
        realmManager.deleteAllShows(userId);
    }

    public List<Show> getAllShows(int userId)
    {
        shows = realmManager.getAllShows(userId);
        return shows;
    }

    public Show getShow(int showId, String type)
    {
        RealmResults<Show> shows = realmManager.getShow(showId);
        for(int i = shows.size()-1; i >=0; i--)
        {
            if(shows.get(i).getType().equals(type))
            {
                show = shows.get(i);
            }
        }
        return show;
    }
}
