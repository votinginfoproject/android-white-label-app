package com.votinginfoproject.VotingInformationProject.models.api.requests;

import android.support.annotation.NonNull;

import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

/**
 * Created by marcvandehey on 4/18/16.
 */
public class GeocodeVoterInfoRequest implements RequestType {
    private static final String TAG = GeocodeRequest.class.getSimpleName();

    private VoterInfo voterInfo;
    private String geocodeKey;

    public GeocodeVoterInfoRequest(@NonNull String geocodeKey, @NonNull VoterInfo voterInfo) {
        this.geocodeKey = geocodeKey;
        this.voterInfo = voterInfo;
    }

    public VoterInfo getVoterInfo() {
        return voterInfo;
    }

    public String getGeocodeKey() {
        return geocodeKey;
    }

    @Override
    public String buildQueryString() {
        return "";
    }
}
