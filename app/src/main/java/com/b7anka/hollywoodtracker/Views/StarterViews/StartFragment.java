package com.b7anka.hollywoodtracker.Views.StarterViews;


import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.b7anka.hollywoodtracker.Helpers.SharedPreferencesManager;
import com.b7anka.hollywoodtracker.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {

    private View view;
    private Activity activity;
    private SharedPreferencesManager preferencesManager;
    private NavController navController;

    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_start, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        insertItems();
        if(preferencesManager.getAutoLoginState())
        {
            if(preferencesManager.getUseFingerPrintState())
            {
                navController.navigate(R.id.action_startFragment_to_fingerPrintFragment);
            }
            else
            {
                navController.navigate(R.id.action_startFragment_to_homeActivity);
                activity.finish();
            }
        }
        else
        {
            navController.navigate(R.id.action_startFragment_to_loginFragment);
        }

    }

    private void insertItems()
    {
        activity = getActivity();
        preferencesManager = new SharedPreferencesManager(activity);
        navController = Navigation.findNavController(this.activity, R.id.nav_host_fragment);
    }
}
