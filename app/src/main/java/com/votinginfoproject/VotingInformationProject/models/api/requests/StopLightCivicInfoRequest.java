package com.votinginfoproject.VotingInformationProject.models.api.requests;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.votinginfoproject.VotingInformationProject.R;

/**
 * Created by marcvandehey on 3/28/16.
 */
public class StopLightCivicInfoRequest extends CivicInfoRequest {
    private final static String TAG = StopLightCivicInfoRequest.class.getSimpleName();
    private String stopLightAPIKey;

    /**
     * Creates a CivicInfoRequest request body for the CivicInfoInteractor to consume
     *
     * @param context
     * @param electionId
     * @param address
     */
    public StopLightCivicInfoRequest(@NonNull Context context, @Nullable String electionId, @NonNull String address) {
        super(context, electionId, address);

        stopLightAPIKey = context.getString(R.string.stoplight_api_key);
    }

    @Override
    public String getAuthorityString() {
        //Check for stoplightAPI key if none available use civic api instead
        if (stopLightAPIKey != null && !stopLightAPIKey.isEmpty()) {
            return stopLightAPIKey + ".stoplight-proxy.io";
        }

        Log.w(TAG, "No Stoplight API Key detected, falling back to Civic API");

        return super.getAuthorityString();
    }
}
