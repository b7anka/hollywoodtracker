package com.b7anka.hollywoodtracker.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import retrofit2.Response;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.b7anka.hollywoodtracker.Helpers.AlertManager;
import com.b7anka.hollywoodtracker.Helpers.OfflineChangesManager;
import com.b7anka.hollywoodtracker.Helpers.RetrofitManager;
import com.b7anka.hollywoodtracker.Helpers.SharedPreferencesManager;
import com.b7anka.hollywoodtracker.Interfaces.AfterPremiumBoughtListener;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import com.b7anka.hollywoodtracker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import java.util.HashSet;
import java.util.Set;
import static androidx.navigation.Navigation.findNavController;
import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;
import static androidx.navigation.ui.NavigationUI.setupWithNavController;

public class HomeActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler, RetrofitManager.PremiumBoughtListener
{

    private Toolbar toolbar;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private Set<Integer> topLevelDestinations;
    private BillingProcessor bp;
    private boolean isOneTimePurchaseSupported;
    private AlertManager alertManager;
    private AfterPremiumBoughtListener afterPremiumBoughtListener;
    private RetrofitManager retrofitManager;
    private static SharedPreferencesManager preferencesManager;
    private static OfflineChangesManager offlineChangesManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int currentDestination = navController.getCurrentDestination().getId();
            switch (item.getItemId()) {
                case R.id.nav_movies:
                    if(currentDestination != R.id.nav_movies)navController.navigate(R.id.action_startFragment2_to_moviesFragment);
                    return true;
                case R.id.nav_tvshows:
                    if(currentDestination != R.id.nav_tvshows)navController.navigate(R.id.action_startFragment2_to_tvShowsFragment);
                    return true;
                case R.id.nav_recent:
                    if(currentDestination != R.id.nav_recent)navController.navigate(R.id.action_startFragment2_to_recentsFragment);
                    return true;
                case R.id.nav_profile:
                    if(currentDestination != R.id.nav_profile)navController.navigate(R.id.action_startFragment2_to_profileFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setSupportActionBar(toolbar);

        navController = findNavController(this, R.id.nav_host_fragment);
        topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.nav_movies);
        topLevelDestinations.add(R.id.nav_tvshows);
        topLevelDestinations.add(R.id.nav_recent);
        topLevelDestinations.add(R.id.nav_profile);
        appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).setDrawerLayout(null).build();
        setupActionBarWithNavController(this,navController,appBarConfiguration);
        setupWithNavController(bottomNavigationView,navController);

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if(isOpen)
                        {
                            if(navController.getCurrentDestination().getId() == R.id.nav_profile) {
                                bottomNavigationView.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            if(navController.getCurrentDestination().getId() == R.id.nav_profile) {
                                bottomNavigationView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        this.bp = new BillingProcessor(this, null, this);
        this.isOneTimePurchaseSupported = false;
        this.alertManager = new AlertManager(this);
        this.retrofitManager = new RetrofitManager(this);
        this.preferencesManager = new SharedPreferencesManager(this);
        offlineChangesManager = new OfflineChangesManager(HomeActivity.this);
        offlineChangesManager.sendChangesToServer();
    }

    public void getPremium()
    {
        if(isOneTimePurchaseSupported)
        {
            bp.consumePurchase("premium");
            bp.purchase(this, "premium");
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.purchase_not_supported), Toast.LENGTH_LONG);
        }
    }

    public void setAfterPremiumBoughtListener(AfterPremiumBoughtListener afterPremiumBoughtListener) {
        this.afterPremiumBoughtListener = afterPremiumBoughtListener;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {

        if (navController.getCurrentDestination().getId() == R.id.nav_movies ||
                navController.getCurrentDestination().getId() == R.id.nav_tvshows ||
                navController.getCurrentDestination().getId() == R.id.nav_recent ||
                navController.getCurrentDestination().getId() == R.id.nav_profile)
        {
            finish();
        }
        else if(navController.getCurrentDestination().getId() == R.id.nav_settings)
        {
            if(SharedPreferencesManager.wasHomeScreenChanged())
            {
                SharedPreferencesManager.saveWasHomeScreenChanged(false);
                navController.navigate(R.id.startFragment2);
            }
            else
            {
                navController.navigateUp();
            }
        }
        else
        {
            navController.navigateUp();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details)
    {
        retrofitManager.buyPremium(preferencesManager.getUserId(),details.purchaseInfo.responseData,details.purchaseInfo.signature);
    }

    @Override
    public void onPurchaseHistoryRestored()
    {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error)
    {
        if(error != null)
        {
            alertManager.displayToastWithCustomTextAndLenght(error.getLocalizedMessage(),Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onBillingInitialized()
    {
        isOneTimePurchaseSupported = bp.isOneTimePurchaseSupported();
    }

    @Override
    protected void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    public void afterPremiumBoughtSuccessfully(Response<APIResponse> response, String details)
    {
        if(afterPremiumBoughtListener != null)afterPremiumBoughtListener.afterPremiumBoughtSuccessfully(response, details);
    }

    @Override
    public void afterPremiumBoughtFailed(Throwable t, String details)
    {
        if(afterPremiumBoughtListener != null)afterPremiumBoughtListener.afterPremiumBoughtFailed(t, details);
    }
}
