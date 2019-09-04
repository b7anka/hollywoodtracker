package com.b7anka.hollywoodtracker.Views.Profile;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.OtherSharedMethods;
import com.b7anka.hollywoodtracker.Helpers.RetrofitManager;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import com.b7anka.hollywoodtracker.Model.User;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.ViewModels.OfflineChangesViewModel;
import com.b7anka.hollywoodtracker.ViewModels.UserViewModel;
import com.b7anka.hollywoodtracker.Views.Base.BaseNoBottomMenuFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFromProfileFragment extends BaseNoBottomMenuFragment implements RetrofitManager.UpdateDataListener
{
    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private EditText repeatNewPasswordEditText;
    private CheckBox showPasswordsCheckBox;
    private Button saveButton;
    private UserViewModel viewModel;
    private String password;
    private RetrofitManager retrofitManager;
    private OfflineChangesViewModel offlineChangesViewModel;
    private ProgressBar progressBar;
    private OtherSharedMethods otherSharedMethods;

    public ChangePasswordFromProfileFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_password_from_profile, container, false);
        insertItems();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUserInputs();
            }
        });

        repeatNewPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    validateUserInputs();
                }
                return false;
            }
        });

        showPasswordsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OtherSharedMethods.toggleShowPasswordStatus(new EditText[] {currentPasswordEditText,newPasswordEditText,repeatNewPasswordEditText});
            }
        });
    }

    private void validateUserInputs()
    {
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String repeatNewPassword = repeatNewPasswordEditText.getText().toString().trim();

        if(validator.checkForEmptyInputs(new String[] {currentPassword, newPassword, repeatNewPassword}))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.empty_user_inputs), Toast.LENGTH_LONG);
            EditText[] texts = {currentPasswordEditText, newPasswordEditText, repeatNewPasswordEditText};

            for(EditText et: texts)
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
            boolean isCurrentPasswordValid = currentPassword.equals(password);
            if(!isCurrentPasswordValid) currentPasswordEditText.setError(getString(R.string.previous_password_doesnt_match)); currentPasswordEditText.requestFocus();

            boolean isNewPasswordValid = validator.verifyPassword(newPassword);
            if(!isNewPasswordValid)newPasswordEditText.setError(getString(R.string.password_requirements)); newPasswordEditText.requestFocus();

            boolean areBothNewPasswordsTheSame = repeatNewPassword.equals(newPassword);
            if(!areBothNewPasswordsTheSame)repeatNewPasswordEditText.setError(getString(R.string.passwords_dont_match)); repeatNewPasswordEditText.requestFocus();

            boolean isNewPasswordTheSameAsCurrentPassword = newPassword.equals(password);
            if(isNewPasswordTheSameAsCurrentPassword)newPasswordEditText.setError(getString(R.string.previous_password_equals_current_password)); newPasswordEditText.requestFocus();

            boolean everythingIsValid = isCurrentPasswordValid && isNewPasswordValid && areBothNewPasswordsTheSame && !isNewPasswordTheSameAsCurrentPassword;

            if(everythingIsValid)
            {
                User currentUser = viewModel.getUser(preferencesManager.getUserId());
                User newUser = new User();
                newUser.setId(currentUser.getId());
                newUser.setUsername(currentUser.getUsername());
                newUser.setFullName(currentUser.getFullName());
                newUser.setEmail(currentUser.getEmail());
                newUser.setPassword(newPassword);
                newUser.setPremium(currentUser.isPremium());
                newUser.setMovies(currentUser.getMovies());
                newUser.setTvShows(currentUser.getTvShows());
                newUser.setRecent(currentUser.getRecent());
                newUser.setTotal(currentUser.getTotal());
                newUser.setThumbnail(currentUser.getThumbnail());
                viewModel.update(newUser);
                String content = newPassword + ";";
                updateUserData(preferencesManager.getUserId(), Constants.PROFILE,content);
                otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {saveButton});
                otherSharedMethods.hideKeyboard(repeatNewPasswordEditText);
            }
        }
    }

    private void updateUserData(int id, String type, String content)
    {
        if(networkManager.isInternetAvailable())
        {
            progressBar.setVisibility(View.VISIBLE);
            retrofitManager.updateUserData(id, preferencesManager.getUserId(), type, content);
        }
        else
        {
            createOfflineChange(id, type, content);
            navController.navigateUp();
        }
    }

    private void createOfflineChange(int id, String type, String content)
    {
        offlineChangesViewModel.insert(id, type, content);
        alertManager.displayToastWithCustomTextAndLenght(getString(R.string.updated_data_saved_offline), Toast.LENGTH_LONG);
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {saveButton});
    }

    @Override
    protected void insertItems()
    {
        super.insertItems();
        retrofitManager = new RetrofitManager(this);
        otherSharedMethods = new OtherSharedMethods(context);
        currentPasswordEditText = view.findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = view.findViewById(R.id.newPasswordProfileEditText);
        repeatNewPasswordEditText = view.findViewById(R.id.repeatNewPasswordProfileEditText);
        showPasswordsCheckBox = view.findViewById(R.id.showPasswordsCheckBox);
        saveButton = view.findViewById(R.id.changePasswordProfileButton);
        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        password = viewModel.getUser(preferencesManager.getUserId()).getPassword();
        offlineChangesViewModel = ViewModelProviders.of(this).get(OfflineChangesViewModel.class);
        progressBar = view.findViewById(R.id.progressBar);
        currentPasswordEditText.requestFocus();
    }

    @Override
    public void afterUpdatedDataSuccess(Response<APIResponse> response)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {saveButton});

        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_responding), Toast.LENGTH_SHORT);
        }

        if(response.isSuccessful())
        {
            alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(), Toast.LENGTH_LONG);
            if(response.body().getSuccess())
            {
                navController.navigateUp();
            }
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void afterUpdateDataFailed(Throwable t)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {saveButton});

        if(t.toString().contains("SocketTimeoutException"))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_timeout),Toast.LENGTH_SHORT);
        }
    }
}
