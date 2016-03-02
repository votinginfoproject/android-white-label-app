package com.votinginfoproject.VotingInformationProject.asynctasks;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.adapters.DirectionsAdapter;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Bounds;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Leg;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Response;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Asynchronously query the Google Directions API for directions from user's address to a polling
 * location, via a given mode.
 * <p/>
 * <p/>
 * Created by kathrynkillebrew on 7/31/14.
 */
public class DirectionsQuery extends AsyncTask<String, String, Response> {

    String homeCoordinates;
    String destinationCoordinates;
    String api_key;
    String addressKey;
    HttpContext httpContext;
    HttpClient httpClient;
    Resources resources;
    Context mContext;
    PolylineCallBackListener listener;

    private final WeakReference<ListView> directionsListViewReference;
    private final WeakReference<TextView> errorViewReference;

    private static HashMap<String, Response> directionsCache = new HashMap(4);

    public interface PolylineCallBackListener {
        /**
         * Callback for returning encoded overview polyline and its bounds for map display
         */
        public void polylineCallback(String polyline, Bounds bounds);
    }

    public DirectionsQuery(Context context, ListView listView, TextView errorView, String originCoordinates,
                           String destinationCoordinates, PolylineCallBackListener listener) {
        super();

        mContext = context;

        resources = mContext.getResources();
        this.homeCoordinates = originCoordinates;
        this.destinationCoordinates = destinationCoordinates;
        this.api_key = context.getResources().getString(R.string.google_api_browser_key);

        this.directionsListViewReference = new WeakReference(listView);
        this.errorViewReference = new WeakReference(errorView);

        this.httpContext = new BasicHttpContext();
        this.httpClient = new DefaultHttpClient();
        this.listener = listener;

        // build cache key for this instance
        StringBuilder cacheKeyBuilder = new StringBuilder(originCoordinates);
        cacheKeyBuilder.append("-");
        cacheKeyBuilder.append(destinationCoordinates);
        cacheKeyBuilder.append("-");

        this.addressKey = cacheKeyBuilder.toString();
    }

    @Override
    protected Response doInBackground(String... travelModes) {

        // first check if we already have this one cached
        String cacheKey = addressKey + travelModes[0];
        Response cachedResponse = directionsCache.get(cacheKey);
        if (cachedResponse != null) {
            Log.d("DirectionsQuery", "Have response in cache.");
            return cachedResponse;
        }

        // Response is not cached; go query for directions.
        // Uri.Builder is not safe for concurrent use, so just build a string
        StringBuilder uri = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?origin=");
        uri.append(homeCoordinates);
        uri.append("&destination=");
        uri.append(destinationCoordinates);
        uri.append("&mode=");
        uri.append(travelModes[0]);

        if (travelModes[0].equals("transit")) {
            // must specify departure or arrival time
            uri.append("&departure_time=");
            uri.append(String.valueOf(System.currentTimeMillis() / 1000)); // now, as seconds since epoch
        }

        uri.append("&key=");
        uri.append(api_key);
        Log.d("DirectionsQuery", uri.toString());

        InputStream inputStream = null;
        HttpGet httpGet = null;

        try {
            httpGet = new HttpGet(uri.toString());
            HttpResponse response = httpClient.execute(httpGet, httpContext);
            int status = response.getStatusLine().getStatusCode();
            inputStream = response.getEntity().getContent();
            BufferedReader ir = new BufferedReader(new InputStreamReader(inputStream));

            Log.d("DirectionsQuery", "GOT RESPONSE STATUS: " + status);

            Gson gson = new GsonBuilder().create();
            if (status == HttpStatus.SC_OK) {
                Response gsonObj = gson.fromJson(ir, Response.class);
                ir.close();
                inputStream.close();

                // add this response to the cache, if it's good
                if (gsonObj != null && gsonObj.status.equals("OK")) {
                    directionsCache.put(cacheKey, gsonObj);
                }

                return gsonObj;
            } else {
                // error
                Log.e("DirectionsQuery", "Error with response: " + ir.readLine());
                ir.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Log.e("DirectionsQuery", "Error closing input stream");
                    ex.printStackTrace();
                }
            }

            if (httpGet != null) {
                try {
                    httpGet.abort();
                } catch (Exception ex) {
                    Log.e("DirectionsQuery", "Error aborting HTTP Get");
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Show loading message while fetching response.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        ListView directionsListView = directionsListViewReference.get();
        TextView errorView = errorViewReference.get();

        if (directionsListView != null && errorView != null) {
            errorView.setText(resources.getString(R.string.directions_loading_message));
            errorView.setVisibility(View.VISIBLE);
            directionsListView.setVisibility(View.GONE);
        } else {
            Log.e("DirectionsQuery:onPreExecute", "Directions ListView and/or error TextView references are null.");
        }
    }

    @Override
    protected void onPostExecute(Response response) {
        if (response == null) {
            Log.e("DirectionsQuery", "Did not get directions query response");
            showError();
            return;
        }

        if (!response.status.equals("OK")) {
            Log.e("DirectionsQuery", "Directions query response status is: " + response.status);
            showError();
            return;
        }

        // did not query for alternate routes or provide waypoints, so should get one route with one leg
        Route foundRoute = response.routes.get(0);

        // get overview polyline to display on map
        String encodedPolyline = foundRoute.overview_polyline.points;
        Bounds polylineBounds = foundRoute.bounds;

        if (encodedPolyline != null && !encodedPolyline.isEmpty()) {
            listener.polylineCallback(encodedPolyline, polylineBounds);
        }

        Leg leg = foundRoute.legs.get(0);
        DirectionsAdapter listAdapter = new DirectionsAdapter(mContext, leg.steps);
        ListView directionsListView = directionsListViewReference.get();
        TextView errorView = errorViewReference.get();

        if (directionsListView != null && errorView != null) {
            errorView.setVisibility(View.GONE);
            directionsListView.setVisibility(View.VISIBLE);
            directionsListView.setAdapter(listAdapter);
        } else {
            Log.e("DirectionsQuery:onPostExecute", "Directions ListView and/or error TextView references are null.");
        }
    }

    private void showError() {
        // show error message
        ListView directionsListView = directionsListViewReference.get();
        TextView errorView = errorViewReference.get();
        if (directionsListView != null && errorView != null) {
            directionsListView.setVisibility(View.GONE);
            errorView.setText(resources.getString(R.string.directions_error_message));
            errorView.setVisibility(View.VISIBLE);
        } else {
            Log.e("DirectionsQuery:showError", "Directions ListView and/or error TextView references are null.");
        }
    }
}
