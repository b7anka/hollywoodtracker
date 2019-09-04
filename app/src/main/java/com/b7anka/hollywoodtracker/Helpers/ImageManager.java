package com.b7anka.hollywoodtracker.Helpers;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import com.b7anka.hollywoodtracker.Model.Show;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ImageManager
{
    public interface ImageDownloaderListener
    {
       void onImageDownloadCompleted(Bitmap bitmap);
    }

    public interface ShowImageDownloaderListener
    {
        void onImageDownloadCompleted(Bitmap bitmap, Show show);
    }

    private ImageDownloaderListener imageDownloaderListener;
    private ShowImageDownloaderListener showImageDownloaderListener;

    public ImageManager(ImageDownloaderListener imageDownloaderListener) {
        this.imageDownloaderListener = imageDownloaderListener;
    }

    public ImageManager(ShowImageDownloaderListener showImageDownloaderListener) {
        this.showImageDownloaderListener = showImageDownloaderListener;
    }

    public static String encoder64(Bitmap image)
    {
        Bitmap imageCopy = image;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageCopy.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] bytes = stream.toByteArray();
        String imageString = Base64.encodeToString(bytes,Base64.DEFAULT);
        return imageString;
    }

    public static Bitmap decoder64(String image)
    {
        byte[] decodeByte = Base64.decode(image,0);
        return BitmapFactory.decodeByteArray(decodeByte,0,decodeByte.length);
    }

    public static String saveToInternalStorage(Bitmap bitmapImage, Context context){
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir(Constants.IMAGES_DIR, Context.MODE_PRIVATE);
        String randomName = UUID.randomUUID().toString() + ".jpg";
        File myPath = new File(directory,randomName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return String.format("%s/%s", directory.getAbsolutePath(),randomName);
    }

    public static Bitmap loadImageFromStorage(String path)
    {
        Bitmap b = null;
        try {
            File f = new File(path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return b;
    }

    public static void deleteImageFromStorage(String path)
    {
        File f = new File(path);
        f.delete();
    }

    public void loadImageFromURL(String url)
    {
        new DownloadImageFromNetwork().execute(url);
    }

    public void loadImageFromURL(String url, Show show)
    {
        new DownloadImageForShowFromNetwork(show).execute(url);
    }

    private class DownloadImageFromNetwork extends AsyncTask<String, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... strings)
        {
           return imageFromURL(strings);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            if(imageDownloaderListener != null)imageDownloaderListener.onImageDownloadCompleted(bitmap);
        }
    }

    private class DownloadImageForShowFromNetwork extends DownloadImageFromNetwork
    {
        Show show;

        public DownloadImageForShowFromNetwork(Show show)
        {
            this.show = show;
        }

        @Override
        protected Bitmap doInBackground(String... strings)
        {
            return imageFromURL(strings);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            if(showImageDownloaderListener != null)showImageDownloaderListener.onImageDownloadCompleted(bitmap, show);
        }
    }


    private Bitmap imageFromURL(String... strings)
    {
        Bitmap bmp = null;
        try {
            InputStream in = new java.net.URL(strings[0]).openStream();
            bmp = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }
}
