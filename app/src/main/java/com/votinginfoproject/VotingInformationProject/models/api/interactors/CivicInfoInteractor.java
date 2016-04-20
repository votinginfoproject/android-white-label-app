package com.votinginfoproject.VotingInformationProject.models.api.interactors;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.votinginfoproject.VotingInformationProject.models.VoterInfoResponse;
import com.votinginfoproject.VotingInformationProject.models.api.requests.CivicInfoRequest;
import com.votinginfoproject.VotingInformationProject.models.api.requests.RequestType;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by marcvandehey on 3/22/16.
 */
public class CivicInfoInteractor extends BaseInteractor<VoterInfoResponse, CivicInfoInteractor.CivicInfoCallback> {
    private static final String TAG = CivicInfoInteractor.class.getSimpleName();

    @Override
    protected VoterInfoResponse doInBackground(RequestType... params) {
        VoterInfoResponse response = null;

        if (params.length > 0) {
            OkHttpClient client = new OkHttpClient();
            Gson gson = new GsonBuilder().create();

            CivicInfoRequest civicInfoRequest = (CivicInfoRequest) params[0];

            Request request = new Request.Builder().url(civicInfoRequest.buildQueryString()).build();

            try {
                Response okHttpResponse = client.newCall(request).execute();
                response = gson.fromJson(okHttpResponse.body().string(), VoterInfoResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Unexpected error in VoterInfoResponse Request");
            }
        }

        return response;
    }

    @Override
    protected void onPostExecute(VoterInfoResponse voterInfoResponseResponse) {
        super.onPostExecute(voterInfoResponseResponse);

        CivicInfoCallback callback = getCallback();

        if (callback != null) {
            callback.civicInfoResponse(voterInfoResponseResponse);
        }
    }

    public interface CivicInfoCallback {
        void civicInfoResponse(VoterInfoResponse response);
    }
}
