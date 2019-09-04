package com.b7anka.hollywoodtracker.Views.AddShow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Response;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.OtherSharedMethods;
import com.b7anka.hollywoodtracker.Helpers.RetrofitManager;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import com.b7anka.hollywoodtracker.Model.Show;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.Views.Movies.ShowsFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddShowFragment extends ShowsFragment implements RetrofitManager.SaveDataListener
{
    private String fragmentTitle;
    private Boolean isEditing;
    private int showId;
    private Show show;
    private EditText titleEditText;
    private EditText watchedTimeEditText;
    private EditText seasonEditText;
    private EditText episodeEditText;
    private Button saveButton;
    private CheckBox isBeginningCheckBox;
    private ProgressBar progressBar;
    private String type;
    private LinearLayout tvShowsRelatedStuffLinearLayout;

    public AddShowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_show, container, false);
        insertItems();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        this.toolbar.setTitle(fragmentTitle);
        setButtonsClickListeners();
        putBottomMenuVisible();
        putToolBarButtonsVisible();
    }

    @Override
    protected void insertItems()
    {
        super.insertItems();
        fragmentTitle = getArguments().getString(Constants.ADD_FRAGMENT_TITLE);
        isEditing = getArguments().getBoolean(Constants.IS_EDITING);
        titleEditText = view.findViewById(R.id.titleEditText);
        watchedTimeEditText = view.findViewById(R.id.watchedTimeEditText);
        seasonEditText = view.findViewById(R.id.seasonEditText);
        episodeEditText = view.findViewById(R.id.episodeEditText);
        isBeginningCheckBox = view.findViewById(R.id.isBeginingCheckBox);
        saveButton = view.findViewById(R.id.saveButton);
        retrofitManager.setSaveDataListener(this);
        tvShowsRelatedStuffLinearLayout = view.findViewById(R.id.tvshowsRelatedStuffLinearLayout);
        if(fragmentTitle.equals(getString(R.string.add_tvshow)) || fragmentTitle.equals(getString(R.string.edit_tvshow)))
        {
            tvShowsRelatedStuffLinearLayout.setVisibility(View.VISIBLE);
        }
        progressBar = view.findViewById(R.id.progressBar);
        type = tvShowsRelatedStuffLinearLayout.getVisibility() == View.VISIBLE ? Constants.TV_SHOWS : Constants.MOVIES;
        if(isEditing)
        {
            showId = getArguments().getInt(Constants.SHOW_ID);
            String typeOfShow = type.equals(Constants.MOVIES) ? Constants.MOVIE : Constants.TV_SHOW;
            show = showsViewModel.getShow(showId, typeOfShow);
            populateFieldsForEditing(show);
        }
        titleEditText.requestFocus();
    }

    @Override
    protected void setButtonsClickListeners()
    {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUserInputs();
            }
        });

        isBeginningCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    watchedTimeEditText.setText(Constants.BEGINNING_WATCH_TIME);
                    watchedTimeEditText.setEnabled(false);
                    if(seasonEditText.getVisibility() == View.VISIBLE)
                    {
                        seasonEditText.requestFocus();
                    }
                }
                else
                {
                    watchedTimeEditText.setText(Constants.EMPTY);
                    watchedTimeEditText.setEnabled(true);
                    watchedTimeEditText.requestFocus();
                }
            }
        });

        episodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    if(seasonEditText.getVisibility() == View.VISIBLE)
                    {
                        validateUserInputs();
                    }
                }
                return false;
            }
        });

        watchedTimeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    if(fragmentTitle.equals(getString(R.string.add_movie)) || fragmentTitle.equals(getString(R.string.edit_movie)))
                    {
                        validateUserInputs();
                    }
                }
                return false;
            }
        });
    }

    private void validateUserInputs()
    {
        String title = OtherSharedMethods.capitalizeString(titleEditText.getText().toString().trim());
        String watchedTime = watchedTimeEditText.getText().toString().trim();
        String season = Constants.EMPTY;
        String episode = Constants.EMPTY;
        boolean emptyInputs;
        boolean isEverythingValid;

        if(tvShowsRelatedStuffLinearLayout.getVisibility() == View.VISIBLE)
        {
            season = seasonEditText.getText().toString().trim();
            episode = episodeEditText.getText().toString().trim();

            if(!season.isEmpty() && !episode.isEmpty())
            {
                if(Long.parseLong(season) < 10)
                {
                    season = String.format("0%s",season);
                }

                if(Long.parseLong(episode) < 10)
                {
                    episode = String.format("0%s",episode);
                }
            }
        }

        if(tvShowsRelatedStuffLinearLayout.getVisibility() == View.VISIBLE)
        {
            emptyInputs = validator.checkForEmptyInputs(new String[] {title,watchedTime, season, episode});
        }
        else
        {
            emptyInputs = validator.checkForEmptyInputs(new String[] {title,watchedTime});
        }

        if(emptyInputs)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.empty_user_inputs), Toast.LENGTH_LONG);
            EditText[] editTexts = {titleEditText,watchedTimeEditText,seasonEditText,episodeEditText};
            for(EditText et: editTexts)
            {
                if(et.getText().toString().trim().isEmpty() && et.getVisibility() == View.VISIBLE)
                {
                    et.setError(getString(R.string.fill_this_field));
                    et.requestFocus();
                }
            }
        }
        else
        {
            //Title validation
            boolean isTitleValid = !validator.verifyTitleLength(title);
            if(!isTitleValid)titleEditText.setError(getString(R.string.on_title_too_big));
            boolean isWatchedTimeLengthValid = !validator.verifyWatchedTimeLength(watchedTime);

            //Watched time validation
            if(!isWatchedTimeLengthValid)watchedTimeEditText.setError(getString(R.string.on_watched_time_too_big));
            boolean isWatchedTimeRegexValid = validator.verifyValidWatchedTime(watchedTime);
            if(!isWatchedTimeRegexValid)watchedTimeEditText.setError(getString(R.string.on_watched_time_invalid));
            boolean isWatchedTimeValid = isWatchedTimeLengthValid && isWatchedTimeRegexValid;

            boolean isSeasonLengthValid;
            boolean isSeasonRegexValid;
            boolean isSeasonValid;
            boolean isEpisodeLengthValid;
            boolean isEpisodeRegexValid;
            boolean isEpisodeValid;

            if(tvShowsRelatedStuffLinearLayout.getVisibility() == View.VISIBLE)
            {
                //Season validation
                isSeasonLengthValid = !validator.verifySeasonAndEpisodeLength(season);
                if(!isSeasonLengthValid)seasonEditText.setError(getString(R.string.on_season_too_big));
                isSeasonRegexValid = validator.verifyValidSeason(season);
                if(!isSeasonRegexValid)seasonEditText.setError(getString(R.string.on_season_invalid));
                isSeasonValid = isSeasonLengthValid && isSeasonRegexValid;

                //Episode validation
                isEpisodeLengthValid = !validator.verifySeasonAndEpisodeLength(episode);
                if(!isEpisodeLengthValid)episodeEditText.setError(getString(R.string.on_episode_too_big));
                isEpisodeRegexValid = validator.verifyValidEpisode(episode);
                if(!isEpisodeRegexValid)episodeEditText.setError(getString(R.string.on_episode_invalid));
                isEpisodeValid = isEpisodeLengthValid && isEpisodeRegexValid;

                isEverythingValid = isTitleValid && isWatchedTimeValid && isSeasonValid && isEpisodeValid;
            }
            else
            {
                isEverythingValid = isTitleValid && isWatchedTimeValid;
            }

            if(isEverythingValid)
            {
                if(!isEditing)
                {
                    if(type.equals(Constants.MOVIES))
                    {
                        saveData(preferencesManager.getUserId(),type,title,watchedTime,0,0,0);
                    }
                    else
                    {
                        saveData(preferencesManager.getUserId(),type,title,watchedTime,Integer.parseInt(season),Integer.parseInt(episode),0);
                    }
                }
                else
                {
                    String content;
                    Show newShow = new Show();
                    newShow.setId(show.getId());
                    newShow.setIdUser(show.getIdUser());
                    newShow.setTitle(show.getTitle());
                    newShow.setWatchedTime(show.getWatchedTime());
                    newShow.setSeason(show.getSeason());
                    newShow.setEpisode(show.getEpisode());
                    newShow.setType(show.getType());
                    newShow.setCompleted(show.isCompleted());
                    newShow.setThumbnail(show.getThumbnail());

                    if(type.equals(Constants.MOVIES))
                    {
                        content = String.format("%s;%s;%d", title, watchedTime, 0);
                        newShow.setTitle(title);
                        newShow.setWatchedTime(watchedTime);
                    }
                    else
                    {
                        content = String.format("%s;%s;%s;%s;%d", title, season, episode, watchedTime, 0);
                        newShow.setTitle(title);
                        newShow.setWatchedTime(watchedTime);
                        newShow.setSeason(Integer.parseInt(season));
                        newShow.setEpisode(Integer.parseInt(episode));
                    }
                    updateUserData(newShow,type,content);
                    otherSharedMethods.hideKeyboard(episodeEditText);
                    otherSharedMethods.hideKeyboard(watchedTimeEditText);
                    navController.navigateUp();
                }
            }
        }
    }

    private void saveData(int idUser, String type, String title, String watchedTime, int season, int episode, int completed)
    {
        if(networkManager.isInternetAvailable())
        {
            progressBar.setVisibility(View.VISIBLE);
            retrofitManager.saveUserData(idUser,type,title,watchedTime,season,episode,completed);
        }
        else
        {
            alertManager.showNoInternetConnectionAlert();
        }
    }

    @Override
    protected void lastActivity()
    {
        getData();
    }

    @Override
    protected void getData()
    {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
    }

    private void populateFieldsForEditing(Show show)
    {
        titleEditText.setText(show.getTitle());
        watchedTimeEditText.setText(show.getWatchedTime());

        if(show.getType().equals(Constants.TV_SHOW))
        {
            seasonEditText.setText(String.valueOf(show.getSeason()));
            episodeEditText.setText(String.valueOf(show.getEpisode()));
        }
    }

    @Override
    protected void putBottomMenuVisible()
    {
        if(this.bottomNavigationView.getVisibility() == View.VISIBLE)
        {
            this.bottomNavigationView.setVisibility(View.GONE);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void putToolBarButtonsVisible()
    {
        if(this.linearLayout.getVisibility() == View.VISIBLE)
        {
            this.linearLayout.setVisibility(View.GONE);
        }

        if(this.getPremiumForFreeFloatingActionButton.getVisibility() == View.VISIBLE)this.getPremiumForFreeFloatingActionButton.setVisibility(View.GONE);
    }

    @Override
    public void afterDataSavedSuccessfully(Response<APIResponse> response)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.GONE);
        }

        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_reponding),Toast.LENGTH_LONG);
        }

        if(response.isSuccessful())
        {
            alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(), Toast.LENGTH_LONG);
            preferencesManager.saveShowsNeedUpdating(true);

            if(response.body().getSuccess())
            {
                if(type.equals(Constants.MOVIES))
                {
                    userViewModel.addOneToUserShows(Constants.MOVIES);
                }
                else
                {
                    userViewModel.addOneToUserShows(Constants.TV_SHOWS);
                }
                navController.navigateUp();
            }
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void afterDataSavedFailed(Throwable t)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.GONE);
        }
        if(t.toString().contains("SocketTimeoutException"))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_timeout),Toast.LENGTH_SHORT);
        }
    }
}
