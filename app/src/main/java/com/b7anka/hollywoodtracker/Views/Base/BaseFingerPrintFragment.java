package com.b7anka.hollywoodtracker.Views.Base;

import android.content.DialogInterface;
import android.widget.Toast;
import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.FingerPrintManager;
import com.b7anka.hollywoodtracker.Helpers.PermissionsManager;
import com.b7anka.hollywoodtracker.R;

public class BaseFingerPrintFragment extends BaseNoBottomMenuFragment implements FingerPrintManager.FingerPrintListener, PermissionsManager.PermissionsListener
{
    protected FingerPrintManager fingerPrintManager;
    protected PermissionsManager permissionsManager;

    @Override
    protected void insertItems()
    {
        super.insertItems();
        fingerPrintManager = new FingerPrintManager(activity, this);
        permissionsManager = new PermissionsManager(activity, this);
    }

    @Override
    public void onAuthenticationSuccess()
    {
        alertManager.displayToastWithCustomTextAndLenght(getString(R.string.fingerprint_succes), Toast.LENGTH_SHORT);
    }

    @Override
    public void onAuthenticationFailed()
    {
        alertManager.displayToastWithCustomTextAndLenght(getString(R.string.fingerprint_error), Toast.LENGTH_LONG);
    }

    @Override
    public void onAuthenticationCancelled()
    {
        alertManager.displayToastWithCustomTextAndLenght(getString(R.string.fingerprint_cancel), Toast.LENGTH_SHORT);
        activity.finish();
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString)
    {
        alertManager.displayToastWithCustomTextAndLenght(errString.toString(), Toast.LENGTH_SHORT);
        activity.finish();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString)
    {
        alertManager.displayToastWithCustomTextAndLenght(helpString.toString(), Toast.LENGTH_LONG);
    }

    @Override
    public void onPermissionNotGranted(final String type)
    {
        DialogInterface.OnClickListener yesListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(type.equals(Constants.BIOMETRIC))
                {
                    permissionsManager.askForBiometricPermission();
                }
                else
                {
                    permissionsManager.askForFingerPrintPermission();
                }
            }
        };

        DialogInterface.OnClickListener noListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                activity.finish();
            }
        };

        alertManager.displayErrorAlertWithCustomTitleAndMsgIconAndTwoListeners(
                getString(R.string.fingerprint_permission_not_granted_title),
                getString(R.string.fingerprint_permission_not_granted),android.R.drawable.ic_dialog_alert,
                new DialogInterface.OnClickListener[] {yesListener, noListener});
    }

    @Override
    public void onPermissionGranted()
    {
        fingerPrintManager.initializeFingerPrintAuthentication();
    }

    @Override
    public void onPermissionDenied()
    {
        preferencesManager.saveUseFingerPrintState(false);

        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                activity.finish();
            }
        };

        alertManager.displayErrorAlertWithCustomTitleAndMsgIconAndOneListener(
                getString(R.string.fingerprint_permission_not_granted_title),
                getString(R.string.fingerprint_permission_still_not_granted),
                android.R.drawable.ic_dialog_alert,okListener);
    }
}
