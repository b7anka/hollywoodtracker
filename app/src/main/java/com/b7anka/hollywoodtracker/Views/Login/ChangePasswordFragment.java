package com.b7anka.hollywoodtracker.Views.Login;

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
import android.widget.TextView;
import android.widget.Toast;
import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.OtherSharedMethods;
import com.b7anka.hollywoodtracker.Helpers.RetrofitManager;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.Views.Base.BaseForgotPasswordFromLoginFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends BaseForgotPasswordFromLoginFragment implements RetrofitManager.AfterChangedPasswordFromLogin {

    private EditText newPasswordEditText;
    private EditText repeatNewPasswordEditText;
    private CheckBox showPasswordsCheckBox;
    private Button changePasswordButton;
    private RetrofitManager retrofitManager;
    private int userId;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_password, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        insertItems();

        showPasswordsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OtherSharedMethods.toggleShowPasswordStatus(new EditText[] {newPasswordEditText, repeatNewPasswordEditText});
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

        repeatNewPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    validateInputs();
                }
                return false;
            }
        });
    }

    private void validateInputs()
    {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String repeatNewPassword = repeatNewPasswordEditText.getText().toString().trim();
        EditText[] editTexts = {newPasswordEditText, repeatNewPasswordEditText};
        if(validator.checkForEmptyInputs(new String[] {newPassword, repeatNewPassword}))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.empty_user_inputs), Toast.LENGTH_LONG);
            for(EditText t : editTexts)
            {
                if(t.getText().toString().isEmpty())
                {
                    t.setError(getString(R.string.fill_this_field));
                    t.requestFocus();
                }
            }
        }
        else
        {
            if(!validator.verifyPassword(newPassword))
            {
                alertManager.displayErrorAlertWithCustomTitleAndMsgAndIcon(getString(R.string.error), getString(R.string.password_requirements),android.R.drawable.ic_dialog_alert);
                newPasswordEditText.setError(getString(R.string.password_requirements));
                newPasswordEditText.requestFocus();
            }
            else
            {
                if(!repeatNewPassword.equals(newPassword))
                {
                    alertManager.displayErrorAlertWithCustomTitleAndMsgAndIcon(getString(R.string.error), getString(R.string.passwords_dont_match),android.R.drawable.ic_dialog_alert);
                    repeatNewPasswordEditText.setError(getString(R.string.passwords_dont_match));
                    repeatNewPasswordEditText.requestFocus();
                }
                else
                {
                    changePassword(newPassword);
                    otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {changePasswordButton});
                    otherSharedMethods.hideKeyboard(repeatNewPasswordEditText);
                }
            }
        }
    }

    @Override
    protected void insertItems()
    {
        super.insertItems();
        newPasswordEditText = view.findViewById(R.id.passwordEditText);
        repeatNewPasswordEditText = view.findViewById(R.id.repeatPasswordEditText);
        showPasswordsCheckBox = view.findViewById(R.id.showPasswordsCheckBox);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);
        userId = getArguments().getInt(Constants.USER_ID);
        retrofitManager = new RetrofitManager(this);
    }

    private void changePassword(String password)
    {
        if(networkManager.isInternetAvailable())
        {
            progressBar.setVisibility(View.VISIBLE);
            retrofitManager.changePassword(password, userId);
        }
        else
        {
           alertManager.showNoInternetConnectionAlert();
        }
    }

    @Override
    public void afterPasswordChangedSuccessfully(Response<APIResponse> response)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.GONE);
        }

        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_reponding), Toast.LENGTH_LONG);
        }

        if(response.isSuccessful())
        {
            if(response.body().getSuccess())
            {
                navController.navigateUp();
            }
            alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(),Toast.LENGTH_LONG);
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(response.message(),Toast.LENGTH_LONG);
        }
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {changePasswordButton});
    }

    @Override
    public void afterPasswordChangedFailed(Throwable t)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.GONE);
        }
        if(t.toString().contains("SocketTimeoutException"))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_timeout),Toast.LENGTH_SHORT);

        }
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {changePasswordButton});
    }
}
