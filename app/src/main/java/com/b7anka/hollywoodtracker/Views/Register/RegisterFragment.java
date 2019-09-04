package com.b7anka.hollywoodtracker.Views.Register;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.OtherSharedMethods;
import com.b7anka.hollywoodtracker.Helpers.RetrofitManager;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.Views.Base.BaseLoginAndRegisterFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends BaseLoginAndRegisterFragment implements RetrofitManager.RegisterListener
{

    private ImageView avatarImageView;
    private EditText fullNameEditText;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private CheckBox showPasswordsRegisterCheckBox;
    private Button registerButton;
    private RetrofitManager retrofitManager;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        insertItems();
        putBottomMenuInVisible();
        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlertForUserToChooseWhereToFetchImage(avatarImageView);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

        showPasswordsRegisterCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OtherSharedMethods.toggleShowPasswordStatus(new EditText[] {passwordEditText, repeatPasswordEditText});
            }
        });

        repeatPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
       String fullName = OtherSharedMethods.capitalizeString(fullNameEditText.getText().toString().trim());
       String username = usernameEditText.getText().toString().trim();
       String email = emailEditText.getText().toString().toLowerCase().trim();
       String password = passwordEditText.getText().toString().trim();
       String repeatPassword = repeatPasswordEditText.getText().toString().trim();

       if(validateUserInputs(new String[] {fullName,username,email,password,repeatPassword}))
       {
           alertManager.displayToastWithCustomTextAndLenght(getString(R.string.empty_user_inputs), Toast.LENGTH_LONG);
           for(EditText t : new EditText[] {fullNameEditText,usernameEditText,emailEditText,
                   passwordEditText,repeatPasswordEditText})
           {
               if(t.getText().toString().trim().isEmpty())
               {
                   t.setError(getString(R.string.fill_this_field));
                   t.requestFocus();
               }
           }
       }
       else
       {
           //Full name validation

            boolean isFullNameLengthValid = !validator.verifyFullNameLength(fullName);
            if(!isFullNameLengthValid)fullNameEditText.setError(getString(R.string.fullname_toobig));
            boolean isFullNameSizeValid = validator.verifyFullNameSize(fullName);
            if(!isFullNameSizeValid)fullNameEditText.setError(getString(R.string.fullname_size));
            boolean isFullNameRegexValid = validator.verifyFullNameRegex(fullName);
            if(!isFullNameRegexValid)fullNameEditText.setError(getString(R.string.only_letter_and_spaces_on_name));
            boolean isFullNameValid = isFullNameLengthValid && isFullNameSizeValid && isFullNameRegexValid;

            //Username validation

           boolean isUsernameLengthValid = !validator.verifyUsernameLength(username);
           if(!isUsernameLengthValid)usernameEditText.setError(getString(R.string.username_toobig));
           boolean isUsernameRegexValid = validator.verifyUsernameContent(username);
           if(!isUsernameRegexValid)usernameEditText.setError(getString(R.string.username_requirements));
           boolean isUsernameValid = isUsernameLengthValid && isUsernameRegexValid;

           //Email validation

           boolean isEmailLengthValid = !validator.verifyEmailAddressLength(email);
           if(!isEmailLengthValid)emailEditText.setError(getString(R.string.email_toobig));
           boolean isEmailAValidEmail = validator.verifyValidEmailAddress(email);
           if(!isEmailAValidEmail)emailEditText.setError(getString(R.string.email_notValid));
           boolean isEmailValid = isEmailLengthValid && isEmailAValidEmail;

           //Password validation

            boolean isPasswordRegexValid = validator.verifyPassword(password);
            if(!isPasswordRegexValid)passwordEditText.setError(getString(R.string.password_requirements));
            boolean areBothPasswordsTheSame = repeatPassword.equals(password);
            if(!areBothPasswordsTheSame)repeatPasswordEditText.setError(getString(R.string.passwords_dont_match));
            boolean isPasswordValid = isPasswordRegexValid && areBothPasswordsTheSame;

            boolean isEverythingValid = isFullNameValid && isUsernameValid && isEmailValid && isPasswordValid;

            if(isEverythingValid)
            {
                otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {registerButton});
                String base64StringThumbnail = this.imageBase64 != null && !this.imageBase64.isEmpty() ? "data:image/jpg{base64," + imageBase64 : Constants.EMPTY;
                register(fullName,username,email,password,base64StringThumbnail);
                imageBase64 = Constants.EMPTY;
                otherSharedMethods.hideKeyboard(repeatPasswordEditText);
            }
       }
    }

    @Override
    protected void insertItems()
    {
        super.insertItems();
        avatarImageView = view.findViewById(R.id.avatarImageView);
        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        repeatPasswordEditText = view.findViewById(R.id.repeatPasswordEditText);
        registerButton = view.findViewById(R.id.registerButton);
        showPasswordsRegisterCheckBox = view.findViewById(R.id.showPasswordsRegisterCheckBox);
        retrofitManager = new RetrofitManager(this);
    }

    private void register(String fullName, String user, String email, String pass, String thumbnail)
    {
        if(networkManager.isInternetAvailable())
        {
            progressBar.setVisibility(View.VISIBLE);
            retrofitManager.register(fullName,user,email,pass,thumbnail);
        }
        else
        {
            alertManager.showNoInternetConnectionAlert();
        }
    }

    @Override
    public void afterRegisterSuccessfully(Response<APIResponse> response)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }

        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_reponding), Toast.LENGTH_LONG);
        }

        if(response.isSuccessful())
        {
            alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(),Toast.LENGTH_LONG);

            if(response.body().getSuccess())
            {
                navController.navigateUp();
            }
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_SHORT);
        }
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {registerButton});
    }

    @Override
    public void afterRegisterFailure(Throwable t)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }

        if(t.toString().contains("SocketTimeoutException"))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_timeout),Toast.LENGTH_SHORT);
        }
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {registerButton});
    }
}
