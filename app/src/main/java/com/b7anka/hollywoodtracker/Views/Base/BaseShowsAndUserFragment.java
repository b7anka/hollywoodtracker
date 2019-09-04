package com.b7anka.hollywoodtracker.Views.Base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.b7anka.hollywoodtracker.Activities.HomeActivity;
import com.b7anka.hollywoodtracker.Helpers.AdsManager;
import com.b7anka.hollywoodtracker.Helpers.AlertManager;
import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.NetworkManager;
import com.b7anka.hollywoodtracker.Helpers.OtherSharedMethods;
import com.b7anka.hollywoodtracker.Helpers.SharedPreferencesManager;
import com.b7anka.hollywoodtracker.Helpers.ValidateUserInputs;
import com.b7anka.hollywoodtracker.Interfaces.AfterPremiumBoughtListener;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import com.b7anka.hollywoodtracker.Model.User;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.ViewModels.OfflineChangesViewModel;
import com.b7anka.hollywoodtracker.ViewModels.UserViewModel;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Response;

public class BaseShowsAndUserFragment extends BaseCameraFragment implements AfterPremiumBoughtListener
{
    protected View view;
    protected Activity activity;
    protected AlertManager alertManager;
    protected BottomNavigationView bottomNavigationView;
    protected TextView showsInformationTextView;
    protected LinearLayout linearLayout;
    protected NetworkManager networkManager;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected OfflineChangesViewModel offlineChangesViewModel;
    protected Toolbar toolbar;
    protected Button logoutButton;
    protected Button addButton;
    protected Button aboutButton;
    protected Button settingsButton;
    protected Button deleteAllButton;
    protected Button refreshButton;
    protected Button buyPremiumButton;
    protected FloatingActionButton getPremiumForFreeFloatingActionButton;
    protected TextView fragmentNameTextView;
    protected SharedPreferencesManager preferencesManager;
    protected NavController navController;
    protected ValidateUserInputs validator;
    protected ScrollView scrollView;
    protected AdView adView;
    protected AdsManager adsManager;
    protected OtherSharedMethods otherSharedMethods;
    private UserViewModel viewModel;

    protected void insertItems()
    {
        super.insertItems();
        this.activity = getActivity();
        this.networkManager = new NetworkManager(this.activity);
        this.adsManager = new AdsManager(activity);
        this.preferencesManager = new SharedPreferencesManager(this.activity);
        this.offlineChangesViewModel = ViewModelProviders.of(this).get(OfflineChangesViewModel.class);
        this.alertManager = new AlertManager(this.activity);
        this.bottomNavigationView = this.activity.findViewById(R.id.bottom_nav);
        this.toolbar = this.activity.findViewById(R.id.toolbar);
        this.logoutButton = this.toolbar.findViewById(R.id.logoutButton);
        this.addButton = this.toolbar.findViewById(R.id.addShowButton);
        this.aboutButton = this.toolbar.findViewById(R.id.aboutButton);
        this.settingsButton = this.toolbar.findViewById(R.id.settingsButton);
        this.deleteAllButton = this.toolbar.findViewById(R.id.deleteAllButton);
        this.refreshButton = this.toolbar.findViewById(R.id.refreshButton);
        this.buyPremiumButton = this.toolbar.findViewById(R.id.buyPremiumButton);
        this.fragmentNameTextView = this.toolbar.findViewById(R.id.fragmentNameTextView);
        this.linearLayout = this.toolbar.findViewById(R.id.toolbarLinearLayout);
        this.navController = Navigation.findNavController(this.activity, R.id.nav_host_fragment);
        this.swipeRefreshLayout = this.activity.findViewById(R.id.showsSwipeToRefreshLayout);
        this.getPremiumForFreeFloatingActionButton = this.activity.findViewById(R.id.getPremiumFeaturesFreeFloatingActionButton);
        this.validator = new ValidateUserInputs();
        this.scrollView = this.activity.findViewById(R.id.mainScrollView);
        this.adView = view.findViewById(R.id.adView);
        this.otherSharedMethods = new OtherSharedMethods(context);
        MobileAds.initialize(context, Constants.APP_ADMOB_ID);
        this.viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        ((HomeActivity)getActivity()).setAfterPremiumBoughtListener(this);
        setBuyPremiumButtonListener();
    }

    private void setBuyPremiumButtonListener()
    {
        this.buyPremiumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((HomeActivity)getActivity()).getPremium();
            }
        });
    }

    protected void putBottomMenuVisible()
    {
        if(this.bottomNavigationView.getVisibility() == View.GONE)
        {
            this.bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    protected void putToolBarButtonsVisible()
    {
        if(this.linearLayout.getVisibility() == View.GONE)
        {
            this.linearLayout.setVisibility(View.VISIBLE);
        }

        if(addButton.getVisibility() == View.GONE)
        {
            addButton.setVisibility(View.VISIBLE);
        }

        if(deleteAllButton.getVisibility() == View.VISIBLE)
        {
            deleteAllButton.setVisibility(View.GONE);
        }
    }

    protected void setPreferencesToFalse()
    {
        if(!preferencesManager.getUseFingerPrintState())
        {
            preferencesManager.saveAutoLoginState(false);
        }
    }

    @Override
    public void afterPremiumBoughtSuccessfully(Response<APIResponse> response, String details)
    {
        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_responding), Toast.LENGTH_SHORT);
            if(details != null)getPremiumToUser(details);
        }

        if(response.isSuccessful())
        {
            if(response.body().getSuccess())
            {
                getPremiumToUser(details);
                alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(), Toast.LENGTH_LONG);
            }
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void afterPremiumBoughtFailed(Throwable t, String details)
    {
        showGenericMessageOnRetrofitFailed(t);
        if(details != null)
        {
            getPremiumToUser(details);
        }
    }

    @SuppressLint("RestrictedApi")
    private void getPremiumToUser(String details)
    {
        preferencesManager.savePremiumPurchasedDetails(details);
        buyPremiumButton.setVisibility(View.GONE);
        preferencesManager.saveUserPremium(Constants.USER_PREMIUM_TRUE);
        adView.setVisibility(View.GONE);
        getPremiumForFreeFloatingActionButton.setVisibility(View.GONE);
        User user = viewModel.getUser(preferencesManager.getUserId());
        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setUsername(user.getUsername());
        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setPremium(true);
        newUser.setMovies(user.getMovies());
        newUser.setTvShows(user.getTvShows());
        newUser.setRecent(user.getRecent());
        newUser.setTotal(user.getTotal());
        newUser.setThumbnail(user.getThumbnail());
        viewModel.update(newUser);
    }

    private void showGenericMessageOnRetrofitFailed(Throwable t)
    {
        if(t.toString().contains("SocketTimeoutException"))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_timeout),Toast.LENGTH_SHORT);
        }
    }
}
