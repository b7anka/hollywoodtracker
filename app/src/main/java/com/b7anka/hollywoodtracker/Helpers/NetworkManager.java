package com.b7anka.hollywoodtracker.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager
{
    private Context context;

    public NetworkManager(Context context) {
        this.context = context;
    }

    public boolean isInternetAvailable()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo != null && activeNetworkInfo.isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
