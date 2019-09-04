package com.b7anka.hollywoodtracker.Views.About;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import retrofit2.Response;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.b7anka.hollywoodtracker.Helpers.AdsManager;
import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.RetrofitManager;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import com.b7anka.hollywoodtracker.Model.User;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.ViewModels.UserViewModel;
import com.b7anka.hollywoodtracker.Views.Base.BaseNoBottomMenuFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class GetPremiumFeaturesForFreeFragment extends BaseNoBottomMenuFragment implements AdsManager.AfterRewardedVideoListener, RetrofitManager.GetRewardedVideos
{
    private Button watchVideoButton;
    private AdsManager adsManager;
    private TextView totalVidsTextView;
    private TextView requiredVidsTextView;
    private RetrofitManager retrofitManager;
    private int totalWatchedVideos;
    private UserViewModel viewModel;
    private long now;
    private long lastWatchedVideoTime;

    public GetPremiumFeaturesForFreeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_get_premium_features_for_free, container, false);
        insertItems();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getRewardedVideosFromServer();
        watchVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(networkManager.isInternetAvailable())
                {
                    now = System.currentTimeMillis() - Constants.FIVE_MINUTES_IN_MILLIS;
                    if(lastWatchedVideoTime >= now)
                    {
                        alertManager.displayErrorAlertWithCustomTitleAndMsgAndIcon(getString(R.string.error), getString(R.string.wait_to_watch_video_again), android.R.drawable.ic_dialog_alert);
                    }
                    else
                    {
                        adsManager.prepareRewardedVideoAd();
                        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {watchVideoButton});
                    }
                }
                else
                {
                    alertManager.showNoInternetConnectionAlert();
                }
            }
        });
    }

    private void getRewardedVideosFromServer()
    {
        if(networkManager.isInternetAvailable())
        {
            retrofitManager.getRewardedVideos(preferencesManager.getUserId(),false, 0);
        }
        else
        {
            populateViewWithInfo();
        }
    }

    private void populateViewWithInfo()
    {
        totalVidsTextView.setText(String.format("%s: %d", getString(R.string.total_watched_vids),totalWatchedVideos));
    }

    @Override
    protected void insertItems()
    {
        super.insertItems();
        this.adsManager = new AdsManager(context, this);
        this.watchVideoButton = view.findViewById(R.id.watchVideoButton);
        this.totalVidsTextView = view.findViewById(R.id.totalVidsWatchedTextView);
        this.requiredVidsTextView = view.findViewById(R.id.requiredVideosTextView);
        this.retrofitManager = new RetrofitManager(this);
        this.totalWatchedVideos = preferencesManager.getTotalWatchedVids();
        this.viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        this.lastWatchedVideoTime = preferencesManager.getLastVideoWatchedTimeStamp();
        this.requiredVidsTextView.setText(String.format("%s: %d", getString(R.string.required_videos), Constants.REQUIRED_VIDS));
    }

    @Override
    public void onRewardedVideoCompleted()
    {
        retrofitManager.getRewardedVideos(preferencesManager.getUserId(), true, ++totalWatchedVideos);
        lastWatchedVideoTime = System.currentTimeMillis();
        preferencesManager.saveLastVideoWatchedTimeStamp(lastWatchedVideoTime);
    }

    @Override
    public void onRewardedVideoAdClosed()
    {
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {watchVideoButton});
    }

    @Override
    public void onRewardedVideoFailedToLoad()
    {
        alertManager.displayErrorAlertWithCustomTitleAndMsgAndIcon(getString(R.string.error),getString(R.string.rewarded_video_failed_to_load), android.R.drawable.ic_dialog_alert);
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {watchVideoButton});
    }

    @Override
    public void afterRewardedVideosLoadedSuccessfully(Response<APIResponse> response, boolean isSaving)
    {
        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_responding), Toast.LENGTH_SHORT);
        }

        if(response.isSuccessful())
        {
            if(response.body().getSuccess())
            {
                updateUserToPremium();
                alertManager.displayErrorAlertWithCustomTitleAndMsgAndIcon(getString(R.string.congratulations_title),getString(R.string.congratulations),android.R.drawable.ic_dialog_info);
            }
            else
            {
                switch(response.body().getError())
                {
                    case Constants.SUCCESSFULLY_SAVED:
                        alertManager.displayToastWithCustomTextAndLenght(getString(R.string.rewarded_vids_updated_ok), Toast.LENGTH_LONG);
                        updateTotalWatchVids(totalWatchedVideos);
                        break;
                    case Constants.FAILED_SAVE:
                        alertManager.displayToastWithCustomTextAndLenght(getString(R.string.rewarded_vids_updated_nok), Toast.LENGTH_LONG);
                        break;
                    case Constants.UPDATE_WATCHED_VIDS:
                        updateTotalWatchVids(response.body().getVideosWatched());
                        break;
                }
            }
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_LONG);
        }
        if(isSaving)otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {watchVideoButton});
    }

    private void updateTotalWatchVids(int value)
    {
        totalWatchedVideos = value;
        preferencesManager.saveUserTotalWatchedVids(totalWatchedVideos);
        if(isVisible())populateViewWithInfo();
    }

    private void updateUserToPremium()
    {
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
        preferencesManager.saveUserPremium(Constants.PREMIUM);
        preferencesManager.saveUserTotalWatchedVids(0);
        navController.navigateUp();
    }

    @Override
    public void afterRewardedVideosLoadFailed(Throwable t)
    {
        if(t.toString().contains("SocketTimeoutException"))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_timeout), Toast.LENGTH_SHORT);
        }
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {watchVideoButton});
    }
}
