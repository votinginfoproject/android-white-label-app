package com.votinginfoproject.VotingInformationProject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.votinginfoproject.VotingInformationProject.R;

/**
 * Class for custom map info window to use with polling locations.
 * <p/>
 * Created by kathrynkillebrew on 7/29/14.
 */
public class LocationInfoWindow implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater = null;

    public LocationInfoWindow(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View popup = inflater.inflate(R.layout.map_popup, null);
        TextView text = (TextView) popup.findViewById(R.id.title);
        text.setText(marker.getTitle());
        text = (TextView) popup.findViewById(R.id.snippet);
        text.setText(marker.getSnippet());

        return (popup);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}
