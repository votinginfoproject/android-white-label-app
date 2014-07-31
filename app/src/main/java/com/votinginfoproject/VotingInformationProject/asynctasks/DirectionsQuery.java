package com.votinginfoproject.VotingInformationProject.asynctasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.adapters.DirectionsAdapter;
import com.votinginfoproject.VotingInformationProject.adapters.LocationsAdapter;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirectionsApiLeg;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirectionsApiResponse;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirectionsApiStep;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VIPAppContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Asynchronously query the Google Directions API for directions from user's address to a polling
 * location, via a given mode.
 *
 *
 * Created by kathrynkillebrew on 7/31/14.
 */
public class DirectionsQuery extends AsyncTask<String, String, GoogleDirectionsApiResponse> {

    String homeCoordinates;
    String destinationCoordinates;
    String travelMode;
    String api_key;
    HttpContext httpContext;
    HttpClient httpClient;
    Context context;
    ListView directionsListView;
    TextView errorView;

    public DirectionsQuery(ListView listView, TextView errorView, String originCoordinates,
                           String destinationCoordinates, String travelMode) {
        super();
        context = VIPAppContext.getContext();
        this.homeCoordinates = originCoordinates;
        this.destinationCoordinates = destinationCoordinates;
        this.travelMode = travelMode;
        this.api_key = context.getResources().getString(R.string.google_api_browser_key);

        this.directionsListView = listView;
        this.errorView = errorView;

        this.httpContext = new BasicHttpContext();
        this.httpClient = new DefaultHttpClient();
    }

    @Override
    protected GoogleDirectionsApiResponse doInBackground(String... addresses) {

        GoogleDirectionsApiResponse directionsResponse = null;

        // Uri.Builder is not safe for concurrent use, so just build a string
        StringBuilder uri = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?origin=");
        uri.append(homeCoordinates);
        uri.append("&destination=");
        uri.append(destinationCoordinates);
        uri.append("&mode=");
        uri.append(travelMode);

        if (travelMode == "transit") {
            // must specify departure or arrival time
            uri.append("&departure_time=");
            uri.append(String.valueOf(System.currentTimeMillis() / 1000)); // now, as seconds since epoch
        }

        uri.append("&key=");
        uri.append(api_key);
        Log.d("DirectionsQuery", uri.toString());

        try {
            HttpGet httpGet = new HttpGet(uri.toString());
            HttpResponse response = httpClient.execute(httpGet, httpContext);
            int status = response.getStatusLine().getStatusCode();
            InputStream in = response.getEntity().getContent();
            BufferedReader ir = new BufferedReader(new InputStreamReader(in));

            Log.d("DirectionsQuery", "GOT RESPONSE STATUS: " + status);

            Gson gson = new GsonBuilder().create();
            if (status == 200) {
                return gson.fromJson(ir, GoogleDirectionsApiResponse.class);
            } else {
                // error
                Log.e("DirectionsQuery", "Error with response: " + ir.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return directionsResponse;
    }

    @Override
    protected void onPostExecute(GoogleDirectionsApiResponse response) {
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
        GoogleDirectionsApiLeg leg = response.routes.get(0).legs.get(0);

        Log.d("DirectionsQuery", "Start at " + leg.start_address);
        Log.d("DirectionsQuery", "End at " + leg.end_address);
        Log.d("DirectionsQuery", "Distance / duration: " + leg.distance.text + " - " + leg.duration.text);
        Log.d("DirectionsQuery", "Number of steps: " + leg.steps.size());

        for (GoogleDirectionsApiStep step : leg.steps) {
            Log.d("DirectionsQuery", step.html_instructions);
            Log.d("DirectionsQuery", step.distance.text + " - " + step.duration.text);
        }

        DirectionsAdapter listAdapter = new DirectionsAdapter(leg.steps);
        errorView.setVisibility(View.GONE);
        directionsListView.setVisibility(View.VISIBLE);
        directionsListView.setAdapter(listAdapter);
    }

    private void showError() {
        directionsListView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }
}
