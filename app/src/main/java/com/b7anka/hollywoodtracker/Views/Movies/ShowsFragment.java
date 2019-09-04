package com.b7anka.hollywoodtracker.Views.Movies;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Response;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.b7anka.hollywoodtracker.Adapter.DataAdapter;
import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.ImageManager;
import com.b7anka.hollywoodtracker.Helpers.RetrofitManager;
import com.b7anka.hollywoodtracker.Helpers.SharedPreferencesManager;
import com.b7anka.hollywoodtracker.Interfaces.SetOnMovieClickListener;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import com.b7anka.hollywoodtracker.Model.Result;
import com.b7anka.hollywoodtracker.Model.Show;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.ViewModels.ShowsViewModel;
import com.b7anka.hollywoodtracker.ViewModels.UserViewModel;
import com.b7anka.hollywoodtracker.Views.Base.BaseShowsAndUserFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowsFragment extends BaseShowsAndUserFragment implements SetOnMovieClickListener,
        RetrofitManager.GetDataListener, ImageManager.ShowImageDownloaderListener,
        RetrofitManager.UpdateDataListener, RetrofitManager.DeleteDataListener, RetrofitManager.CheckUserListener
{
    protected RecyclerView showsRecyclerView;
    protected LinearLayoutManager layoutManager;
    protected ImageView tempImageView;
    protected DataAdapter dataAdapter;
    protected ShowsViewModel showsViewModel;
    protected UserViewModel userViewModel;
    protected RetrofitManager retrofitManager;
    protected ImageManager pictureManager;
    protected boolean isClicked = false;
    protected List<Show> allShows;
    private List<Show> movies;

    public ShowsFragment()
    {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_all_shows, container, false);
        insertItems();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setButtonsClickListeners();
        lastActivity();
        getFreePremiumButtonClickListener();
        checkIfIsPremium();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            activateRefreshButton();
        }
        else
        {
            scrollViewListener();
        }
    }

    protected void lastActivity()
    {
        swipeRefreshLayout.setRefreshing(true);
        if(networkManager.isInternetAvailable())
        {
            retrofitManager.checkUserStatus(userViewModel.getUser(preferencesManager.getUserId()).getUsername());
        }
        else
        {
            getData();
        }
    }

    @SuppressLint("RestrictedApi")
    private void checkIfIsPremium()
    {
        if(preferencesManager.getUserPremium()==0)
        {
            adsManager.showBannerAdds(adView);
            adsManager.prepareInterstitialAd();
            buyPremiumButton.setVisibility(View.VISIBLE);
            getPremiumForFreeFloatingActionButton.setVisibility(View.VISIBLE);
        }
        else
        {
            buyPremiumButton.setVisibility(View.GONE);
            getPremiumForFreeFloatingActionButton.setVisibility(View.GONE);
        }
    }

    private void getFreePremiumButtonClickListener()
    {
        getPremiumForFreeFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.getPremiumFeaturesForFreeFragment);
            }
        });
    }

    protected void setButtonsClickListeners()
    {
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_moviesFragment_to_nav_settings);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.USER_ID,preferencesManager.getUserId());
                navController.navigate(R.id.action_moviesFragment_to_nav_about, bundle);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(networkManager.isInternetAvailable())
                {
                    goToAddFragment(getString(R.string.add_movie), false, null);
                }
                else
                {
                    alertManager.showNoInternetConnectionAlert();
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreferencesToFalse();
                navController.navigate(R.id.action_nav_movies_to_mainActivity);
                activity.finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshing();
            }
        });
    }

    protected void onRefreshing()
    {
        if(networkManager.isInternetAvailable())
        {
            movies.clear();
            getMoviesFromServer();
        }
        else
        {
            swipeRefreshLayout.setRefreshing(false);
            showNoInternetToRefresh();
        }
    }

    @TargetApi(23)
    protected void scrollViewListener()
    {
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if(scrollY > 0)
                {
                    swipeRefreshLayout.setEnabled(false);
                }
                else
                {
                    swipeRefreshLayout.setEnabled(true);
                }
            }
        });
    }

    protected void activateRefreshButton()
    {
        swipeRefreshLayout.setEnabled(false);
        refreshButton.setVisibility(View.VISIBLE);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setRefreshing(true);
                onRefreshing();
            }
        });
    }

    protected void showNoInternetToRefresh()
    {
        swipeRefreshLayout.setRefreshing(false);
        alertManager.showNoInternetConnectionAlert();
    }

    protected void goToAddFragment(String title, boolean isEditing, @Nullable Show show)
    {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ADD_FRAGMENT_TITLE, title);
        bundle.putBoolean(Constants.IS_EDITING, isEditing);
        if(show != null)bundle.putInt(Constants.SHOW_ID, show.getId());
        navigateToAddFragment(bundle);
    }

    protected void navigateToAddFragment(Bundle bundle)
    {
        navController.navigate(R.id.action_moviesFragment_to_addShowFragment, bundle);
    }

    protected void defineShows()
    {
        for(Show s : allShows)
        {
            if(s.getType().equals(Constants.MOVIE) && !s.isCompleted())
            {
                if(movies.contains(s))
                {
                    int index = movies.indexOf(s);
                    movies.set(index, s);
                }
                else
                {
                    movies.add(s);
                }
            }
        }
        getDataToRecyclerView();
    }

    @Override
    protected void insertItems()
    {
        super.insertItems();
        this.showsRecyclerView = view.findViewById(R.id.rvData);
        this.pictureManager = new ImageManager((ImageManager.ShowImageDownloaderListener) this);
        if(this.showsRecyclerView != null)this.showsRecyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(this.activity);
        if(this.showsRecyclerView != null)this.showsRecyclerView.setLayoutManager(this.layoutManager);
        this.tempImageView = new ImageView(this.activity);
        this.showsInformationTextView = view.findViewById(R.id.showsInformationTextView);
        showsViewModel = ViewModelProviders.of(this).get(ShowsViewModel.class);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        allShows = new ArrayList<>();
        retrofitManager = new RetrofitManager(this,this,this, this);
        putBottomMenuVisible();
        putToolBarButtonsVisible();
        fragmentNameTextView.setText(getString(R.string.movies));
        movies = new ArrayList<>();
        swipeRefreshLayout.setEnabled(true);
        allShows = showsViewModel.getAllShows(preferencesManager.getUserId());
    }

    protected void populateDataView(List<Show> shows, String info)
    {
        dataAdapter = new DataAdapter(this.activity,shows,this);
        showsRecyclerView.setAdapter(dataAdapter);
        if(layoutManager.getItemCount() > 0)
        {
            showsInformationTextView.setVisibility(View.GONE);
        }
        else
        {
            showsInformationTextView.setText(info);
            showsInformationTextView.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    protected void getData()
    {
        if(preferencesManager.getShowsNeedUpdating())
        {
            preferencesManager.saveShowsNeedUpdating(false);
            getShowsFromServer();
        }
        else if(preferencesManager.getShowWasEdited())
        {
            preferencesManager.saveShowWasEdited(false);
            allShows = showsViewModel.getAllShows(preferencesManager.getUserId());
            defineShows();
        }
        else
        {
            if(allShows != null && allShows.size() > 0)
            {
                defineShows();
            }
            else
            {
                getShowsFromServer();
            }
        }
    }

    private void getShowsFromServer()
    {
        if(networkManager.isInternetAvailable())
        {
            getMoviesFromServer();
            getTvShowsFromServer();
            getRecentFromServer();
        }
        else
        {
            swipeRefreshLayout.setRefreshing(false);
            showsInformationTextView.setVisibility(View.VISIBLE);
            showsInformationTextView.setText(getString(R.string.no_data_to_show));
            alertManager.showNoInternetConnectionAlert();
        }
    }

    protected void getMoviesFromServer()
    {
        retrofitManager.getData(preferencesManager.getUserId(), Constants.MOVIES);
    }

    protected void getTvShowsFromServer()
    {
        retrofitManager.getData(preferencesManager.getUserId(), Constants.TV_SHOWS);
    }

    protected void getRecentFromServer()
    {
        retrofitManager.getData(preferencesManager.getUserId(), Constants.RECENTLY_WATCHED);
    }

    @Override
    public void afterDataRetrievedSuccessfully(Response<APIResponse> response)
    {
        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_responding), Toast.LENGTH_SHORT);
        }

        if(response.isSuccessful())
        {
            if(response.body().getSuccess())
            {
                if(response.body().getResults().size()>0)
                {
                    for(Result r : response.body().getResults())
                    {
                        Show show;
                        int userId = r.getUserId() != null ? r.getUserId() : preferencesManager.getUserId();
                        if(r.getType().equals(Constants.TV_SHOW))
                        {
                            show = new Show();
                            show.setId(r.getId());
                            show.setIdUser(userId);
                            show.setTitle(r.getTitle());
                            show.setWatchedTime(r.getTimewatched());
                            show.setSeason(r.getSeason());
                            show.setEpisode(r.getEpisode());
                            show.setType(r.getType());
                            show.setCompleted(r.getCompleted());
                            show.setThumbnail(Constants.EMPTY);
                        }
                        else
                        {
                            show = new Show();
                            show.setId(r.getId());
                            show.setIdUser(userId);
                            show.setTitle(r.getTitle());
                            show.setWatchedTime(r.getTimewatched());
                            show.setSeason(0);
                            show.setEpisode(0);
                            show.setType(r.getType());
                            show.setCompleted(r.getCompleted());
                            show.setThumbnail(Constants.EMPTY);
                        }

                        pictureManager.loadImageFromURL(r.getThumbnail(), show);
                    }
                }
                else
                {
                    if(getActivity() != null)getDataToRecyclerView();
                }
            }
            else
            {
                alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(), Toast.LENGTH_LONG);
            }
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void afterDataRetrievedFailed(Throwable t)
    {
        showMessageOnRetrofitFailed(t);
    }

    @Override
    public void setOnShowClick(int position)
    {
        if(!isClicked)askUserIfHeHasFinishedWatchingTheShow(movies.get(position));
    }

    @Override
    public void onImageDownloadCompleted(Bitmap bitmap, Show show)
    {
        String fileName = ImageManager.saveToInternalStorage(bitmap,this.activity);
        show.setThumbnail(fileName);
        if(allShows.size() > 0)
        {
            for(int i = 0; i < allShows.size(); i++)
            {
                if(allShows.get(i).getId() == show.getId() && allShows.get(i).getType().equals(show.getType()))
                {
                    allShows.remove(i);
                }
            }
        }
        allShows.add(show);
        showsViewModel.insertShow(show);
        if(networkManager.isInternetAvailable())
        {
            if(isVisible())checkForShowOnShowsArray(show);
        }
        else
        {
            checkForShowOnShowsArray(show);
        }
    }

    protected void checkForShowOnShowsArray(Show show)
    {
        for(int i = 0; i < movies.size(); i++)
        {
            if(movies.get(i).getId() == show.getId())
            {
                movies.remove(i);
            }
        }
        defineShows();
    }

    protected void askUserIfHeHasFinishedWatchingTheShow(final Show show)
    {
        isClicked = true;

        final String movieOrEpisode = show.getType().equals(Constants.MOVIE) ? getString(R.string.movie) : getString(R.string.episode);
        final String type = show.getType().equals(Constants.MOVIE) ? Constants.MOVIES : Constants.TV_SHOWS;

        final AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(activity);

        builder.setTitle(getString(R.string.title_finished))
                .setMessage(String.format("%s %s?", getString(R.string.aks_user_if_he_has_finished), movieOrEpisode))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        updateUserData(show, type, Constants.CONTENT_COMPLETED);
                        getDataToRecyclerView();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        askUserIfHeWantsToEditShow(show);
                    }
                })
                .setNeutralButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                        isClicked = false;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    protected void updateUserData(Show show, String type, String content)
    {
        Show newShow = new Show();
        newShow.setId(show.getId());
        newShow.setIdUser(show.getIdUser());
        newShow.setTitle(show.getTitle());
        newShow.setWatchedTime(show.getWatchedTime());
        newShow.setSeason(show.getSeason());
        newShow.setEpisode(show.getEpisode());
        newShow.setType(show.getType());
        newShow.setThumbnail(show.getThumbnail());

        if(content.equals(Constants.CONTENT_COMPLETED))
        {
            newShow.setCompleted(true);
            removeShowFromList(newShow);
        }
        else
        {
            String temp[] = content.split(";");
            if(show.getType().equals(Constants.MOVIE))
            {
                newShow.setTitle(temp[0]);
                newShow.setWatchedTime(temp[1]);
            }
            else
            {
                newShow.setTitle(temp[0]);
                newShow.setWatchedTime(temp[3]);
                newShow.setSeason(Integer.parseInt(temp[1]));
                newShow.setEpisode(Integer.parseInt(temp[2]));
            }
        }

        if(networkManager.isInternetAvailable())
        {
            retrofitManager.updateUserData(newShow.getId(),preferencesManager.getUserId(),type,content);
        }
        else
        {
            createOfflineChange(newShow.getId(), type, content);
        }

        updateLocalData(newShow);
    }

    private void updateLocalData(Show show)
    {
        showsViewModel.updateShow(show);
        preferencesManager.saveShowWasEdited(true);
    }

    protected void createOfflineChange(int id, String type, String content)
    {
        offlineChangesViewModel.insert(id, type, content);
        alertManager.displayToastWithCustomTextAndLenght(getString(R.string.updated_data_saved_offline), Toast.LENGTH_LONG);
        isClicked = false;
    }

    protected void askUserIfHeWantsToEditShow(final Show show)
    {
        final String movieOrEpisode = show.getType().equals(Constants.MOVIE) ? getString(R.string.movie) : getString(R.string.episode);
        final String titleOfFragment = show.getType().equals(Constants.MOVIE) ? getString(R.string.edit_movie) : getString(R.string.edit_tvshow);
        final AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(activity);

        builder.setTitle(getString(R.string.title_edit_show))
                .setMessage(String.format("%s %s?", getString(R.string.aks_user_if_he_wants_to_edit_show), movieOrEpisode))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        goToAddFragment(titleOfFragment, true, show);
                        isClicked = false;
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        isClicked = false;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    @Override
    public void afterUpdatedDataSuccess(Response<APIResponse> response)
    {
        showOnRetrofitSuccessGenericMessage(response);
    }

    @Override
    public void afterUpdateDataFailed(Throwable t)
    {
        showMessageOnRetrofitFailed(t);
    }

    @Override
    public void afterDataDeletionSuccessfully(Response<APIResponse> response)
    {
        showOnRetrofitSuccessGenericMessage(response);
    }

    @Override
    public void afterDataDeletionFailed(Throwable t)
    {
        showMessageOnRetrofitFailed(t);
    }

    protected void showMessageOnRetrofitFailed(Throwable t)
    {
        if(t.toString().contains("SocketTimeoutException"))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_timeout),Toast.LENGTH_SHORT);
        }
        isClicked = false;
    }

    protected void showOnRetrofitSuccessGenericMessage(Response<APIResponse> response)
    {
        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_responding), Toast.LENGTH_SHORT);
        }

        if(response.isSuccessful())
        {
            alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(), Toast.LENGTH_LONG);
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_LONG);
        }
        isClicked = false;
    }

    protected void removeShowFromList(Show show)
    {
        int index = -1;
        for(Show s : movies)
        {
            if(s.getId() == show.getId())
            {
                index = movies.indexOf(s);
            }
        }
        if(index != -1)movies.remove(index);
        userViewModel.subtractOneToUserShows(Constants.MOVIES);
        userViewModel.addOneToUserShows(Constants.RECENTLY_WATCHED);
    }

    protected void getDataToRecyclerView()
    {
        if(!networkManager.isInternetAvailable())
        {
            populateDataView(movies, getString(R.string.no_movies_to_show));
        }
        else
        {
            if(isVisible())populateDataView(movies, getString(R.string.no_movies_to_show));
        }
    }

    @Override
    public void afterUserCheckedSuccessfully(Response<APIResponse> response)
    {
        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_responding), Toast.LENGTH_SHORT);
        }

        if(response.isSuccessful())
        {
            if(response.body().getSuccess())
            {
                if(response.body().getLastActivity() > SharedPreferencesManager.getTimeStamp())
                {
                    preferencesManager.saveShowsNeedUpdating(true);
                    preferencesManager.saveProfileNeedsUpdating(true);
                    allShows.clear();
                    movies.clear();
                    showsViewModel.deleteAllShows(preferencesManager.getUserId());
                }
            }
            else
            {
                showUserInvalidDialog();
            }
        }
        if(isVisible())getData();
    }

    protected void showUserInvalidDialog()
    {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setPreferencesToFalse();
                userViewModel.delete(userViewModel.getUser(preferencesManager.getUserId()));
                navController.navigate(R.id.action_nav_movies_to_mainActivity);
                activity.finish();
            }
        };
        alertManager.displayErrorAlertWithCustomTitleAndMsgIconAndOneListener(getString(R.string.error),
                getString(R.string.user_invalid),android.R.drawable.ic_dialog_alert, listener);
    }

    @Override
    public void afterUserCheckedFails(Throwable t)
    {
        if(isVisible())showMessageOnRetrofitFailed(t);
    }
}
