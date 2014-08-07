package com.votinginfoproject.VotingInformationProject.asynctasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.votinginfoproject.VotingInformationProject.models.Candidate;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Download an image from a given URL, then set the bitmap on a given ImageView.
 *
 * Based partially on this blog post on Android image downloading:
 * http://android-developers.blogspot.com/2010/07/multithreading-for-performance.html
 *
 * Created by kathrynkillebrew on 8/7/14.
 */
public class FetchImageQuery extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageView;
    HttpContext httpContext;
    HttpClient httpClient;
    Candidate candidate;

    public FetchImageQuery(Candidate candidate, ImageView imageView) {
        this.candidate = candidate;
        this.imageView = new WeakReference(imageView);
        this.httpContext = new BasicHttpContext();
        this.httpClient = new DefaultHttpClient();
    }

    protected Bitmap doInBackground(String... urls) {
        String photoUrl = urls[0];
        InputStream inputStream = null;
        HttpGet httpGet = null;

        try {
            httpGet = new HttpGet(photoUrl);
            HttpResponse response = httpClient.execute(httpGet, httpContext);
            int status = response.getStatusLine().getStatusCode();

            if (status != HttpStatus.SC_OK) {
                Log.e("FetchImageQuery", "Error " + status + " while fetching image at URL " + photoUrl);
                return null;
            }

            final BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
            if (entity != null) {
                inputStream = entity.getContent();
                return BitmapFactory.decodeStream(inputStream);
            }

        } catch (IOException e) {
            Log.e("FetchImageQuery", "IOException downloading image at URL " + photoUrl);
            e.printStackTrace();
        } catch (Exception ex) {
            Log.e("FetchImageQuery", "Exception downloading image at URL " + photoUrl);
            ex.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }  catch (IOException ex) {
                    Log.e("FetchImageQuery", "Error closing input stream");
                    ex.printStackTrace();
                }
            }

            if (httpGet != null) {
                try {
                    httpGet.abort();
                } catch (Exception ex) {
                    Log.e("FetchImageQuery", "Error aborting HTTP Get");
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            Log.e("FetchImageQuery:onPostExecute", "Task cancelled; not setting bitmap.");
            return;
        }

        candidate.photo = bitmap;

        if (imageView != null) {
            // get weakly referenced image view
            ImageView view = imageView.get();
            if (view != null) {
                Log.d("FetchImageQuery:onPostExecute", "Setting image bitmap.");
                view.setImageBitmap(bitmap);
            }
        } else {
            Log.e("FetchImageQuery:onPostExecute", "No ImageView found to give the bitmap.");
        }
    }
}
