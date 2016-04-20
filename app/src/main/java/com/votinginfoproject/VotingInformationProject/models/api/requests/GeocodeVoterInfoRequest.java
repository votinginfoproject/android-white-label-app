package com.votinginfoproject.VotingInformationProject.models.api.requests;

import android.support.annotation.NonNull;

import com.votinginfoproject.VotingInformationProject.models.VoterInfoResponse;

/**
 * Created by marcvandehey on 4/18/16.
 */
public class GeocodeVoterInfoRequest implements RequestType {
    private static final String TAG = GeocodeRequest.class.getSimpleName();

    private VoterInfoResponse voterInfoResponse;
    private String geocodeKey;

    public GeocodeVoterInfoRequest(@NonNull String geocodeKey, @NonNull VoterInfoResponse voterInfoResponse) {
        this.geocodeKey = geocodeKey;
        this.voterInfoResponse = voterInfoResponse;
    }

    public VoterInfoResponse getVoterInfoResponse() {
        return voterInfoResponse;
    }

    public String getGeocodeKey() {
        return geocodeKey;
    }

    @Override
    public String buildQueryString() {
        return "";
    }
}
