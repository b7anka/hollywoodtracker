package com.b7anka.hollywoodtracker.Views.Profile;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Response;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.ImageManager;
import com.b7anka.hollywoodtracker.Helpers.OtherSharedMethods;
import com.b7anka.hollywoodtracker.Helpers.RetrofitManager;
import com.b7anka.hollywoodtracker.Helpers.SharedPreferencesManager;
import com.b7anka.hollywoodtracker.Model.APIResponse;;
import com.b7anka.hollywoodtracker.Model.User;
import com.b7anka.hollywoodtracker.ViewModels.UserViewModel;
import com.b7anka.hollywoodtracker.Views.Base.BaseCameraFragment;
import com.b7anka.hollywoodtracker.Views.Base.BaseShowsAndUserFragment;
import com.b7anka.hollywoodtracker.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends BaseShowsAndUserFragment implements RetrofitManager.GetDataListener,
        RetrofitManager.UpdateDataListener, RetrofitManager.DeleteUserAccountListener, RetrofitManager.CheckUserListener, RetrofitManager.AfterGoogleAccountUpdatedListener,
        ImageManager.ImageDownloaderListener, GoogleApiClient.OnConnectionFailedListener, BaseCameraFragment.DialogCancelListener
{
    private UserViewModel viewModel;
    private MutableLiveData<User> userLiveData;
    private User newUser;
    private ImageView userThumbnailImageView;
    private TextView moviesWatchingTextView;
    private TextView tvShowsWatchingTextView;
    private TextView recentlyWatchedTextView;
    private TextView totalWatchedTextView;
    private TextView premiumUserTextView;
    private EditText usernameProfileEditText;
    private EditText fullNameProfileEditText;
    private EditText emailProfileEditText;
    private Button changePasswordButton;
    private Button editProfileButton;
    private Button deleteMyAccountButton;
    private Button unlinkGoogleAccountButton;
    private TextView changeImageTextView;
    private RetrofitManager retrofitManager;
    private ImageManager imageManager;
    private OtherSharedMethods otherSharedMethods;
    private String content;
    private GoogleApiClient googleApiClient;

    public ProfileFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        insertItems();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setButtonsClickListeners();
        lastActivity();
        checkIfIsPremium();
        userLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                updateUser(user);
            }
        });
    }

    private void lastActivity()
    {
        swipeRefreshLayout.setRefreshing(true);
        if(networkManager.isInternetAvailable())
        {
            retrofitManager.checkUserStatus(userLiveData.getValue().getUsername());
        }
        else
        {
            populateUserView();
        }
    }

    private void updateUser(User user)
    {
        viewModel.update(user);
        populateUserView();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void insertItems()
    {
        super.insertItems();
        imageManager = new ImageManager(this);
        userLiveData = new MutableLiveData<>();
        otherSharedMethods = new OtherSharedMethods(context);
        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.getUser(preferencesManager.getUserId());
        userLiveData.setValue(viewModel.getUser(preferencesManager.getUserId()));
        retrofitManager = new RetrofitManager(this, this, this, this, this);
        fragmentNameTextView.setText(getString(R.string.profile));
        userThumbnailImageView = view.findViewById(R.id.ivUserPhoto);
        moviesWatchingTextView = view.findViewById(R.id.tvMoviesWatching);
        tvShowsWatchingTextView = view.findViewById(R.id.tvShowsWatching);
        recentlyWatchedTextView = view.findViewById(R.id.tvRecentlyWatched);
        totalWatchedTextView = view.findViewById(R.id.tvTotalWatched);
        premiumUserTextView = view.findViewById(R.id.tvPremiumUser);
        usernameProfileEditText = view.findViewById(R.id.etUsernameProfile);
        fullNameProfileEditText = view.findViewById(R.id.etFullnameProfile);
        emailProfileEditText = view.findViewById(R.id.etEmailProfile);
        putBottomMenuVisible();
        putToolBarButtonsVisible();
        addButton.setVisibility(View.GONE);
        deleteAllButton.setVisibility(View.GONE);
        getPremiumForFreeFloatingActionButton.setVisibility(View.GONE);
        changePasswordButton = view.findViewById(R.id.btnChangePassword);
        unlinkGoogleAccountButton = view.findViewById(R.id.unlinkGoogleAccountButton);
        editProfileButton = view.findViewById(R.id.btnEditProfile);
        deleteMyAccountButton = view.findViewById(R.id.btnDeleteAccount);
        userThumbnailImageView.setEnabled(false);
        changeImageTextView = view.findViewById(R.id.changeImageTextView);
        dialogCancelListener = this;
        if(preferencesManager.getGoogleAccountState())unlinkGoogleAccountButton.setVisibility(View.VISIBLE);
    }

    private void setButtonsClickListeners()
    {
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_profileFragment_to_nav_settings);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.USER_ID,preferencesManager.getUserId());
                navController.navigate(R.id.action_profileFragment_to_nav_about, bundle);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreferencesToFalse();
                logout();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                navController.navigate(R.id.action_nav_profile_to_changePasswordFromProfileFragment);
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!fullNameProfileEditText.isEnabled())
                {
                    prepareViewForProfileEditing();
                }
                else
                {
                    validateUserInputs();
                }
            }
        });

        fullNameProfileEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    if(!emailProfileEditText.isEnabled())
                    {
                        validateUserInputs();
                    }
                }
                return false;
            }
        });

        emailProfileEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    validateUserInputs();
                }
                return false;
            }
        });

        deleteMyAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(networkManager.isInternetAvailable())
                {
                    showDialogToAskToDeleteAccount();
                }
                else
                {
                    alertManager.showNoInternetConnectionAlert();
                }
            }
        });

        userThumbnailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlertForUserToChooseWhereToFetchImage(userThumbnailImageView);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshing();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefreshing();
            }
        });

        unlinkGoogleAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlinkGoogleAccount();
            }
        });
    }

    private void unlinkGoogleAccount()
    {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(getActivity(),this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();

        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(activity);

        builder.setTitle(getString(R.string.are_you_sure))
                .setMessage(getString(R.string.ask_user_if_he_wants_to_unlink_his_google_account))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(networkManager.isInternetAvailable())
                        {
                            swipeRefreshLayout.setRefreshing(true);
                            otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {unlinkGoogleAccountButton, editProfileButton, changePasswordButton, deleteMyAccountButton});
                            retrofitManager.updateGoogleAccountStatus(userLiveData.getValue().getEmail(), false);
                        }
                        else
                        {
                            alertManager.showNoInternetConnectionAlert();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        OtherSharedMethods.disconnectGoogleApiClient(googleApiClient, getActivity());
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void logout()
    {
        navController.navigate(R.id.action_nav_profile_to_mainActivity);
        activity.finish();
    }

    private void showDialogToAskToDeleteAccount()
    {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.warning))
                .setCancelable(false)
                .setMessage(getString(R.string.action_delete_account))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        deleteUserAccount();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteUserAccount()
    {
        swipeRefreshLayout.setRefreshing(true);
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {changePasswordButton, deleteMyAccountButton, editProfileButton, unlinkGoogleAccountButton});
        retrofitManager.deleteUserAccount(preferencesManager.getUserId());
    }

    private void onRefreshing()
    {
        if(networkManager.isInternetAvailable())
        {
            getProfileFromServer();
        }
        else
        {
            swipeRefreshLayout.setRefreshing(false);
            alertManager.showNoInternetConnectionAlert();
        }
    }

    private void validateUserInputs()
    {
        String username = usernameProfileEditText.getText().toString().trim();
        String fullName = OtherSharedMethods.capitalizeString(fullNameProfileEditText.getText().toString().trim());
        String email = emailProfileEditText.getText().toString().trim();

        if(validator.checkForEmptyInputs(new String[] {username,fullName,email}))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.empty_user_inputs), Toast.LENGTH_LONG);
            EditText [] editTexts = {usernameProfileEditText, fullNameProfileEditText, emailProfileEditText};
            for(EditText et: editTexts)
            {
                if(et.getText().toString().trim().isEmpty())
                {
                    et.setError(getString(R.string.fill_this_field));
                    et.requestFocus();
                }
            }
        }
        else
        {
            //Username validation
            boolean isUserNameLengthValid = !validator.verifyUsernameLength(username);
            if(!isUserNameLengthValid)usernameProfileEditText.setError(getString(R.string.username_toobig));
            boolean isUserNameContentValid = validator.verifyUsernameContent(username);
            if(!isUserNameContentValid)usernameProfileEditText.setError(getString(R.string.username_requirements));
            boolean isUserNameValid = isUserNameLengthValid && isUserNameContentValid;

            //Full name validation
            boolean isFullNameLengthValid = !validator.verifyFullNameLength(fullName);
            if(!isFullNameLengthValid)fullNameProfileEditText.setError(getString(R.string.fullname_toobig));
            boolean isFullNameSizeValid = validator.verifyFullNameSize(fullName);
            if(!isFullNameSizeValid)fullNameProfileEditText.setError(getString(R.string.fullname_size));
            boolean isFullNameRegexValid = validator.verifyFullNameRegex(fullName);
            if(!isFullNameRegexValid)fullNameProfileEditText.setError(getString(R.string.only_letter_and_spaces_on_name));
            boolean isFullNameValid = isFullNameLengthValid && isFullNameSizeValid && isFullNameRegexValid;

            //Email validation
            boolean isEmailLengthValid = !validator.verifyEmailAddressLength(email);
            if(!isEmailLengthValid)emailProfileEditText.setError(getString(R.string.email_toobig));
            boolean isEmailContentValid = validator.verifyValidEmailAddress(email);
            if(!isEmailContentValid)emailProfileEditText.setError(getString(R.string.email_notValid));
            boolean isEmailValid = isEmailLengthValid && isEmailContentValid;
            boolean everyThingIsValid = isFullNameValid && isUserNameValid && isEmailValid;

            if(everyThingIsValid)
            {
                int id = userLiveData.getValue() != null ? userLiveData.getValue().getId() : preferencesManager.getUserId();
                String base64ImageStringToSend = this.imageBase64 != null && !this.imageBase64.isEmpty() ? "data:image/jpg{base64," + this.imageBase64 : "";
                content = username + ";" + fullName + ";" + email + ";" + base64ImageStringToSend;
                updateUserData(id,Constants.PROFILE,content);
                this.imageBase64 = "";
                if(emailProfileEditText.isEnabled())
                {
                    otherSharedMethods.hideKeyboard(emailProfileEditText);
                }
                else
                {
                    otherSharedMethods.hideKeyboard(fullNameProfileEditText);
                }
            }
        }
    }

    private void updateLocalUser(String username, String fullName, String email, String base64ImageStringToSend)
    {
        User newUser = new User();
        newUser.setId(userLiveData.getValue().getId());
        newUser.setUsername(username);
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPassword(userLiveData.getValue().getPassword());
        newUser.setPremium(userLiveData.getValue().isPremium());
        newUser.setMovies(userLiveData.getValue().getMovies());
        newUser.setTvShows(userLiveData.getValue().getTvShows());
        newUser.setRecent(userLiveData.getValue().getRecent());
        newUser.setTotal(userLiveData.getValue().getTotal());
        newUser.setThumbnail(userLiveData.getValue().getThumbnail());

        if(!base64ImageStringToSend.isEmpty())
        {
            Drawable d = userThumbnailImageView.getDrawable();
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) d);
            Bitmap bitmap = bitmapDrawable.getBitmap();
            String filename = ImageManager.saveToInternalStorage(bitmap,activity);
            newUser.setThumbnail(filename);
        }

        userLiveData.postValue(newUser);
    }

    private void updateUserData(int id, String type, String content)
    {
        if(networkManager.isInternetAvailable())
        {
            retrofitManager.updateUserData(id,preferencesManager.getUserId(),type,content);
        }
        else
        {
            String[] temp = content.split(Constants.SEMI_COLON);
            if(temp.length >= 4)
            {
                updateLocalUser(temp[0], temp[1], temp[2], temp[3]);
            }
            else
            {
                updateLocalUser(temp[0], temp[1], temp[2], Constants.EMPTY);
            }
            createOfflineChange(id, type, content);
            resetViewToDefault();
        }
    }

    private void resetViewToDefault()
    {
        editProfileButton.setText(getString(R.string.btn_edit));
        userThumbnailImageView.setEnabled(false);
        changeImageTextView.setVisibility(View.GONE);
        usernameProfileEditText.setEnabled(false);
        fullNameProfileEditText.setEnabled(false);
        emailProfileEditText.setEnabled(false);
        unlinkGoogleAccountButton.setEnabled(true);
        editProfileButton.setEnabled(true);
        changePasswordButton.setEnabled(true);
        deleteMyAccountButton.setEnabled(true);
        deleteMyAccountButton.setBackgroundColor(getResources().getColor(R.color.colorButtonRed));
        unlinkGoogleAccountButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        changePasswordButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        editProfileButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    private void createOfflineChange(int id, String type, String content)
    {
        offlineChangesViewModel.insert(id, type, content);
        alertManager.displayToastWithCustomTextAndLenght(getString(R.string.updated_data_saved_offline), Toast.LENGTH_LONG);
    }

    private void prepareViewForProfileEditing()
    {
        fullNameProfileEditText.setEnabled(true);
        if(networkManager.isInternetAvailable())
        {
            usernameProfileEditText.setEnabled(true);
            fullNameProfileEditText.setEnabled(true);
            emailProfileEditText.setEnabled(true);
            usernameProfileEditText.requestFocus();
        }
        else
        {
            fullNameProfileEditText.requestFocus();
        }
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {changePasswordButton, deleteMyAccountButton, unlinkGoogleAccountButton});
        userThumbnailImageView.setEnabled(true);
        changeImageTextView.setVisibility(View.VISIBLE);
        editProfileButton.setText(getString(R.string.save));
    }

    private void getProfileFromServer()
    {
        retrofitManager.getData(preferencesManager.getUserId(), Constants.PROFILE);
    }

    private void populateUserView()
    {
        tvShowsWatchingTextView.setText(String.format("%s: %s", getString(R.string.tvshows), userLiveData.getValue().getTvShows()));
        moviesWatchingTextView.setText(String.format("%s: %s", getString(R.string.movies), userLiveData.getValue().getMovies()));
        recentlyWatchedTextView.setText(String.format("%s: %s", getString(R.string.recent), userLiveData.getValue().getRecent()));
        totalWatchedTextView.setText(String.format("%s: %s", getString(R.string.total), userLiveData.getValue().getTotal()));
        String premium = userLiveData.getValue().isPremium() ? getString(R.string.yes) : getString(R.string.no);
        premiumUserTextView.setText(String.format("%s: %s", getString(R.string.premium_user), premium));
        usernameProfileEditText.setText(userLiveData.getValue().getUsername());
        fullNameProfileEditText.setText(userLiveData.getValue().getFullName());
        emailProfileEditText.setText(userLiveData.getValue().getEmail());
        Bitmap bitmap = ImageManager.loadImageFromStorage(userLiveData.getValue().getThumbnail());
        userThumbnailImageView.setImageBitmap(bitmap);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void afterDataRetrievedSuccessfully(Response<APIResponse> response)
    {
        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_responding), Toast.LENGTH_SHORT);
        }

        if(!response.isSuccessful())
        {
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_LONG);
        }
        else
        {
            newUser = new User();
            newUser.setId(response.body().getResults().get(Constants.FIRST_OBJECT).getId());
            newUser.setUsername(response.body().getResults().get(Constants.FIRST_OBJECT).getUsername());
            newUser.setFullName(response.body().getResults().get(Constants.FIRST_OBJECT).getFullname());
            newUser.setEmail(response.body().getResults().get(Constants.FIRST_OBJECT).getEmail());
            newUser.setPassword(userLiveData.getValue().getPassword());
            newUser.setPremium(response.body().getResults().get(Constants.FIRST_OBJECT).getPremium() == 1);
            newUser.setMovies(response.body().getResults().get(Constants.FIRST_OBJECT).getMovies());
            newUser.setTvShows(response.body().getResults().get(Constants.FIRST_OBJECT).getTvshows());
            newUser.setRecent(response.body().getResults().get(Constants.FIRST_OBJECT).getRecentlywatched());
            newUser.setTotal(response.body().getResults().get(Constants.FIRST_OBJECT).getTotal());
            newUser.setThumbnail(Constants.EMPTY);

            imageManager.loadImageFromURL(response.body().getResults().get(Constants.FIRST_OBJECT).getThumbnail());
            preferencesManager.saveUserPremium(response.body().getResults().get(Constants.FIRST_OBJECT).getPremium());

        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showMessageOnRetrofitFailed(Throwable t)
    {
        if(t.toString().contains("SocketTimeoutException"))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_timeout),Toast.LENGTH_SHORT);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void afterDataRetrievedFailed(Throwable t)
    {
        showMessageOnRetrofitFailed(t);
    }

    @Override
    public void onImageDownloadCompleted(Bitmap bitmap)
    {
        String fileName = ImageManager.saveToInternalStorage(bitmap, activity);
        newUser.setThumbnail(fileName);
        userLiveData.postValue(newUser);
        newUser = null;
    }

    @Override
    public void afterUpdatedDataSuccess(Response<APIResponse> response)
    {
        swipeRefreshLayout.setRefreshing(false);

        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_responding), Toast.LENGTH_SHORT);
        }

        if(response.isSuccessful())
        {
            if(response.body().getSuccess())
            {
                String[] temp = content.split(Constants.SEMI_COLON);
                if(temp.length >= 4)
                {
                    updateLocalUser(temp[0], temp[1], temp[2], temp[3]);
                }
                else
                {
                    updateLocalUser(temp[0], temp[1], temp[2], Constants.EMPTY);
                }
                resetViewToDefault();
            }
            alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(), Toast.LENGTH_LONG);
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void afterUpdateDataFailed(Throwable t)
    {
        showMessageOnRetrofitFailed(t);
    }

    @Override
    public void onDialogCancelled()
    {
        resetViewToDefault();
    }

    @Override
    public void afterUserAccountDeletionSuccessfully(Response<APIResponse> response)
    {
        swipeRefreshLayout.setRefreshing(false);

        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_responding), Toast.LENGTH_SHORT);
        }

        if(response.isSuccessful())
        {
            alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(), Toast.LENGTH_LONG);
            if(response.body().getSuccess())
            {
                deleteLocalData();
            }
            else
            {
                resetViewToDefault();
            }
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_LONG);
        }
    }

    private void checkIfIsPremium()
    {
        if(preferencesManager.getUserPremium()==0)
        {
            adsManager.showBannerAdds(adView);
            adsManager.prepareInterstitialAd();
            buyPremiumButton.setVisibility(View.VISIBLE);
        }
        else
        {
            buyPremiumButton.setVisibility(View.GONE);
        }
    }

    private void deleteLocalData()
    {
        viewModel.delete(userLiveData.getValue());
        setPreferencesToFalse();
        resetViewToDefault();
        logout();
    }

    @Override
    public void afterUserAccountDeletionFailed(Throwable t)
    {
        resetViewToDefault();
        showMessageOnRetrofitFailed(t);
    }

    @Override
    public void afterUserCheckedSuccessfully(Response<APIResponse> response)
    {
        swipeRefreshLayout.setRefreshing(false);

        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_responding), Toast.LENGTH_SHORT);
        }

        if(response.isSuccessful())
        {
            if(!response.body().getSuccess())
            {
                showUserInvalidDialog();
            }
            else
            {
                if(response.body().getLastActivity() > SharedPreferencesManager.getTimeStamp() || preferencesManager.getProfileNeedsUpdating())
                {
                    preferencesManager.saveProfileNeedsUpdating(false);
                    getProfileFromServer();
                }
                else
                {
                    if(isVisible())populateUserView();
                }
            }
        }
    }

    protected void showUserInvalidDialog()
    {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setPreferencesToFalse();
                viewModel.delete(viewModel.getUser(preferencesManager.getUserId()));
                logout();
            }
        };
        alertManager.displayErrorAlertWithCustomTitleAndMsgIconAndOneListener(getString(R.string.error),
                getString(R.string.user_invalid),android.R.drawable.ic_dialog_alert, listener);
    }

    @Override
    public void afterUserCheckedFails(Throwable t)
    {
        swipeRefreshLayout.setRefreshing(false);
        if(isVisible())showMessageOnRetrofitFailed(t);
    }

    @Override
    public void afterGoogleAccountUpdatedSuccessfully(Response<APIResponse> response)
    {
        if(response.code() != 200)
        {
            swipeRefreshLayout.setRefreshing(false);
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_responding), Toast.LENGTH_SHORT);
        }

        if(response.isSuccessful())
        {

            if(response.body().getSuccess())
            {
                logoutFromGoogle();
            }
            else
            {
                alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(),Toast.LENGTH_SHORT);
            }

        }
        else
        {
            swipeRefreshLayout.setRefreshing(false);
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_LONG);
        }
    }

    private void logoutFromGoogle()
    {
        if(googleApiClient.isConnected())
        {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if(status.isSuccess())
                    {
                        alertManager.displayToastWithCustomTextAndLenght(getString(R.string.successfully_unliked_google_account),Toast.LENGTH_LONG);
                        preferencesManager.saveGoogleAccountState(false);
                        unlinkGoogleAccountButton.setVisibility(View.GONE);
                        OtherSharedMethods.disconnectGoogleApiClient(googleApiClient, getActivity());
                    }
                    else
                    {
                        alertManager.displayToastWithCustomTextAndLenght(getString(R.string.failed_unliked_google_account),Toast.LENGTH_LONG);
                    }
                }
            });
            resetViewToDefault();
            swipeRefreshLayout.setRefreshing(false);
        }
        else
        {
            logoutFromGoogle();
        }
    }

    @Override
    public void afterGoogleAccountUpdatedFailed(Throwable t)
    {
        resetViewToDefault();
        showMessageOnRetrofitFailed(t);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        if(connectionResult.getErrorMessage() != null)
        {
            alertManager.displayErrorAlertWithCustomTitleAndMsgAndIcon
                    (getString(R.string.error), connectionResult.getErrorMessage(),android.R.drawable.ic_dialog_alert);
        }
        else
        {
            alertManager.displayErrorAlertWithCustomTitleAndMsgAndIcon
                    (getString(R.string.error), getString(R.string.failed_to_connect_to_google),android.R.drawable.ic_dialog_alert);
        }
        resetViewToDefault();
    }

    @Override
    public void onPause() {
        super.onPause();
        OtherSharedMethods.disconnectGoogleApiClient(googleApiClient, getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OtherSharedMethods.disconnectGoogleApiClient(googleApiClient, getActivity());
    }
}
