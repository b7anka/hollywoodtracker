package com.b7anka.hollywoodtracker.Views.Base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import com.b7anka.hollywoodtracker.Helpers.AlertManager;
import com.b7anka.hollywoodtracker.Helpers.NetworkManager;
import com.b7anka.hollywoodtracker.Helpers.OtherSharedMethods;
import com.b7anka.hollywoodtracker.Helpers.SharedPreferencesManager;
import com.b7anka.hollywoodtracker.Helpers.ValidateUserInputs;
import com.b7anka.hollywoodtracker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class BaseNoBottomMenuFragment extends Fragment
{
    protected View view;
    protected Context context;
    protected Activity activity;
    protected NavController navController;
    protected BottomNavigationView navigationView;
    protected AlertManager alertManager;
    protected NetworkManager networkManager;
    protected ValidateUserInputs validator;
    protected OtherSharedMethods otherSharedMethods;
    protected SharedPreferencesManager preferencesManager;
    protected FloatingActionButton getPremiumForFreeFloatingActionButton;
    protected Toolbar toolbar;
    protected SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout linearLayout;

    protected void insertItems()
    {
        this.context = getActivity();
        this.activity = getActivity();
        this.navController = Navigation.findNavController(this.activity, R.id.nav_host_fragment);
        this.navigationView = this.activity.findViewById(R.id.bottom_nav);
        this.alertManager = new AlertManager(this.context);
        this.networkManager = new NetworkManager(this.context);
        this.validator = new ValidateUserInputs();
        this.otherSharedMethods = new OtherSharedMethods(this.context);
        this.preferencesManager = new SharedPreferencesManager(this.activity);
        this.toolbar = this.activity.findViewById(R.id.toolbar);
        this.getPremiumForFreeFloatingActionButton = this.activity.findViewById(R.id.getPremiumFeaturesFreeFloatingActionButton);
        this.linearLayout = this.toolbar.findViewById(R.id.toolbarLinearLayout);
        this.swipeRefreshLayout = this.activity.findViewById(R.id.showsSwipeToRefreshLayout);
        putBottomMenuInVisible();
        putToolBarButtonsNotVisible();
    }

    private void putBottomMenuInVisible()
    {
        if(this.navigationView.getVisibility() == View.VISIBLE)
        {
            this.navigationView.setVisibility(View.GONE);
        }
        if(this.swipeRefreshLayout != null)
        {
            this.swipeRefreshLayout.setEnabled(false);
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @SuppressLint("RestrictedApi")
    private void putToolBarButtonsNotVisible()
    {
        if(this.linearLayout != null)this.linearLayout.setVisibility(View.GONE);
        if(this.getPremiumForFreeFloatingActionButton != null)this.getPremiumForFreeFloatingActionButton.setVisibility(View.GONE);
    }
}
