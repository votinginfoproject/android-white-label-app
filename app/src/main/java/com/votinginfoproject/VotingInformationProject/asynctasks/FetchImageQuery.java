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
    final BitmapFactory.Options justGetBoundsOptions;


    public FetchImageQuery(Candidate candidate, ImageView imageView) {
        this.candidate = candidate;
        this.imageView = new WeakReference(imageView);
        this.httpContext = new BasicHttpContext();
        this.httpClient = new DefaultHttpClient();
        justGetBoundsOptions = new BitmapFactory.Options();
        justGetBoundsOptions.inJustDecodeBounds = true;
        justGetBoundsOptions.inPurgeable = true;
        justGetBoundsOptions.inInputShareable = true;
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

            // scale down image to fit candidate detail view thumbnail

            // first query for image size, to scale it
            BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
            if (entity != null) {
                inputStream = entity.getContent();
                BitmapFactory.decodeStream(inputStream, null, justGetBoundsOptions);
            }

            // Calculate inSampleSize
            justGetBoundsOptions.inSampleSize = calculateInSampleSize(justGetBoundsOptions);

            // Decode bitmap with inSampleSize set
            justGetBoundsOptions.inJustDecodeBounds = false;

            // re-query, now to actually get the image
            inputStream.close();
            httpGet = new HttpGet(photoUrl);
            response = httpClient.execute(httpGet, httpContext);
            status = response.getStatusLine().getStatusCode();

            if (status != HttpStatus.SC_OK) {
                Log.e("FetchImageQuery", "Error " + status + " while fetching image at URL " + photoUrl);
                return null;
            }

            entity = new BufferedHttpEntity(response.getEntity());
            if (entity != null) {
                inputStream = entity.getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, justGetBoundsOptions);
                return bitmap;
            }

        } catch (IOException e) {
            Log.e("FetchImageQuery", "IOException downloading image at URL " + photoUrl);
            e.printStackTrace();
            cancel(true);
        } catch (OutOfMemoryError err) {
            Log.e("FetchImageQuery", "Ran out of memory dowloading image at URL " + photoUrl);
            cancel(true);
        } catch (Exception ex) {
            Log.e("FetchImageQuery", "Exception downloading image at URL " + photoUrl);
            ex.printStackTrace();
            cancel(true);
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
    protected void onCancelled() {
        super.onCancelled();
        Log.e("FetchImageQuery", "Image fetch got cancelled!");
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            Log.e("FetchImageQuery:onPostExecute", "Task cancelled; not setting bitmap.");
            bitmap = null;
            return;
        }

        if (bitmap == null) {
            Log.e("FetchImageQuery:onPostExecute", "Bitmap is null; not setting bitmap.");
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

    /**
     * Calculate sample size for scaling down huge images to fit in the view, without overloading
     * app memory.  From here:
     * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     *
     * @param options BitmapFactory options used to pre-query for the image dimensions
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options) {
        // Size to scale to
        int reqWidth = 80;
        int reqHeight = 80;

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            Log.d("FetchImageQuery", "Scaling image down from " + height + " " + width);

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
