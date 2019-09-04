package com.b7anka.hollywoodtracker.Views.Login;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Response;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.RetrofitManager;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.Views.Base.BaseForgotPasswordFromLoginFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends BaseForgotPasswordFromLoginFragment implements RetrofitManager.ForgotPasswordListener {

    private EditText emailEditText;
    private Button sendButton;
    private RetrofitManager retrofitManager;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        insertItems();
        //emailEditText.requestFocus();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

        emailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
    }

    private void validateInputs()
    {
        String email = emailEditText.getText().toString().trim().toLowerCase();

        if(validator.checkForEmptyInputs(new String[] {email}))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.empty_user_inputs), Toast.LENGTH_LONG);
            emailEditText.setError(getString(R.string.fill_this_field));
            emailEditText.requestFocus();
        }
        else
        {
           recoverPassword(email);
           otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {sendButton});
            otherSharedMethods.hideKeyboard(emailEditText);
        }
    }

    @Override
    protected void insertItems()
    {
        super.insertItems();
        emailEditText = view.findViewById(R.id.emailFpEditText);
        sendButton = view.findViewById(R.id.sendButton);
        retrofitManager = new RetrofitManager(this);
    }

    private void recoverPassword(String email)
    {
        if(networkManager.isInternetAvailable())
        {
            progressBar.setVisibility(View.VISIBLE);
            retrofitManager.recoverPassword(email);
        }
        else
        {
            alertManager.showNoInternetConnectionAlert();
        }
    }

    @Override
    public void afterForgotPasswordSuccess(Response<APIResponse> response)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.GONE);
        }

        if(response.code() != 200)
        {
            Toast.makeText(context, getString(R.string.server_not_reponding), Toast.LENGTH_LONG).show();
        }

        if(response.isSuccessful())
        {
            if(response.body().getSuccess())
            {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.CODE,response.body().getResults().get(0).getCode());
                bundle.putInt(Constants.USER_ID, response.body().getResults().get(0).getId());
                navController.navigate(R.id.action_forgotPasswordFragment_to_verifyCodeFragment, bundle);
                Toast.makeText(context, response.body().getMsg(),Toast.LENGTH_LONG).show();

            }
            else
            {
                Toast.makeText(context, response.body().getMsg(),Toast.LENGTH_LONG).show();
                emailEditText.setError(response.body().getMsg());
                emailEditText.setText(Constants.EMPTY);
            }
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_LONG);
        }
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {sendButton});
    }

    @Override
    public void afterForgotPasswordFailed(Throwable t)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.GONE);
        }

        if(t.toString().contains("SocketTimeoutException"))
        {
            Toast.makeText(context,getString(R.string.server_timeout),Toast.LENGTH_SHORT).show();
        }
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {sendButton});
    }
}
