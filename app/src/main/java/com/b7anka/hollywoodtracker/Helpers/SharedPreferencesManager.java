package com.b7anka.hollywoodtracker.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager
{
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private Context context;

    public SharedPreferencesManager(Context context)
    {
        this.context = context;
        this.preferences = this.context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        this.editor = preferences.edit();
    }

    public void savePremiumPurchasedDetails(String body)
    {
        editor.putString(Constants.PREMIUM_DETAILS,body);
        editor.commit();
    }

    public void saveAutoLoginState(boolean state)
    {
        editor.putBoolean(Constants.AUTO_LOGIN_KEY,state);
        editor.commit();
    }

    public void saveGoogleAccountState(boolean state)
    {
        editor.putBoolean(Constants.IS_GOOGLE_ACCOUNT,state);
        editor.commit();
    }

    public void saveShowsNeedUpdating(boolean state)
    {
        editor.putBoolean(Constants.SHOWS_NEED_UPDATING,state);
        editor.commit();
    }

    public void saveProfileNeedsUpdating(boolean state)
    {
        editor.putBoolean(Constants.PROFILE_NEEDS_UPDATING,state);
        editor.commit();
    }

    public void saveShowWasEdited(boolean state)
    {
        editor.putBoolean(Constants.SHOW_WAS_EDITED,state);
        editor.commit();
    }

    public String getPremiumPurchasedDetails()
    {
        return preferences.getString(Constants.PREMIUM_DETAILS,"");
    }

    public boolean getAutoLoginState()
    {
        return preferences.getBoolean(Constants.AUTO_LOGIN_KEY,false);
    }

    public boolean getGoogleAccountState()
    {
        return preferences.getBoolean(Constants.IS_GOOGLE_ACCOUNT,false);
    }

    public boolean getShowWasEdited()
    {
        return preferences.getBoolean(Constants.SHOW_WAS_EDITED,false);
    }

    public boolean getShowsNeedUpdating()
    {
        return preferences.getBoolean(Constants.SHOWS_NEED_UPDATING,false);
    }

    public boolean getProfileNeedsUpdating()
    {
        return preferences.getBoolean(Constants.PROFILE_NEEDS_UPDATING,false);
    }

    public boolean getUseFingerPrintState()
    {
        return preferences.getBoolean(Constants.USE_FINGERPRINT_SENSOR,false);
    }

    public void saveUseFingerPrintState(boolean state)
    {
        editor.putBoolean(Constants.USE_FINGERPRINT_SENSOR,state);
        editor.commit();
    }

    public static boolean wasHomeScreenChanged()
    {
        return preferences.getBoolean(Constants.HOME_SCREEN_CHANGED,false);
    }

    public static void saveWasHomeScreenChanged(boolean state)
    {
        editor.putBoolean(Constants.HOME_SCREEN_CHANGED,state);
        editor.commit();
    }

    public int getUserId()
    {
        return preferences.getInt(Constants.USER_ID,0);
    }

    public void saveUserId(int id)
    {
        editor.putInt(Constants.USER_ID,id);
        editor.commit();
    }

    public long getLastVideoWatchedTimeStamp()
    {
        return preferences.getLong(Constants.LAST_VIDEO_TIME_STAMP,0);
    }

    public void saveLastVideoWatchedTimeStamp(long value)
    {
        editor.putLong(Constants.LAST_VIDEO_TIME_STAMP,value);
        editor.commit();
    }

    public int getTotalWatchedVids()
    {
        return preferences.getInt(Constants.TOTAL_WATCHED_VIDS,0);
    }

    public void saveUserTotalWatchedVids(int value)
    {
        editor.putInt(Constants.TOTAL_WATCHED_VIDS,value);
        editor.commit();
    }

    public int getUserPremium()
    {
        return preferences.getInt(Constants.PREMIUM_USER,0);
    }

    public void saveUserPremium(int value)
    {
        editor.putInt(Constants.PREMIUM_USER,value);
        editor.commit();
    }

    public static long getTimeStamp()
    {
        return preferences.getLong(Constants.TIME_STAMP,0);
    }

    public static void saveTimeStamp(long value)
    {
        editor.putLong(Constants.TIME_STAMP,value);
        editor.commit();
    }

    public String getHomeScreenToUse()
    {
        return preferences.getString(Constants.HOME_SCREEN_TO_USE, Constants.MOVIES);
    }

    public void saveHomeScreenToUse(String home)
    {
        editor.putString(Constants.HOME_SCREEN_TO_USE,home);
        editor.commit();
    }

    public long getLastInterTimeStamp()
    {
        return preferences.getLong(Constants.LAST_INTERSTITIAL_AD_TIME_STAMP,0);
    }

    public void saveLastInterTimeStamp(long value)
    {
        editor.putLong(Constants.LAST_INTERSTITIAL_AD_TIME_STAMP,value);
        editor.commit();
    }

    public int getOfflineChangesCounter()
    {
        return preferences.getInt(Constants.OFFLINE_COUNTER,-1);
    }

    public void saveOfflineChangesCounter(int value)
    {
        editor.putInt(Constants.OFFLINE_COUNTER,value);
        editor.commit();
    }
}
