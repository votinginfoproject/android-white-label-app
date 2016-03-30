package com.votinginfoproject.VotingInformationProject.models.api.requests;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by marcvandehey on 3/29/16.
 */
public class DirectionsRequest implements RequestType {
    private static final String TAG = DirectionsRequest.class.getSimpleName();

    private String originCoordinates;
    private String destinationCoordinates;
    private String directionsKey;
    private String travelMode;
    private Context mContext;

    public DirectionsRequest(@NonNull Context context,
                             @NonNull String directionsKey,
                             @NonNull String travelMode,
                             @NonNull String originCoordinates,
                             @NonNull String destinationCoordinates) {

        this.travelMode = travelMode;
        this.originCoordinates = originCoordinates;
        this.destinationCoordinates = destinationCoordinates;
        this.directionsKey = directionsKey;
        this.mContext = context;
    }

    public String getTravelMode() {
        return travelMode;
    }

    @Override
    public String buildQueryString() {
        StringBuilder uri = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?origin=");
        uri.append(originCoordinates);
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
        uri.append(directionsKey);
        Log.d(TAG, uri.toString());

        return uri.toString();
    }
}
