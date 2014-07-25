package com.votinginfoproject.VotingInformationProject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

/**
 * Created by kathrynkillebrew on 7/25/14.
 */
public class LocationsAdapter extends ArrayAdapter<PollingLocation> {

    DecimalFormat distanceFormat;
    Comparator<PollingLocation> pollingLocationComparator;

    // View lookup cache.  Pattern from here:
    // https://github.com/thecodepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
    private static class ViewHolder {
        TextView name;
        TextView address;
        TextView distance;
    }

    public void sortList() {
        sort(pollingLocationComparator);
    }

    public LocationsAdapter(Context context, List<PollingLocation> locations) {
        super(context, R.layout.location_list_item, locations);
        distanceFormat =  new DecimalFormat("0.00");

        // sort locations by distance
        pollingLocationComparator = new Comparator<PollingLocation>() {
            @Override
            public int compare(PollingLocation loc1, PollingLocation loc2) {
                return Double.compare(loc1.address.distance, loc2.address.distance);
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PollingLocation location = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.location_list_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.location_list_item_name);
            viewHolder.address = (TextView) convertView.findViewById(R.id.location_list_item_address);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.location_list_item_distance);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object

        // use either the polling location name, or the name on its address object
        if (location.name != null && !location.name.isEmpty()) {
            viewHolder.name.setText(location.name);
        } else if (location.address.locationName != null && !location.address.locationName.isEmpty()) {
            viewHolder.name.setText(location.address.locationName);
        }

        String addr = location.address.toGeocodeString();
        if (!addr.isEmpty()) {
            viewHolder.address.setText(addr);
        } else {
            viewHolder.address.setVisibility(View.GONE);
        }

        if (location.address.distance > 0) {
            viewHolder.distance.setText(distanceFormat.format(location.address.distance) + " mi.");
        }

        // tag distance text view so it may be updated later
        if (location.id != null) {
            viewHolder.distance.setTag(location.id);
        } else {
            viewHolder.distance.setTag(addr);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
