package com.b7anka.hollywoodtracker.Views.Base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.ImageView;
import com.b7anka.hollywoodtracker.Helpers.ImageManager;
import com.b7anka.hollywoodtracker.R;
import java.io.IOException;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class BaseCameraFragment extends Fragment implements ImageManager.ImageDownloaderListener
{
    public interface DialogCancelListener
    {
        void onDialogCancelled();
    }

    private ImageView imageView;
    protected Bitmap bitmap;
    protected String imageBase64;
    protected Context context;
    protected ImageManager imageManager;
    protected DialogCancelListener dialogCancelListener;
    private final int SELECT_IMAGE = 1;
    private final int CAMERA_PIC_REQUEST = 2;

    protected void insertItems()
    {
        this.context = getActivity();
        this.imageManager = new ImageManager(this);
    }

    protected void displayAlertForUserToChooseWhereToFetchImage(final ImageView imageView)
    {
        this.imageView = imageView;

        final AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(context);

        builder.setTitle(this.context.getString(R.string.camera_or_gallery_title))
                .setMessage(this.context.getString(R.string.camera_or_gallery))
                .setCancelable(false)
                .setPositiveButton(this.context.getString(R.string.camera), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        openCamera();
                    }
                })
                .setNegativeButton(this.context.getString(R.string.gallery), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openGallery(imageView);
                    }
                })
                .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dialogCancelListener != null)dialogCancelListener.onDialogCancelled();
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    protected void openCamera()
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, this.CAMERA_PIC_REQUEST);
    }

    protected void openGallery(ImageView imageView)
    {
        this.imageView = imageView;
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i, this.SELECT_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK)
        {
            Bitmap image;
            switch(requestCode)
            {
                case SELECT_IMAGE:
                    if (data != null)
                    {
                        try
                        {
                            image = MediaStore.Images.Media.getBitmap(this.context.getContentResolver(), data.getData());
                            this.imageBase64 = ImageManager.encoder64(image);
                            this.imageView.setImageBitmap(image);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
                case CAMERA_PIC_REQUEST:
                    if(data != null)
                    {
                        image = (Bitmap) data.getExtras().get("data");
                        this.imageBase64 = ImageManager.encoder64(image);
                        this.imageView.setImageBitmap(image);
                    }
                    break;
            }
        }
    }

    @Override
    public void onImageDownloadCompleted(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }
}
