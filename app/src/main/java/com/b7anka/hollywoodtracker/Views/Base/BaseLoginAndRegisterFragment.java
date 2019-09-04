package com.b7anka.hollywoodtracker.Views.Base;
import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import com.b7anka.hollywoodtracker.Helpers.AlertManager;
import com.b7anka.hollywoodtracker.Helpers.NetworkManager;
import com.b7anka.hollywoodtracker.Helpers.OtherSharedMethods;
import com.b7anka.hollywoodtracker.Helpers.SharedPreferencesManager;
import com.b7anka.hollywoodtracker.Helpers.ValidateUserInputs;
import com.b7anka.hollywoodtracker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


public class BaseLoginAndRegisterFragment extends BaseCameraFragment
{
    protected View view;
    protected ValidateUserInputs validator;
    protected AlertManager alertManager;
    protected NetworkManager networkManager;
    protected ProgressBar progressBar;
    protected Activity activity;
    protected SharedPreferencesManager preferencesManager;
    protected OtherSharedMethods otherSharedMethods;
    protected BottomNavigationView bottomNavigationView;
    protected NavController navController;

    @Override
    protected void insertItems()
    {
        super.insertItems();
        this.activity = getActivity();
        this.networkManager = new NetworkManager(this.context);
        this.alertManager = new AlertManager(this.context);
        this.progressBar = this.view.findViewById(R.id.progressBar);
        this.preferencesManager = new SharedPreferencesManager(this.activity);
        this.validator = new ValidateUserInputs();
        this.otherSharedMethods = new OtherSharedMethods(this.context);
        this.bottomNavigationView = this.activity.findViewById(R.id.bottom_nav);
        this.navController = Navigation.findNavController(activity,R.id.nav_host_fragment);
    }

    protected boolean validateUserInputs(String[] inputs)
    {
        return this.validator.checkForEmptyInputs(inputs);
    }

    protected void putBottomMenuVisible()
    {
        if(this.bottomNavigationView.getVisibility() == View.GONE)
        {
            this.bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    protected void putBottomMenuInVisible()
    {
        if(this.bottomNavigationView.getVisibility() == View.VISIBLE)
        {
            this.bottomNavigationView.setVisibility(View.GONE);
        }
    }
}
