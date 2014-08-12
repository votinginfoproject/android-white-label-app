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
import com.votinginfoproject.VotingInformationProject.models.googledirections.Leg;
import com.votinginfoproject.VotingInformationProject.models.googledirections.Response;
import com.votinginfoproject.VotingInformationProject.models.googledirections.Step;
import com.votinginfoproject.VotingInformationProject.models.VIPAppContext;

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

/**
 * Asynchronously query the Google Directions API for directions from user's address to a polling
 * location, via a given mode.
 *
 *
 * Created by kathrynkillebrew on 7/31/14.
 */
public class DirectionsQuery extends AsyncTask<String, String, Response> {

    String homeCoordinates;
    String destinationCoordinates;
    String travelMode;
    String api_key;
    HttpContext httpContext;
    HttpClient httpClient;
    Context context;
    Resources resources;
    private final WeakReference<ListView> directionsListViewReference;
    private final WeakReference<TextView> errorViewReference;

    public DirectionsQuery(ListView listView, TextView errorView, String originCoordinates,
                           String destinationCoordinates, String travelMode) {
        super();
        context = VIPAppContext.getContext();
        resources = context.getResources();
        this.homeCoordinates = originCoordinates;
        this.destinationCoordinates = destinationCoordinates;
        this.travelMode = travelMode;
        this.api_key = context.getResources().getString(R.string.google_api_browser_key);

        this.directionsListViewReference = new WeakReference(listView);
        this.errorViewReference = new WeakReference(errorView);

        this.httpContext = new BasicHttpContext();
        this.httpClient = new DefaultHttpClient();
    }

    @Override
    protected Response doInBackground(String... addresses) {
        // Uri.Builder is not safe for concurrent use, so just build a string
        StringBuilder uri = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?origin=");
        uri.append(homeCoordinates);
        uri.append("&destination=");
        uri.append(destinationCoordinates);
        uri.append("&mode=");
        uri.append(travelMode);

        if (travelMode.equals("transit")) {
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

    /** Show loading message while fetching response.
     *
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
        Leg leg = response.routes.get(0).legs.get(0);
        DirectionsAdapter listAdapter = new DirectionsAdapter(leg.steps);
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
