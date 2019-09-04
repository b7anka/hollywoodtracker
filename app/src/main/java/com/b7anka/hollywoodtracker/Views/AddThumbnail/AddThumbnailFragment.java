package com.b7anka.hollywoodtracker.Views.AddThumbnail;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import retrofit2.Response;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.b7anka.hollywoodtracker.Helpers.AlertManager;
import com.b7anka.hollywoodtracker.Helpers.NetworkManager;
import com.b7anka.hollywoodtracker.Helpers.OtherSharedMethods;
import com.b7anka.hollywoodtracker.Helpers.RetrofitManager;
import com.b7anka.hollywoodtracker.Helpers.ValidateUserInputs;
import com.b7anka.hollywoodtracker.Model.APIResponse;
import com.b7anka.hollywoodtracker.R;
import com.b7anka.hollywoodtracker.Views.Base.BaseCameraFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddThumbnailFragment extends BaseCameraFragment implements RetrofitManager.AfterThumbnailUploadListener
{

    private View view;
    private Activity activity;
    private Button uploadAThumbnailButton;
    private EditText originalTitleEditText;
    private ImageView thumbnailToUploadImageView;
    private NavController navController;
    private BottomNavigationView navigationView;
    private LinearLayout linearLayout;
    private Toolbar toolbar;
    private AlertManager alertManager;
    private ValidateUserInputs validator;
    private NetworkManager networkManager;
    private RetrofitManager retrofitManager;
    private ProgressBar progressBar;
    private OtherSharedMethods otherSharedMethods;

    public AddThumbnailFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_thumbnail, container, false);
        insertItems();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        putBottomMenuInVisible();
        putToolBarButtonsNotVisible();
        originalTitleEditText.requestFocus();
        uploadAThumbnailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(uploadAThumbnailButton.getText().equals(getString(R.string.select_thumbnail)))
                {
                    openGallery(thumbnailToUploadImageView);
                    uploadAThumbnailButton.setText(getString(R.string.send_button));
                }
                else
                {
                    validateUserInputs();
                }
            }
        });

        originalTitleEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    validateUserInputs();
                }
                return false;
            }
        });
    }

    private void validateUserInputs()
    {
        if(imageBase64 != null && !imageBase64.isEmpty())
        {
            String originalTitle = OtherSharedMethods.capitalizeString(originalTitleEditText.getText().toString().trim());
            String thumbnailBase64ToSend = String.format("data:image/jpg{base64,%s",imageBase64);

            if(originalTitle.isEmpty())
            {
                alertManager.displayToastWithCustomTextAndLenght(getString(R.string.empty_user_inputs), Toast.LENGTH_LONG);
                originalTitleEditText.setError(getString(R.string.fill_this_field));
                originalTitleEditText.requestFocus();
            }
            else
            {
                boolean isTitleValid = !validator.verifyTitleLength(originalTitle);
                if(!isTitleValid)
                {
                    alertManager.displayToastWithCustomTextAndLenght(getString(R.string.on_title_too_big), Toast.LENGTH_SHORT);
                    originalTitleEditText.setError(getString(R.string.on_title_too_big));
                }
                else
                {
                    uploadThumbnailToServer(originalTitle, thumbnailBase64ToSend);
                }
            }

        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.on_image_to_upload_empty), Toast.LENGTH_LONG);
            uploadAThumbnailButton.setText(getString(R.string.select_thumbnail));
        }
        otherSharedMethods.hideKeyboard(originalTitleEditText);
    }

    private void uploadThumbnailToServer(String title, String thumbnail)
    {
        if(networkManager.isInternetAvailable())
        {
            progressBar.setVisibility(View.VISIBLE);
            retrofitManager.uploadThumbnailToServer(title, thumbnail);
            otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {uploadAThumbnailButton});
        }
        else
        {
            alertManager.showNoInternetConnectionAlert();
        }
    }

    @Override
    protected void insertItems()
    {
        super.insertItems();
        this.activity = getActivity();
        this.uploadAThumbnailButton = view.findViewById(R.id.btnSelectThumbnail);
        this.originalTitleEditText = view.findViewById(R.id.etThumbnailToUploadTitle);
        this.thumbnailToUploadImageView = view.findViewById(R.id.ivThumbnailToUpload);
        this.toolbar = this.activity.findViewById(R.id.toolbar);
        this.navigationView = this.activity.findViewById(R.id.bottom_nav);
        this.linearLayout = this.toolbar.findViewById(R.id.toolbarLinearLayout);
        this.alertManager = new AlertManager(context);
        this.validator = new ValidateUserInputs();
        this.networkManager = new NetworkManager(context);
        this.retrofitManager = new RetrofitManager(this);
        this.progressBar = view.findViewById(R.id.progressBar);
        this.otherSharedMethods = new OtherSharedMethods(context);
        this.navController = Navigation.findNavController(activity,R.id.nav_host_fragment);
    }

    private void putBottomMenuInVisible()
    {
        if(this.navigationView.getVisibility() == View.VISIBLE)
        {
            this.navigationView.setVisibility(View.GONE);
        }
    }

    private void putToolBarButtonsNotVisible()
    {
        if(this.linearLayout != null)this.linearLayout.setVisibility(View.GONE);
    }

    @Override
    public void afterThumbnailUploadedSuccessfully(Response<APIResponse> response)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }

        if(response.code() != 200)
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_not_responding), Toast.LENGTH_SHORT);
        }

        if(response.isSuccessful())
        {
            alertManager.displayToastWithCustomTextAndLenght(response.body().getMsg(),Toast.LENGTH_SHORT);
            if(response.body().getSuccess())
            {
                imageBase64 = "";
                navController.navigateUp();
            }
        }
        else
        {
            alertManager.displayToastWithCustomTextAndLenght(response.message(), Toast.LENGTH_LONG);
        }
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {uploadAThumbnailButton});
    }

    @Override
    public void afterThumbnailUploadedFailed(Throwable t)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }
        if(t.toString().contains("SocketTimeoutException"))
        {
            alertManager.displayToastWithCustomTextAndLenght(getString(R.string.server_timeout),Toast.LENGTH_SHORT);
        }
        otherSharedMethods.toggleButtonsDisabledOrEnabled(new Button[] {uploadAThumbnailButton});
    }
}
