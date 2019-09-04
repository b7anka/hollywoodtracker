package com.b7anka.hollywoodtracker.Views.About;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import retrofit2.Response;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.OtherSharedMethods;
import com.b7anka.hollywoodtracker.Helpers.RetrofitManager;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.ViewModels.OfflineChangesViewModel;
import com.b7anka.hollywoodtracker.Views.Base.BaseNoBottomMenuFragment;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends BaseNoBottomMenuFragment implements RetrofitManager.ReportBugsListener
{
    private Button reportBugsButton;
    private Button rateMyAppButton;
    private Button uploadAThumbnailButton;
    private ProgressBar progressBar;
    private RetrofitManager retrofitManager;
    private OfflineChangesViewModel offlineChangesViewModel;
    private int userId;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        insertItems();

        reportBugsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(networkManager.isInternetAvailable() || userId != 0)
                {
                    openDialogToInputBugsToReport();
                }
                else
                {
                    alertManager.showNoInternetConnectionAlert();
                }
            }
        });

        rateMyAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppRating();
            }
        });

        uploadAThumbnailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_nav_about_to_addThumbnailFragment);
            }
        });
    }

    @Override
    protected void insertItems()
    {
        super.insertItems();
        reportBugsButton = view.findViewById(R.id.reportBugsButton);
        rateMyAppButton = view.findViewById(R.id.rateMyAppButton);
        uploadAThumbnailButton = view.findViewById(R.id.uploadAThumbnailButton);
        userId = getArguments() != null && getArguments().containsKey(Constants.USER_ID) ? getArguments().getInt(Constants.USER_ID) : 0;
        progressBar = view.findViewById(R.id.progressBar);
        retrofitManager = new RetrofitManager(this);
        offlineChangesViewModel = ViewModelProviders.of(this).get(OfflineChangesViewModel.class);
    }

    private void openAppRating()
    {
        if(networkManager.isInternetAvailable())
        {
            String appId = context.getPackageName();
            Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Constants.MARKET_URI + appId));
            boolean marketFound = false;

            final List<ResolveInfo> otherApps = context.getPackageManager()
                    .queryIntentActivities(rateIntent, 0);
            for (ResolveInfo otherApp: otherApps) {
                if (otherApp.activityInfo.applicationInfo.packageName
                        .equals(Constants.ANDROID_VENDING)) {

                    ActivityInfo otherAppActivity = otherApp.activityInfo;
                    ComponentName componentName = new ComponentName(
                            otherAppActivity.applicationInfo.packageName,
                            otherAppActivity.name
                    );
                    rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    rateIntent.setComponent(componentName);
                    context.startActivity(rateIntent);
                    marketFound = true;
                    break;
                }
            }
            if (!marketFound) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(Constants.PLAY_STORE_BASE_URL+appId));
                context.startActivity(webIntent);
            }
        }
        else
        {
            alertManager.showNoInternetConnectionAlert();
        }
    }

    private void openDialogToInputBugsToReport()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle(getString(R.string.report_bugs));
        builder.setView(inflater.inflate(R.layout.report_bugs_dialog, null));
        builder.setPositiveButton(getString(R.string.send_button),null);
        builder.setNegativeButton(getString(R.string.btn_cancel), null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        final Dialog dl = dialog;
        final EditText etBugTitle = dl.findViewById(R.id.etBugTitle);
        final EditText etBugSenderName = dl.findViewById(R.id.etBugSenderName);
        final EditText etBugSenderEmail = dl.findViewById(R.id.etBugSenderEmail);
        final EditText etBugBody = dl.findViewById(R.id.etBugBody);

        final  EditText inputs[] = {etBugTitle,etBugSenderName,etBugSenderEmail,etBugBody};
        etBugTitle.requestFocus();


        etBugBody.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                if(i == EditorInfo.IME_ACTION_DONE)
                {
                    verifySendBugsReportInputs(inputs,dialog);
                }
                return false;
            }

        });

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                verifySendBugsReportInputs(inputs,dialog);
            }
        });
    }

    private void verifySendBugsReportInputs(EditText inputs[], Dialog dl)
    {
        String bugTitle = OtherSharedMethods.capitalizeString(inputs[0].getText().toString().trim());
        String bugSenderName = OtherSharedMethods.capitalizeString(inputs[1].getText().toString().trim());
        String bugSenderEmail = inputs[2].getText().toString().toLowerCase().trim();
        String bugBody = inputs[3].getText().toString().trim();

        if(bugTitle.isEmpty() || bugSenderName.isEmpty() || bugSenderEmail.isEmpty() || bugBody.isEmpty())
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.empty_user_inputs),Toast.LENGTH_LONG);

            for(EditText et : inputs)
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
            boolean isTitleValid = !validator.verifyTitleLength(bugTitle);
            if(!isTitleValid)inputs[0].setError(getString(R.string.on_title_too_big));
            boolean isFullNameSizeValid = validator.verifyFullNameSize(bugSenderName);
            if(!isFullNameSizeValid)inputs[1].setError(getString(R.string.fullname_size));
            boolean isFullNameLengthValid = !validator.verifyFullNameLength(bugSenderName);
            if(!isFullNameLengthValid)inputs[1].setError(getString(R.string.fullname_toobig));
            boolean isFullNameRegexValid = validator.verifyFullNameRegex(bugSenderName);
            if(!isFullNameRegexValid)inputs[1].setError(getString(R.string.only_letter_and_spaces_on_name));
            boolean isFullNameValid = isFullNameSizeValid && isFullNameLengthValid && isFullNameRegexValid;
            boolean isEmailLengthValid = !validator.verifyEmailAddressLength(bugSenderEmail);
            if(!isEmailLengthValid)inputs[2].setError(getString(R.string.email_toobig));
            boolean isAValidEmail = validator.verifyValidEmailAddress(bugSenderEmail);
            if(!isAValidEmail)inputs[2].setError(getString(R.string.email_notValid));
            boolean isEmailValid = isEmailLengthValid && isAValidEmail;
            boolean everythingIsValid = isTitleValid && isFullNameValid && isEmailValid;

            if(everythingIsValid)
            {
                String content[] = {bugTitle,bugSenderName,bugSenderEmail,bugBody};
                sendBugReport(content);
                dl.dismiss();
                otherSharedMethods.hideKeyboard(inputs[3]);
            }
        }
    }

    private void sendBugReport(String[] content)
    {
        if(networkManager.isInternetAvailable())
        {
            progressBar.setVisibility(View.VISIBLE);
            otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {reportBugsButton});
            retrofitManager.reportBugs(content);
        }
        else
        {
            if(userId != 0)
            {
                String contentString = String.format("%s;%s;%s;%s",content[0], content[1], content[2], content[3]);
                offlineChangesViewModel.insert(userId, Constants.BUG_REPORT, contentString);
                alertManager.displayToastWithCustomTextAndLenght(getString(R.string.bug_report_saved), Toast.LENGTH_SHORT);
                navController.navigateUp();
            }
            else
            {
                alertManager.showNoInternetConnectionAlert();
            }
        }
    }

    @Override
    public void afterBugReportSentSuccessfully(Response<APIResponse> response)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.GONE);
        }
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {reportBugsButton});

        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_reponding),Toast.LENGTH_LONG);
        }

        alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(),Toast.LENGTH_LONG);

        if(response.isSuccessful())
        {
            navController.navigateUp();
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void afterBugReportSentFailure(Throwable t)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.GONE);
        }
        if(t.toString().contains("SocketTimeoutException"))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_timeout),Toast.LENGTH_SHORT);
        }
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {reportBugsButton});
    }
}
