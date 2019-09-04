package com.b7anka.hollywoodtracker.Views.Login;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.Views.Base.BaseFingerPrintFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FingerPrintFragment extends BaseFingerPrintFragment {

    private ImageView fingerPrintImageView;
    private TextView fingerPrintInformationTextView;

    public FingerPrintFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_finger_print, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        insertItems();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            hideViews();
        }
        fingerPrintManager.initializeFingerPrintAuthentication();
    }

    @Override
    protected void insertItems()
    {
        super.insertItems();
        fingerPrintImageView = view.findViewById(R.id.fingerPrintImageView);
        fingerPrintInformationTextView = view.findViewById(R.id.fingerPrintTextView);
    }

    @Override
    public void onAuthenticationSuccess()
    {
        super.onAuthenticationSuccess();
        navController.navigate(R.id.action_fingerPrintFragment_to_homeActivity);
        activity.finish();
    }

    private void hideViews()
    {
        fingerPrintInformationTextView.setVisibility(View.GONE);
        fingerPrintImageView.setVisibility(View.GONE);
    }
}
