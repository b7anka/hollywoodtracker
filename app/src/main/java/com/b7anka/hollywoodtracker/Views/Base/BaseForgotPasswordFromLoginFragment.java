package com.b7anka.hollywoodtracker.Views.Base;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.b7anka.hollywoodtracker.Helpers.AlertManager;
import com.b7anka.hollywoodtracker.Helpers.NetworkManager;
import com.b7anka.hollywoodtracker.Helpers.OtherSharedMethods;
import com.b7anka.hollywoodtracker.Helpers.ValidateUserInputs;
import com.b7anka.hollywoodtracker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class BaseForgotPasswordFromLoginFragment extends Fragment
{
    protected View view;
    protected Context context;
    protected Activity activity;
    protected NetworkManager networkManager;
    protected AlertManager alertManager;
    protected ProgressBar progressBar;
    protected ValidateUserInputs validator;
    protected OtherSharedMethods otherSharedMethods;
    protected NavController navController;
    protected BottomNavigationView bottomNavigationView;

    protected void insertItems()
    {
        this.context = getActivity();
        this.activity = getActivity();
        this.networkManager = new NetworkManager(this.context);
        this.alertManager = new AlertManager(this.context);
        this.progressBar = view.findViewById(R.id.progressBar);
        this.validator = new ValidateUserInputs();
        this.otherSharedMethods = new OtherSharedMethods(this.context);
        this.navController = Navigation.findNavController(this.activity,R.id.nav_host_fragment);
        this.bottomNavigationView = this.activity.findViewById(R.id.bottom_nav);
        this.bottomNavigationView.setVisibility(View.GONE);
    }

}
