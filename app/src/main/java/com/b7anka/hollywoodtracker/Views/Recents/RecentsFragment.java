package com.b7anka.hollywoodtracker.Views.Recents;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Response;
import android.view.View;
import android.widget.Toast;
import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.RetrofitManager;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import com.b7anka.hollywoodtracker.Model.Show;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.Views.Movies.ShowsFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentsFragment extends ShowsFragment implements RetrofitManager.DeleteDataListener
{
    private List<Show> recents;

    public RecentsFragment() {
        // Required empty public constructor
    }

    @Override
    protected void insertItems()
    {
        super.insertItems();
        fragmentNameTextView.setText(R.string.recent);
        addButton.setVisibility(View.GONE);
        deleteAllButton.setVisibility(View.VISIBLE);
        retrofitManager.setDeleteDataListener(this);
        recents = new ArrayList<>();
    }

    @Override
    protected void setButtonsClickListeners()
    {
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_recentsFragment_to_nav_settings);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.USER_ID,preferencesManager.getUserId());
                navController.navigate(R.id.action_recentsFragment_to_nav_about, bundle);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreferencesToFalse();
                navController.navigate(R.id.action_nav_recent_to_mainActivity);
                activity.finish();
            }
        });

        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                deleteEverything();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshing();
            }
        });
    }

    private void deleteEverything()
    {
        if(recents.size() > 0)
        {
            for(int i = recents.size()-1; i >= 0; i--)
            {
                String type = recents.get(i).getType().equals(Constants.MOVIE) ? Constants.MOVIES : Constants.TV_SHOWS;
                deleteUserData(recents.get(i),type);
                deleteLocalData(recents.get(i));
            }
            getDataToRecyclerView();
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.nothing_to_delete), Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void askUserIfHeHasFinishedWatchingTheShow(final Show show)
    {
        isClicked = true;

        final String movieOrEpisode = show.getType().equals(Constants.MOVIE) ? getString(R.string.movie) : getString(R.string.episode);
        final String type = show.getType().equals(Constants.MOVIE) ? Constants.MOVIES : Constants.TV_SHOWS;

        final AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(activity);

        builder.setTitle(getString(R.string.title_delete))
                .setMessage(String.format("%s %s?", getString(R.string.aks_user_if_he_wants_to_delete), movieOrEpisode))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        deleteUserData(show, type);
                        deleteLocalData(show);
                        getDataToRecyclerView();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                        isClicked = false;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteUserData(Show show, String type)
    {
        if(networkManager.isInternetAvailable())
        {
            retrofitManager.deleteUserData(show.getId(), preferencesManager.getUserId(), type);
        }
        else
        {
            createOfflineChange(show.getId(),type,Constants.DELETE_CONTENT);
        }
    }

    private void deleteLocalData(Show show)
    {
        for(int i = 0; i < allShows.size(); i++)
        {
            if(allShows.get(i).getId() == show.getId())
            {
                allShows.remove(i);
            }
        }

        for(int i = 0; i < recents.size(); i++)
        {
            if(recents.get(i).getId() == show.getId())
            {
                recents.remove(i);
            }
        }

        showsViewModel.deleteShow(show);
        userViewModel.subtractOneToUserShows(Constants.RECENTLY_WATCHED);
    }

    @Override
    protected void onRefreshing() {
        if (networkManager.isInternetAvailable()) {
            recents.clear();
            getRecentFromServer();
        } else {
            showNoInternetToRefresh();
        }
    }

            @Override
    public void afterDataDeletionSuccessfully(Response<APIResponse> response)
    {
        showOnRetrofitSuccessGenericMessage(response);
    }

    @Override
    public void afterDataDeletionFailed(Throwable t)
    {
        showMessageOnRetrofitFailed(t);
    }

    @Override
    protected void defineShows()
    {
        for(Show s : allShows)
        {
            if(s.isCompleted())
            {
                if(recents.contains(s))
                {
                    int index = recents.indexOf(s);
                    recents.set(index, s);
                }
                else
                {
                    recents.add(s);
                }
            }
        }
        getDataToRecyclerView();
    }

    @Override
    public void setOnShowClick(int position)
    {
        if(!isClicked)askUserIfHeHasFinishedWatchingTheShow(recents.get(position));
    }

    @Override
    protected void getDataToRecyclerView()
    {
        if(!networkManager.isInternetAvailable())
        {
            populateDataView(recents, getString(R.string.no_recents_to_show));
        }
        else
        {
            if(isVisible())populateDataView(recents, getString(R.string.no_recents_to_show));
        }
    }

    @Override
    protected void checkForShowOnShowsArray(Show show)
    {
        for(int i = 0; i < recents.size(); i++)
        {
            if(recents.get(i).getId() == show.getId())
            {
                recents.remove(i);
            }
        }
        defineShows();
    }

    @Override
    protected void showUserInvalidDialog()
    {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setPreferencesToFalse();
                userViewModel.delete(userViewModel.getUser(preferencesManager.getUserId()));
                navController.navigate(R.id.action_nav_recent_to_mainActivity);
                activity.finish();
            }
        };
        alertManager.displayErrorAlertWithCustomTitleAndMsgIconAndOneListener(getString(R.string.error),
                getString(R.string.user_invalid),android.R.drawable.ic_dialog_alert, listener);
    }
}
