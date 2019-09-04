package com.b7anka.hollywoodtracker.Helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class PermissionsManager implements ActivityCompat.OnRequestPermissionsResultCallback
{

    public interface PermissionsListener
    {
        void onPermissionGranted();
        void onPermissionDenied();
    }

    private Activity activity;
    private PermissionsListener permissionsListener;
    private static final int MY_PERMISSIONS_REQUEST_FINGERPRINT = 1;
    private static final int MY_PERMISSIONS_REQUEST_BIOMETRIC = 0;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    private static final int MY_PERMISSIONS_REQUEST_NETWORK_STATE = 3;

    public PermissionsManager(Activity activity, PermissionsListener permissionsListener)
    {
        this.activity = activity;
        this.permissionsListener = permissionsListener;
    }

    public void askForFingerPrintPermission()
    {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.USE_FINGERPRINT},
                MY_PERMISSIONS_REQUEST_FINGERPRINT);
    }

    public void askForBiometricPermission()
    {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.USE_BIOMETRIC},
                MY_PERMISSIONS_REQUEST_BIOMETRIC);
    }

    public void askForCameraPermission()
    {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA},
                MY_PERMISSIONS_REQUEST_CAMERA);
    }

    public void askForNetworkStatePermission()
    {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                MY_PERMISSIONS_REQUEST_NETWORK_STATE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_BIOMETRIC:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(permissionsListener != null)permissionsListener.onPermissionGranted();
                }
                else
                {
                    if(permissionsListener != null)permissionsListener.onPermissionDenied();
                }
                break;
            case MY_PERMISSIONS_REQUEST_FINGERPRINT:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(permissionsListener != null)permissionsListener.onPermissionGranted();
                }
                else
                {
                    if(permissionsListener != null)permissionsListener.onPermissionDenied();
                }
                break;
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(permissionsListener != null)permissionsListener.onPermissionGranted();
                }
                else
                {
                    if(permissionsListener != null)permissionsListener.onPermissionDenied();
                }
                break;
            case MY_PERMISSIONS_REQUEST_NETWORK_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(permissionsListener != null)permissionsListener.onPermissionGranted();
                }
                else
                {
                    if(permissionsListener != null)permissionsListener.onPermissionDenied();
                }
                break;
        }
    }
}
