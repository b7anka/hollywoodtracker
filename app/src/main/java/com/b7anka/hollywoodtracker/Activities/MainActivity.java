package com.b7anka.hollywoodtracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.b7anka.hollywoodtracker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import androidx.appcompat.widget.Toolbar;
import java.util.HashSet;
import java.util.Set;
import static androidx.navigation.Navigation.findNavController;
import static androidx.navigation.ui.NavigationUI.*;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private Set<Integer> topLevelDestinations;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            item.setCheckable(true);
            item.setChecked(true);
            switch (item.getItemId()) {
                case R.id.nav_register:
                    navController.navigate(R.id.action_loginFragment_to_registerFragment);
                    return true;
                case R.id.nav_about:
                    navController.navigate(R.id.action_loginFragment_to_aboutFragment);
                    return true;
                case R.id.nav_settings:
                    navController.navigate(R.id.action_loginFragment_to_nav_settings);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setSupportActionBar(toolbar);

        navController = findNavController(this, R.id.nav_host_fragment);
        topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.loginFragment);
        topLevelDestinations.add(R.id.fingerPrintFragment);
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
                           if(navController.getCurrentDestination().getId() == R.id.loginFragment) {
                               bottomNavigationView.setVisibility(View.GONE);
                           }
                       }
                       else
                       {
                           if(navController.getCurrentDestination().getId() == R.id.loginFragment) {
                               bottomNavigationView.setVisibility(View.VISIBLE);
                           }
                       }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {

            if (navController.getCurrentDestination().getId() == R.id.loginFragment ||
                    navController.getCurrentDestination().getId() == R.id.fingerPrintFragment)
            {
                finish();
            }
            else
            {
                navController.navigateUp();
            }

    }
}
