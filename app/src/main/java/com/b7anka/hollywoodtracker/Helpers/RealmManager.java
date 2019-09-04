package com.b7anka.hollywoodtracker.Helpers;

import android.content.Context;
import com.b7anka.hollywoodtracker.Model.OfflineChanges;
import com.b7anka.hollywoodtracker.Model.Show;
import com.b7anka.hollywoodtracker.Model.User;
import java.util.ArrayList;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmMigrationNeededException;

public class RealmManager
{
    private Realm realm;
    private SharedPreferencesManager preferencesManager;

    public RealmManager(Context context)
    {
        Realm.init(context);
        try {
            this.realm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException r) {
            Realm.deleteRealm(Realm.getDefaultConfiguration());
            this.realm = Realm.getDefaultInstance();
        }
        preferencesManager = new SharedPreferencesManager(context);
    }

    public boolean checkIfShowExists(Show show)
    {
        final RealmResults<Show> dataRealm = realm.where(Show.class).equalTo("id",show.getId()).findAll();
        if(dataRealm.size() > 0)
        {
            for(int i = 0; i < dataRealm.size(); i++)
            {
                if(dataRealm.get(i).getType().equals(show.getType()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkIfUserExists(User user)
    {
        final RealmResults<User> dataRealm = realm.where(User.class).equalTo("id",user.getId()).findAll();
        return dataRealm.size() > 0;
    }

    public void createShow(Show show)
    {
        if(checkIfShowExists(show))
        {
            String previousThumbnailFile = Constants.EMPTY;
            RealmResults<Show> oldShows = getShow(show.getId());
            for(int i = oldShows.size()-1; i >= 0; i--)
            {
                if(oldShows.get(i).getType().equals(show.getType()))
                {
                    previousThumbnailFile = oldShows.get(i).getThumbnail();
                }
            }
            if(!previousThumbnailFile.isEmpty())ImageManager.deleteImageFromStorage(previousThumbnailFile);
            updateShow(show);
        }
        else
        {
            realm.beginTransaction();
            Show model = realm.createObject(Show.class);
            model.setId(show.getId());
            model.setIdUser(show.getIdUser());
            model.setTitle(show.getTitle());
            model.setType(show.getType());
            model.setSeason(show.getSeason());
            model.setEpisode(show.getEpisode());
            model.setWatchedTime(show.getWatchedTime());
            model.setThumbnail(show.getThumbnail());
            model.setCompleted(show.isCompleted());
            realm.commitTransaction();
        }
    }

    public void createUser(User user)
    {
        if(checkIfUserExists(user))
        {
            String previousThumbnailFile = getUser(user.getId()).getThumbnail();
            ImageManager.deleteImageFromStorage(previousThumbnailFile);
            updateUser(user);
        }
        else
        {
            realm.beginTransaction();
            User model = realm.createObject(User.class);
            model.setId(user.getId());
            model.setUsername(user.getUsername());
            model.setFullName(user.getFullName());
            model.setEmail(user.getEmail());
            model.setPassword(user.getPassword());
            model.setThumbnail(user.getThumbnail());
            model.setPremium(user.isPremium());
            model.setMovies(user.getMovies());
            model.setTvShows(user.getTvShows());
            model.setRecent(user.getRecent());
            model.setTotal(user.getTotal());
            realm.commitTransaction();
        }
    }

    public void createOfflineChange(int id, String type, String content)
    {
        realm.beginTransaction();
        OfflineChanges data = realm.createObject(OfflineChanges.class);
        data.setIdChange(OtherSharedMethods.getRandomNumber());
        data.setIdUser(preferencesManager.getUserId());
        data.setId(id);
        data.setType(type);
        data.setContent(content);
        realm.commitTransaction();
    }

    public void updateShow(final Show show)
    {
        final RealmResults<Show> dataRealm = realm.where(Show.class).equalTo("id",show.getId()).findAll();
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                for(int i = dataRealm.size()-1; i >=0; i--)
                {
                    if(dataRealm.get(i).getType().equals(show.getType()))
                    {
                        dataRealm.get(i).setTitle(show.getTitle());
                        dataRealm.get(i).setWatchedTime(show.getWatchedTime());
                        dataRealm.get(i).setCompleted(show.isCompleted());
                        dataRealm.get(i).setType(show.getType());
                        dataRealm.get(i).setIdUser(show.getIdUser());
                        dataRealm.get(i).setSeason(show.getSeason());
                        dataRealm.get(i).setEpisode(show.getEpisode());
                        dataRealm.get(i).setThumbnail(show.getThumbnail());
                    }
                }
            }
        });
    }

    public void updateUser(final User user)
    {
        final RealmResults<User> dataRealm = realm.where(User.class).equalTo("id",user.getId()).findAll();
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                dataRealm.first().setUsername(user.getUsername());
                dataRealm.first().setFullName(user.getFullName());
                dataRealm.first().setEmail(user.getEmail());
                if(user.getPassword() != null && !user.getPassword().isEmpty())
                {
                    dataRealm.first().setPassword(user.getPassword());
                }
                dataRealm.first().setThumbnail(user.getThumbnail());
                dataRealm.first().setPremium(user.isPremium());
                dataRealm.first().setMovies(user.getMovies());
                dataRealm.first().setTvShows(user.getTvShows());
                dataRealm.first().setRecent(user.getRecent());
                dataRealm.first().setTotal(user.getTotal());
            }
        });
    }

    public void deleteShow(Show show)
    {
        final RealmResults<Show> dataRealm = realm.where(Show.class).equalTo("id",show.getId()).findAll();
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                dataRealm.deleteAllFromRealm();
            }
        });
    }

    public void deleteUser(User user)
    {
        deleteAllShows(user.getId());
        final RealmResults<User> dataRealm = realm.where(User.class).equalTo("id",user.getId()).findAll();
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                dataRealm.deleteAllFromRealm();
            }
        });
    }

    public void deleteOfflineChange(OfflineChanges offlineChange)
    {
        final RealmResults<OfflineChanges> dataRealm = realm.where(OfflineChanges.class).equalTo("id",offlineChange.getId()).findAll();
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                dataRealm.deleteAllFromRealm();
            }
        });
    }

    public void deleteAllOfflineChanges(int userId)
    {

        final RealmResults<OfflineChanges> changes = realm.where(OfflineChanges.class).equalTo("idUser",userId).findAll();
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                changes.deleteAllFromRealm();
            }
        });

    }

    public List<Show> getAllShows(int userId)
    {
        RealmQuery<Show> showRealmQuery = realm.where(Show.class).equalTo("idUser",userId);
        RealmResults<Show> dataRealm =  showRealmQuery.findAll().sort("id", Sort.DESCENDING);
        List<Show> showList = new ArrayList<>();
        for(Show s: dataRealm)
        {
            showList.add(s);
        }

        return showList;
    }

    public void deleteAllShows(int userId)
    {

        final RealmResults<Show> shows = realm.where(Show.class).equalTo("idUser",userId).findAll();
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                shows.deleteAllFromRealm();
            }
        });

    }

    public RealmResults<Show> getShow(int id)
    {
        RealmQuery<Show> showRealmQuery = realm.where(Show.class).equalTo("id",id);
        RealmResults<Show> dataRealm = showRealmQuery.findAll();
        return dataRealm;
    }

    public User getUser(int userId)
    {
        RealmQuery<User> userRealmQuery = realm.where(User.class).equalTo("id",userId);
        RealmResults<User> dataRealm =  userRealmQuery.findAll();
        return dataRealm.first();
    }

    public List<OfflineChanges> getAllOfflineChanges(int userId)
    {
        RealmQuery<OfflineChanges> dataRealm = realm.where(OfflineChanges.class).equalTo("idUser",userId);
        RealmResults<OfflineChanges> offlineChanges =  dataRealm.findAll();
        List<OfflineChanges> offline = new ArrayList<>();
        for(OfflineChanges s: offlineChanges)
        {
            offline.add(s);
        }

        return offline;
    }
}
