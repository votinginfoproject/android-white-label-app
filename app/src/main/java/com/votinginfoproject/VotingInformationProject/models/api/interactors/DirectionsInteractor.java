package com.votinginfoproject.VotingInformationProject.models.api.interactors;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.votinginfoproject.VotingInformationProject.models.api.requests.DirectionsRequest;
import com.votinginfoproject.VotingInformationProject.models.api.requests.RequestType;
import com.votinginfoproject.VotingInformationProject.models.api.responses.DirectionsResponse;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by marcvandehey on 3/29/16.
 */
public class DirectionsInteractor extends BaseInteractor<DirectionsResponse, DirectionsInteractor.DirectionsCallback> {
    private static final String TAG = CivicInfoInteractor.class.getSimpleName();

    @Override
    protected DirectionsResponse doInBackground(RequestType... params) {

        DirectionsResponse response = null;

        if (params.length > 0 && (params[0] instanceof DirectionsRequest)) {
            OkHttpClient client = new OkHttpClient();
            Gson gson = new GsonBuilder().create();

            DirectionsRequest directionsRequest = (DirectionsRequest) params[0];

            Request request = new Request.Builder().url(directionsRequest.buildQueryString()).build();

            try {
                Response okHttpResponse = client.newCall(request).execute();
                response = gson.fromJson(okHttpResponse.body().string(), DirectionsResponse.class);
                response.mode = directionsRequest.getTravelMode();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Unexpected error in VoterInfo Request");
            }
        } else {
            if (params.length < 1) {
                Log.e(TAG, "No params found: " + params.length);
            } else {
                Log.e(TAG, "Invalid Params" + params[0]);
            }
        }

        return response;
    }

    @Override
    protected void onPostExecute(DirectionsResponse directionsResponse) {
        super.onPostExecute(directionsResponse);

        DirectionsCallback callback = getCallback();

        if (callback != null) {
            callback.directionsResponse(directionsResponse);
        }
    }

    public interface DirectionsCallback {
        void directionsResponse(DirectionsResponse response);
    }
}
