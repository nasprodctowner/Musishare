package fr.nashani.musishare.Player;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The type Set album cover async task.
 */
public class SetAlbumCoverAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;

    /**
     * Instantiates a new SetAlbumCoverAsyncTask
     *
     * @param imageView the image view
     */
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


    /*
    Set l'imageBitmap avec le bitmap téléchargé
     */
    @Override
    protected void onPostExecute(Bitmap bitmap){
        super.onPostExecute(bitmap);

        if(isCancelled()){
            bitmap = null;
        }

        ImageView imageView = imageViewReference.get();

        imageView.setImageBitmap(bitmap);

    }

    /**
     * Download bitmap from url
     *
     * @param url
     * @return the bitmap
     * @throws IOException the io exception
     */
    public Bitmap downloadFromURL(String url) throws IOException {
        URL urlImage = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) urlImage.openConnection();
        BufferedInputStream bufferedInputStream= new  BufferedInputStream(httpURLConnection.getInputStream());
        return BitmapFactory.decodeStream(bufferedInputStream);
    }
}
