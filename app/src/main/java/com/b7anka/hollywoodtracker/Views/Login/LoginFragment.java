package com.b7anka.hollywoodtracker.Views.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.ImageManager;
import com.b7anka.hollywoodtracker.Helpers.OtherSharedMethods;
import com.b7anka.hollywoodtracker.Helpers.RetrofitManager;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import com.b7anka.hollywoodtracker.Model.User;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.ViewModels.UserViewModel;
import com.b7anka.hollywoodtracker.Views.Base.BaseLoginAndRegisterFragment;
import com.github.javafaker.Faker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseLoginAndRegisterFragment implements RetrofitManager.AfterLoginListener, RetrofitManager.AfterLoginWithGoogleListener, RetrofitManager.AfterGoogleAccountUpdatedListener, RetrofitManager.RegisterWithGoogleListener, GoogleApiClient.OnConnectionFailedListener {

    private Button loginButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView forgotPasswordTextView;
    private CheckBox showPasswordCheckBox;
    private Switch rememberMeSwitch;
    private UserViewModel viewModel;
    private User user;
    private RetrofitManager retrofitManager;
    private Button signInWithGoogleButton;
    private GoogleApiClient googleApiClient;
    private String nameFromGoogle;
    private String usernameFromGoogle;
    private String emailFromGoogle;
    private String imageUrlFromGoogle;
    private String passwordForGoogle;
    private final int REQ_CODE_LOGIN_WITH_GOOGLE = 9001;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        insertItems();
        putBottomMenuVisible();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    validateInputs();
                }
                return false;
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
            }
        });

        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OtherSharedMethods.toggleShowPasswordStatus(new EditText[] {passwordEditText});
            }
        });

        signInWithGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

    private void validateInputs()
    {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        String[] inputs = {username, password};
        EditText[] editTexts = {usernameEditText,passwordEditText};

        if(validateUserInputs(inputs))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.empty_user_inputs), Toast.LENGTH_SHORT);
            for(EditText t : editTexts)
            {
                if(t.getText().toString().isEmpty())
                {
                    t.setError(getString(R.string.fill_this_field));
                }
            }

            if(username.isEmpty())
            {
                usernameEditText.requestFocus();
            }
            else
            {
                passwordEditText.requestFocus();
            }
        }
        else
        {
            login(username, password);
            disableLoginButtons();
            otherSharedMethods.hideKeyboard(passwordEditText);
        }
    }

    private void disableLoginButtons()
    {
        loginButton.setEnabled(false);
        loginButton.setBackgroundColor(getResources().getColor(R.color.colorSecondaryText));
        signInWithGoogleButton.setEnabled(false);
        signInWithGoogleButton.setBackgroundColor(getResources().getColor(R.color.colorSecondaryText));
    }

    private void login(String username, final String password)
    {
        if(networkManager.isInternetAvailable())
        {
            progressBar.setVisibility(View.VISIBLE);
            retrofitManager.login(username,password);
        }
        else
        {
            alertManager.showNoInternetConnectionAlert();
        }
    }

    private void signInWithGoogle()
    {
        if(networkManager.isInternetAvailable())
        {
            GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail().build();
            googleApiClient = new GoogleApiClient.Builder(context)
                    .enableAutoManage(getActivity(),this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
            progressBar.setVisibility(View.VISIBLE);
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent, REQ_CODE_LOGIN_WITH_GOOGLE);
            disableLoginButtons();
        }
        else
        {
            alertManager.showNoInternetConnectionAlert();
        }
    }

    private void enableLoginButtons()
    {
        loginButton.setEnabled(true);
        loginButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        signInWithGoogleButton.setEnabled(true);
        signInWithGoogleButton.setBackgroundColor(getResources().getColor(android.R.color.white));
    }

    @Override
    protected void insertItems()
    {
        super.insertItems();
        passwordForGoogle = Constants.EMPTY;
        loginButton = view.findViewById(R.id.loginButton);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        forgotPasswordTextView = view.findViewById(R.id.forgotPassWordTextView);
        showPasswordCheckBox = view.findViewById(R.id.showPasswordCheckBox);
        rememberMeSwitch = view.findViewById(R.id.rememberMeSwitch);
        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        retrofitManager = new RetrofitManager(this, this, this, this);
        signInWithGoogleButton = view.findViewById(R.id.signInWithGoogleButton);
    }

    @Override
    public void afterLoginSuccess(Response<APIResponse> response, String password)
    {
        handleRetrofitSuccessResult(response, password);
    }

    private void handleRetrofitSuccessResult(Response<APIResponse> response, String password)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }

        if(response.code() != 200)
        {
            enableLoginButtons();
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_responding), Toast.LENGTH_SHORT);
        }

        if(response.isSuccessful())
        {

            if(response.body().getSuccess())
            {
                enableLoginButtons();
                if(rememberMeSwitch.isChecked())
                {
                    preferencesManager.saveAutoLoginState(response.body().getSuccess());
                }

                imageManager.loadImageFromURL(response.body().getResults().get(Constants.FIRST_OBJECT).getThumbnail());

                user = new User();
                user.setId(response.body().getResults().get(Constants.FIRST_OBJECT).getId());
                user.setUsername(response.body().getResults().get(Constants.FIRST_OBJECT).getUsername());
                user.setFullName(response.body().getResults().get(Constants.FIRST_OBJECT).getFullname());
                user.setEmail(response.body().getResults().get(Constants.FIRST_OBJECT).getEmail());
                user.setPassword(password);
                user.setPremium(response.body().getResults().get(Constants.FIRST_OBJECT).getPremium() == 1);
                user.setMovies(response.body().getResults().get(Constants.FIRST_OBJECT).getMovies());
                user.setTvShows(response.body().getResults().get(Constants.FIRST_OBJECT).getTvshows());
                user.setRecent(response.body().getResults().get(Constants.FIRST_OBJECT).getRecentlywatched());
                user.setTotal(response.body().getResults().get(Constants.FIRST_OBJECT).getTotal());
                user.setThumbnail(Constants.EMPTY);

                preferencesManager.saveUserId(response.body().getResults().get(Constants.FIRST_OBJECT).getId());
                preferencesManager.saveUserPremium(response.body().getResults().get(Constants.FIRST_OBJECT).getPremium());
                preferencesManager.saveUserTotalWatchedVids(response.body().getVideosWatched());
                preferencesManager.saveLastInterTimeStamp(System.currentTimeMillis());
                alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(),Toast.LENGTH_SHORT);
            }
            else
            {
                switch (response.body().getError())
                {
                    case 403:
                        askIfUserWantsToLinkHisGoogleAccount();
                        break;
                    case 401:
                        registerUserWithGoogle();
                        break;
                    default:
                        enableLoginButtons();
                        alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(),Toast.LENGTH_SHORT);
                }
            }

        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_LONG);
        }
    }

    private void registerUserWithGoogle()
    {
        if(networkManager.isInternetAvailable())
        {
            progressBar.setVisibility(View.VISIBLE);
            disableLoginButtons();
            passwordForGoogle = OtherSharedMethods.randomPassword();
            retrofitManager.registerWithGoogle(nameFromGoogle, usernameFromGoogle, emailFromGoogle, passwordForGoogle, imageUrlFromGoogle);
        }
        else
        {
            alertManager.showNoInternetConnectionAlert();
        }
    }

    private void askIfUserWantsToLinkHisGoogleAccount()
    {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(activity);

        builder.setTitle(getString(R.string.user_already_registered))
                .setMessage(getString(R.string.aks_user_if_he_wants_to_link_his_google_account))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(networkManager.isInternetAvailable())
                        {
                            retrofitManager.updateGoogleAccountStatus(emailFromGoogle, true);
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
                        disableLoginButtons();
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    @Override
    public void afterLoginFailure(Throwable t)
    {
        showGenericMessageOnRetrofitFailed(t);
        enableLoginButtons();
    }

    private void showGenericMessageOnRetrofitFailed(Throwable t)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }
        if(t.toString().contains("SocketTimeoutException"))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_timeout),Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onImageDownloadCompleted(Bitmap bitmap)
    {
        super.onImageDownloadCompleted(bitmap);
        if(bitmap == null) {
            imageManager.loadImageFromURL(Constants.DEFAULT_IMAGE);
        }
        else
        {
            String imageFile = ImageManager.saveToInternalStorage(bitmap, this.activity);
            user.setThumbnail(imageFile);
            viewModel.insert(user);
            OtherSharedMethods.disconnectGoogleApiClient(googleApiClient, getActivity());
            navController.navigate(R.id.action_loginFragment_to_homeActivity);
            activity.finish();
        }
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
        enableLoginButtons();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE_LOGIN_WITH_GOOGLE)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result)
    {
        if(result.isSuccess())
        {
            Faker faker = new Faker(new Locale(OtherSharedMethods.getDefaultLocale()));
            GoogleSignInAccount account = result.getSignInAccount();
            nameFromGoogle = account.getDisplayName() != null ? account.getDisplayName() : String.format("%s, %s, %s", faker.name().firstName(), faker.name().nameWithMiddle(), faker.name().lastName());
            emailFromGoogle = account.getEmail();
            String[] temp = emailFromGoogle.split("@");
            usernameFromGoogle = temp[0];
            imageUrlFromGoogle = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : Constants.EMPTY;
            retrofitManager.loginWithGoogle(emailFromGoogle);
        }
        else
        {
            progressBar.setVisibility(View.INVISIBLE);
            enableLoginButtons();
            OtherSharedMethods.disconnectGoogleApiClient(googleApiClient, getActivity());
            alertManager.displayErrorAlertWithCustomTitleAndMsgAndIcon(getString(R.string.error), getString(R.string.google_sign_in_failed),android.R.drawable.ic_dialog_alert);
        }
    }


    @Override
    public void onPause()
    {
        super.onPause();
        OtherSharedMethods.disconnectGoogleApiClient(googleApiClient, getActivity());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        OtherSharedMethods.disconnectGoogleApiClient(googleApiClient, getActivity());
    }

    @Override
    public void afterLoginWithGoogleSuccess(Response<APIResponse> response)
    {
        if(response.isSuccessful())
        {
            if(response.body().getSuccess())
            {
                preferencesManager.saveGoogleAccountState(true);
            }
        }
        handleRetrofitSuccessResult(response, passwordForGoogle);
    }

    @Override
    public void afterLoginWithGoogleFailure(Throwable t)
    {
        enableLoginButtons();
        showGenericMessageOnRetrofitFailed(t);
    }

    @Override
    public void afterGoogleAccountUpdatedSuccessfully(Response<APIResponse> response)
    {
        if(response.code() != 200)
        {
            if(progressBar.getVisibility() == View.VISIBLE)
            {
                progressBar.setVisibility(View.INVISIBLE);
            }
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_responding), Toast.LENGTH_SHORT);
        }

        if(response.isSuccessful())
        {

            if(response.body().getSuccess())
            {
                retrofitManager.loginWithGoogle(emailFromGoogle);
            }
            else
            {
                alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(),Toast.LENGTH_SHORT);
            }

        }
        else
        {
            if(progressBar.getVisibility() == View.VISIBLE)
            {
                progressBar.setVisibility(View.INVISIBLE);
            }
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void afterGoogleAccountUpdatedFailed(Throwable t)
    {
        enableLoginButtons();
        showGenericMessageOnRetrofitFailed(t);
    }

    @Override
    public void afterRegisterWithGoogleSuccessfully(Response<APIResponse> response)
    {
        afterGoogleAccountUpdatedSuccessfully(response);
    }

    @Override
    public void afterRegisterWithGoogleFailure(Throwable t)
    {
        enableLoginButtons();
        showGenericMessageOnRetrofitFailed(t);
    }
}
