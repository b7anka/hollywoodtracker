package com.b7anka.hollywoodtracker.Helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.b7anka.hollywoodtracker.R;

import androidx.appcompat.app.AlertDialog;

public class AlertManager
{
    private Context context;
    private SharedPreferencesManager preferencesManager;

    public AlertManager(Context context)
    {
        this.context = context;
        this.preferencesManager = new SharedPreferencesManager(this.context);
    }

    public void displayErrorAlertWithCustomTitleAndMsgAndIcon(String title, String msg, int icon)
    {
        final AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                })
                .setIcon(icon)
                .show();
    }

    public void displayToastWithCustomTextAndLenght(String msg, int length)
    {
        Toast.makeText(this.context,msg,length).show();
    }

    public void showNoInternetConnectionAlert()
    {
        displayErrorAlertWithCustomTitleAndMsgAndIcon(this.context.getString(R.string.error), this.context.getString(R.string.no_internet), android.R.drawable.ic_dialog_alert);
    }

    public void displayErrorAlertWithCustomTitleAndMsgIconAndOneListener(String title, String msg, int icon, DialogInterface.OnClickListener listener)
    {
        final AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, listener)
                .setIcon(icon)
                .show();
    }

    public void displayErrorAlertWithCustomTitleAndMsgIconAndTwoListeners(String title, String msg, int icon, DialogInterface.OnClickListener[] listeners)
    {
        final AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, listeners[0])
                .setNegativeButton(android.R.string.no, listeners[1])
                .setIcon(icon)
                .show();
    }
}
