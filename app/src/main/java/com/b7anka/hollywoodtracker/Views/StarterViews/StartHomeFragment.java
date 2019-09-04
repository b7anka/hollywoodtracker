package com.b7anka.hollywoodtracker.Views.StarterViews;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.SharedPreferencesManager;
import com.b7anka.hollywoodtracker.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class StartHomeFragment extends Fragment {

    private View view;
    private Activity activity;
    private SharedPreferencesManager preferencesManager;
    private NavController navController;

    public StartHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_start_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        insertItems();

        switch(preferencesManager.getHomeScreenToUse())
        {
            case Constants.MOVIES:
                navController.navigate(R.id.action_startFragment2_to_moviesFragment);
                break;
            case Constants.TV_SHOWS:
                navController.navigate(R.id.action_startFragment2_to_tvShowsFragment);
                break;
            case Constants.RECENTLY_WATCHED:
                navController.navigate(R.id.action_startFragment2_to_recentsFragment);
                break;
                default:
                    navController.navigate(R.id.action_startFragment2_to_profileFragment);
        }

    }

    private void insertItems()
    {
        activity = getActivity();
        preferencesManager = new SharedPreferencesManager(activity);
        navController = Navigation.findNavController(this.activity, R.id.nav_host_fragment);
    }
}
