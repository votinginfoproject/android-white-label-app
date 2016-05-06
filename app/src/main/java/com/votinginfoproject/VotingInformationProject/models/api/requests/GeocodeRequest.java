package com.votinginfoproject.VotingInformationProject.models.api.requests;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by marcvandehey on 4/15/16.
 */
public class GeocodeRequest implements RequestType {
    private static final String TAG = GeocodeRequest.class.getSimpleName();

    private final String address;
    private final String geocodeKey;

    public GeocodeRequest(@NonNull String geocodeKey, @NonNull String address) {
        this.geocodeKey = geocodeKey;
        this.address = address;
    }

    @Override
    public String buildQueryString() {
        Uri.Builder uri = Uri.parse("https://maps.googleapis.com/maps/api/geocode/json").buildUpon();

        uri.appendQueryParameter("address", address);

        uri.appendQueryParameter("key", geocodeKey);

        Log.d(TAG, uri.toString());

        return uri.build().toString();
    }
}
