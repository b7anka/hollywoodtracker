package com.b7anka.hollywoodtracker.ViewModels;

import android.app.Application;

import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.RealmManager;
import com.b7anka.hollywoodtracker.Helpers.SharedPreferencesManager;
import com.b7anka.hollywoodtracker.Model.User;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class UserViewModel extends AndroidViewModel
{
    private RealmManager realmManager;
    private SharedPreferencesManager preferencesManager;
    private User user;

    public UserViewModel(@NonNull Application application)
    {
        super(application);
        this.realmManager = new RealmManager(application.getApplicationContext());
        this.preferencesManager = new SharedPreferencesManager(application.getApplicationContext());
    }

    public void insert(User user)
    {
        realmManager.createUser(user);
    }

    public void update(User user)
    {
        realmManager.updateUser(user);
    }

    public void delete(User user)
    {
        realmManager.deleteUser(user);
    }

    public User getUser(int userId)
    {
        if(user == null)
        {
            user = realmManager.getUser(userId);
        }
        return user;
    }

    public void subtractOneToUserShows(String type)
    {
        if(user == null)
        {
            user = getUser(preferencesManager.getUserId());
        }

        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setUsername(user.getUsername());
        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setPremium(user.isPremium());
        newUser.setMovies(user.getMovies());
        newUser.setTvShows(user.getTvShows());
        newUser.setRecent(user.getRecent());
        newUser.setTotal(user.getTotal());
        newUser.setThumbnail(user.getThumbnail());

        int movies = newUser.getMovies();
        int tvshows = newUser.getTvShows();
        int recents = newUser.getRecent();

        if(type.equals(Constants.MOVIES))
        {
            movies -= 1;
        }
        else if(type.equals(Constants.TV_SHOWS))
        {
            tvshows -=1;
        }
        else
        {
            recents -= 1;
        }

        newUser.setMovies(movies);
        newUser.setTvShows(tvshows);
        newUser.setRecent(recents);
        newUser.setTotal(movies+tvshows+recents);
        update(newUser);
    }

    public void addOneToUserShows(String type)
    {
        if(user == null)
        {
            user = getUser(preferencesManager.getUserId());
        }

        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setUsername(user.getUsername());
        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setPremium(user.isPremium());
        newUser.setMovies(user.getMovies());
        newUser.setTvShows(user.getTvShows());
        newUser.setRecent(user.getRecent());
        newUser.setTotal(user.getTotal());
        newUser.setThumbnail(user.getThumbnail());

        int movies = newUser.getMovies();
        int tvshows = newUser.getTvShows();
        int recents = newUser.getRecent();

        if(type.equals(Constants.MOVIES))
        {
            movies += 1;
        }
        else if(type.equals(Constants.TV_SHOWS))
        {
            tvshows +=1;
        }
        else
        {
            recents += 1;
        }

        newUser.setMovies(movies);
        newUser.setTvShows(tvshows);
        newUser.setRecent(recents);
        newUser.setTotal(movies+tvshows+recents);
        update(newUser);
    }

}
