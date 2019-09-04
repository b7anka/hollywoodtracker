package com.b7anka.hollywoodtracker.Helpers;

import android.content.Context;
import android.text.method.PasswordTransformationMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.b7anka.hollywoodtracker.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Locale;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

public class OtherSharedMethods
{
    private Context context;
    private SharedPreferencesManager preferencesManager;

    public OtherSharedMethods(Context context) {
        this.context = context;
        this.preferencesManager = new SharedPreferencesManager(this.context);
    }

    public static void toggleShowPasswordStatus(EditText[] texts)
    {
        for(EditText et : texts)
        {
            if(et.getTransformationMethod() != null)
            {
                et.setTransformationMethod(null);
            }
            else
            {
                et.setTransformationMethod(new PasswordTransformationMethod());
            }
        }
    }

    public void toggleButtonsDisabledOrEnabled(Button[] buttons)
    {
        for(Button b : buttons)
        {
            if(b.isEnabled())
            {
                b.setEnabled(false);
                b.setBackgroundColor(context.getResources().getColor(R.color.colorSecondaryText));
            }
            else
            {
                b.setEnabled(true);
                b.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    public static String getDefaultLocale()
    {
        return Locale.getDefault().toString();
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') {
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public void hideKeyboard(EditText editText)
    {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void showKeyboard(EditText editText)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static int getRandomNumber()
    {
        Random random = new Random();
        return random.nextInt(10000+1);
    }

    public static long getTimeStamp()
    {
        long timestamp = System.currentTimeMillis();
        SharedPreferencesManager.saveTimeStamp(timestamp);
        return timestamp;
    }

    public static void disconnectGoogleApiClient(GoogleApiClient googleApiClient, FragmentActivity activity)
    {
        if(googleApiClient != null && googleApiClient.isConnected())
        {
            googleApiClient.stopAutoManage(activity);
            googleApiClient.disconnect();
        }
    }

    public static String randomPassword()
    {
        int length = Constants.MIN_PASSWORD_LENGTH;
        String symbol = "-/.^&*_!@%=+>)";
        String cap_letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String small_letter = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";


        String finalString = cap_letter + small_letter +
                numbers + symbol;

        Random random = new Random();

        char[] password = new char[length];

        for (int i = 0; i < length; i++)
        {
            password[i] =
                    finalString.charAt(random.nextInt(finalString.length()));

        }
        return new String(password);
    }

}
