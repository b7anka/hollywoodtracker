package com.b7anka.hollywoodtracker.Views.Settings;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.b7anka.hollywoodtracker.Helpers.FingerPrintManager;
import com.b7anka.hollywoodtracker.Helpers.SharedPreferencesManager;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.Views.Base.BaseNoBottomMenuFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends BaseNoBottomMenuFragment {

    private TextView applicationTextView;
    private TextView useFingerPrintTextView;
    private Switch fingerPrintSwitch;
    private Spinner homeScreensSpinner;
    private ArrayAdapter<CharSequence> adapterHomeScreensSpinner;
    private final int MOVIES = 0;
    private final int TV_SHOWS = 1;
    private final int RECENTLY_WATCHED = 2;
    private final int PROFILE = 3;
    private final String HOME_MOVIES = "movies";
    private final String HOME_TV_SHOWS = "tvshows";
    private final String HOME_RECENTLY_WATCHED = "recentlywatched";
    private final String HOME_PROFILE = "profile";
    private SharedPreferencesManager preferencesManager;
    private FingerPrintManager fingerPrintManager;
    private boolean homeScreenWasChanged = false;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        insertItems();
        checkForFingerPrintSensor();
        fillHomeScreensSpinnerWithAvailableScreens();
        selectHomeScreen();

        homeScreensSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                switch(i)
                {
                    case 0:
                        preferencesManager.saveHomeScreenToUse(HOME_MOVIES);
                        break;
                    case 1:
                        preferencesManager.saveHomeScreenToUse(HOME_TV_SHOWS);
                        break;
                    case 2:
                        preferencesManager.saveHomeScreenToUse(HOME_RECENTLY_WATCHED);
                        break;
                    case 3:
                        preferencesManager.saveHomeScreenToUse(HOME_PROFILE);
                        break;
                }
                if(!homeScreenWasChanged)SharedPreferencesManager.saveWasHomeScreenChanged(true);
                homeScreenWasChanged = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                preferencesManager.saveHomeScreenToUse(HOME_MOVIES);
            }
        });

        fingerPrintSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(!preferencesManager.getAutoLoginState())
                    {
                        fingerPrintSwitch.setChecked(false);
                        alertManager.displayErrorAlertWithCustomTitleAndMsgAndIcon(getString(R.string.error),getString(R.string.activate_auto_login_to_use_fingerprint),android.R.drawable.ic_dialog_alert);
                    }
                    else if(!fingerPrintManager.isFingerprintAvailable() || !fingerPrintManager.isKeyguardSecure())
                    {
                        fingerPrintSwitch.setChecked(false);
                       alertManager.displayToastWithCustomTextAndLenght(getString(R.string.enroll_fingerprints_and_secure_keyguard),Toast.LENGTH_LONG);
                    }
                    else
                    {
                        preferencesManager.saveUseFingerPrintState(true);
                    }
                }
                else
                {
                    preferencesManager.saveUseFingerPrintState(false);
                }
            }
        });
    }

    private void selectHomeScreen()
    {
        String homeScreenToUse = preferencesManager.getHomeScreenToUse();
        if(!homeScreenToUse.isEmpty())
        {
            switch(homeScreenToUse)
            {
                case HOME_MOVIES:
                    homeScreensSpinner.setSelection(MOVIES);
                    break;
                case HOME_TV_SHOWS:
                    homeScreensSpinner.setSelection(TV_SHOWS);
                    break;
                case HOME_RECENTLY_WATCHED:
                    homeScreensSpinner.setSelection(RECENTLY_WATCHED);
                    break;
                case HOME_PROFILE:
                    homeScreensSpinner.setSelection(PROFILE);
                    break;
            }
        }
        homeScreenWasChanged = true;
    }

    private void fillHomeScreensSpinnerWithAvailableScreens()
    {
        adapterHomeScreensSpinner = ArrayAdapter.createFromResource(context,
                R.array.homescreen_available, android.R.layout.simple_spinner_item);
        adapterHomeScreensSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        homeScreensSpinner.setAdapter(adapterHomeScreensSpinner);
    }

    @Override
    protected void insertItems()
    {
        super.insertItems();
        applicationTextView = view.findViewById(R.id.applicationTextView);
        useFingerPrintTextView = view.findViewById(R.id.useFingerPrintTextView);
        fingerPrintSwitch = view.findViewById(R.id.fingerPrintSwitch);
        homeScreensSpinner = view.findViewById(R.id.homeScreensSpinner);
        preferencesManager = new SharedPreferencesManager(activity);
        fingerPrintManager = new FingerPrintManager(activity);
        fingerPrintSwitch.setChecked(preferencesManager.getUseFingerPrintState());
        //fingerPrintSwitch.setEnabled(preferencesManager.getAutoLoginState());
    }

    public void checkForFingerPrintSensor()
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            hideFingerPrintSensorRelatedInformation();
        }
        else
        {
            if(!fingerPrintManager.isHardwareSupported())
            {
                hideFingerPrintSensorRelatedInformation();
            }
        }
    }

    public void hideFingerPrintSensorRelatedInformation()
    {
        applicationTextView.setVisibility(View.GONE);
        useFingerPrintTextView.setVisibility(View.GONE);
        fingerPrintSwitch.setVisibility(View.GONE);
    }
}
