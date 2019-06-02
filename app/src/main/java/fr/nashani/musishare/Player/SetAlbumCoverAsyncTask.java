package fr.nashani.musishare.Player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.cache.MemoryCache;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import fr.nashani.musishare.R;

public class SetAlbumCoverAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;

    public SetAlbumCoverAsyncTask (ImageView imageView){
        this.imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {

        try {
            return downloadFromURL(strings[0]);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        super.onPostExecute(bitmap);

        if(isCancelled()){
            bitmap = null;
        }

        ImageView imageView = imageViewReference.get();

        imageView.setImageBitmap(bitmap);

    }

    public Bitmap downloadFromURL(String url) throws IOException {
        URL urlImage = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) urlImage.openConnection();
        BufferedInputStream bufferedInputStream= new  BufferedInputStream(httpURLConnection.getInputStream());
        return BitmapFactory.decodeStream(bufferedInputStream);
    }
}
