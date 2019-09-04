package com.b7anka.hollywoodtracker.Views.Login;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.Views.Base.BaseForgotPasswordFromLoginFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyCodeFragment extends BaseForgotPasswordFromLoginFragment {

    private EditText verifyCodeEditText;
    private Button verifyButton;
    private String code;
    private int userId;

    public VerifyCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_verify_code, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        insertItems();

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

        verifyCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        String code = verifyCodeEditText.getText().toString().trim().toUpperCase();

        if(validator.checkForEmptyInputs(new String[] {code}))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.empty_user_inputs), Toast.LENGTH_SHORT);
            verifyCodeEditText.setError(getString(R.string.fill_this_field));
            verifyCodeEditText.requestFocus();
        }
        else
        {
            if(!code.equals(this.code))
            {
                alertManager.displayToastWithCustomTextAndLenght(getString(R.string.entered_code_wrong), Toast.LENGTH_SHORT);
                verifyCodeEditText.setError(getString(R.string.entered_code_wrong));
                verifyCodeEditText.setText(Constants.EMPTY);
                verifyCodeEditText.requestFocus();
            }
            else
            {
                otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {verifyButton});
                otherSharedMethods.hideKeyboard(verifyCodeEditText);
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.USER_ID,this.userId);
                navController.navigate(R.id.action_verifyCodeFragment_to_changePasswordFragment, bundle);
            }
        }
    }

    @Override
    protected void insertItems() {
        super.insertItems();
        verifyCodeEditText = view.findViewById(R.id.verifyCodeEditText);
        verifyButton = view.findViewById(R.id.verifyButton);
        code = getArguments().getString(Constants.CODE);
        userId = getArguments().getInt(Constants.USER_ID);
    }
}
