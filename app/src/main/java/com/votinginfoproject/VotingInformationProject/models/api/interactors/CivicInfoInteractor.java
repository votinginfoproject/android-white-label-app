package com.votinginfoproject.VotingInformationProject.models.api.interactors;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.api.requests.CivicInfoRequest;
import com.votinginfoproject.VotingInformationProject.models.api.requests.RequestType;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by marcvandehey on 3/22/16.
 */
public class CivicInfoInteractor extends BaseInteractor<VoterInfo, CivicInfoInteractor.CivicInfoCallback> {
    private static final String TAG = CivicInfoInteractor.class.getSimpleName();

    @Override
    protected VoterInfo doInBackground(RequestType... params) {
        VoterInfo response = null;

        if (params.length > 0 && params[0].getClass().equals(CivicInfoRequest.class)) {
            OkHttpClient client = new OkHttpClient();
            Gson gson = new GsonBuilder().create();

            CivicInfoRequest civicInfoRequest = (CivicInfoRequest) params[0];

            Request request = new Request.Builder().url(civicInfoRequest.buildQueryString()).build();

            try {
                Response okHttpResponse = client.newCall(request).execute();
                response = gson.fromJson(okHttpResponse.body().string(), VoterInfo.class);

                Log.v(TAG, "RESPONSE: " + okHttpResponse.toString());
                Log.v(TAG, "BODY: " + okHttpResponse.body().string());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Unexpected error in VoterInfo Request");
            }
        }

        return response;
    }

    @Override
    protected void onPostExecute(VoterInfo voterInfoResponse) {
        super.onPostExecute(voterInfoResponse);

        CivicInfoCallback callback = getCallback();

        if (callback != null) {
            callback.civicInfoResponse(voterInfoResponse);
        }
    }

    public interface CivicInfoCallback {
        void civicInfoResponse(VoterInfo response);
    }
}
