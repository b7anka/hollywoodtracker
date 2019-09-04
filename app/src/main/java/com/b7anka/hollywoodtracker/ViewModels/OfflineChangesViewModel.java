package com.b7anka.hollywoodtracker.ViewModels;

import android.app.Application;
import com.b7anka.hollywoodtracker.Helpers.RealmManager;
import com.b7anka.hollywoodtracker.Model.OfflineChanges;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class OfflineChangesViewModel extends AndroidViewModel
{
    private RealmManager realmManager;
    private List<OfflineChanges> allOfflineChanges;

    public OfflineChangesViewModel(@NonNull Application application)
    {
        super(application);
        realmManager = new RealmManager(application.getApplicationContext());
    }

    public void insert(int id, String type, String content)
    {
        realmManager.createOfflineChange(id, type, content);
    }

    public void delete(OfflineChanges change)
    {
        realmManager.deleteOfflineChange(change);
    }

    public void deleteAllOfflineChanges(int userId)
    {
        realmManager.deleteAllOfflineChanges(userId);
    }

    public List<OfflineChanges> getAllOfflineChanges(int userId)
    {
        if(allOfflineChanges == null)
        {
            allOfflineChanges = realmManager.getAllOfflineChanges(userId);
        }
        return allOfflineChanges;
    }
}
