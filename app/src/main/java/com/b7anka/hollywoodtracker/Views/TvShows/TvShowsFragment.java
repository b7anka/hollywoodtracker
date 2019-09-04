package com.b7anka.hollywoodtracker.Views.TvShows;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.View;
import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Model.Show;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.Views.Movies.ShowsFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TvShowsFragment extends ShowsFragment
{
    private List<Show> tvShows;

    public TvShowsFragment() {
        // Required empty public constructor
    }

    @Override
    protected void insertItems()
    {
        super.insertItems();
        fragmentNameTextView.setText(getString(R.string.tvshows));
        tvShows = new ArrayList<>();
    }

    @Override
    protected void setButtonsClickListeners()
    {
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_tvShowsFragment_to_nav_settings);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.USER_ID,preferencesManager.getUserId());
                navController.navigate(R.id.action_tvShowsFragment_to_nav_about, bundle);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(networkManager.isInternetAvailable())
                {
                    goToAddFragment(getString(R.string.add_tvshow),false, null);
                }
                else
                {
                    alertManager.showNoInternetConnectionAlert();
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreferencesToFalse();
                navController.navigate(R.id.action_nav_tvshows_to_mainActivity);
                activity.finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshing();
            }
        });
    }

    @Override
    protected void defineShows()
    {
        for(Show s : allShows)
        {
            if(s.getType().equals(Constants.TV_SHOW) && !s.isCompleted())
            {
                if(tvShows.contains(s))
                {
                    int index = tvShows.indexOf(s);
                    tvShows.set(index, s);
                }
                else
                {
                    tvShows.add(s);
                }
            }
        }
        populateDataView(tvShows, getString(R.string.no_tvshows_to_show));
    }

    @Override
    protected void navigateToAddFragment(Bundle bundle)
    {
        navController.navigate(R.id.action_tvShowsFragment_to_addShowFragment, bundle);
    }

    @Override
    protected void checkForShowOnShowsArray(Show show)
    {
        for(int i = 0; i < tvShows.size(); i++)
        {
            if(tvShows.get(i).getId() == show.getId())
            {
                tvShows.remove(i);
            }
        }
        defineShows();
    }

    @Override
    public void setOnShowClick(int position)
    {
        if(!isClicked)askUserIfHeHasFinishedWatchingTheShow(tvShows.get(position));
    }

    @Override
    protected void onRefreshing() {
        if (networkManager.isInternetAvailable()) {
            tvShows.clear();
            getTvShowsFromServer();
        } else {
            showNoInternetToRefresh();
        }
    }

            @Override
    protected void removeShowFromList(Show show)
    {
        int index = -1;
        for(Show s : tvShows)
        {
            if(s.getId() == show.getId())
            {
                index = tvShows.indexOf(s);
            }
        }
        if(index != -1)tvShows.remove(index);
        userViewModel.subtractOneToUserShows(Constants.TV_SHOWS);
        userViewModel.addOneToUserShows(Constants.RECENTLY_WATCHED);
    }

    @Override
    protected void showUserInvalidDialog()
    {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setPreferencesToFalse();
                userViewModel.delete(userViewModel.getUser(preferencesManager.getUserId()));
                navController.navigate(R.id.action_nav_tvshows_to_mainActivity);
                activity.finish();
            }
        };
        alertManager.displayErrorAlertWithCustomTitleAndMsgIconAndOneListener(getString(R.string.error),
                getString(R.string.user_invalid),android.R.drawable.ic_dialog_alert, listener);
    }

    @Override
    protected void getDataToRecyclerView()
    {
        if(!networkManager.isInternetAvailable())
        {
            populateDataView(tvShows, getString(R.string.no_tvshows_to_show));
        }
        else
        {
            if(isVisible())populateDataView(tvShows, getString(R.string.no_tvshows_to_show));
        }
    }
}
