package com.votinginfoproject.VotingInformationProject.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirectionsApiStep;
import com.votinginfoproject.VotingInformationProject.models.VIPAppContext;

import java.util.List;

/**
 * Created by kathrynkillebrew on 8/1/14.
 */
public class DirectionsAdapter extends ArrayAdapter<GoogleDirectionsApiStep> {

    // View lookup cache.  Pattern from here:
    // https://github.com/thecodepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
    private static class ViewHolder {
        TextView instructions;
        TextView distanceDuration;
    }
    /**
     *
     * @param steps list of steps returned by Google Directions API to put in ListView
     */
    public DirectionsAdapter(List<GoogleDirectionsApiStep> steps) {
        super(VIPAppContext.getContext(), R.layout.directions_list_item, steps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GoogleDirectionsApiStep step = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.directions_list_item, parent, false);
            viewHolder.instructions = (TextView) convertView.findViewById(R.id.directions_list_item_instructions);
            viewHolder.distanceDuration = (TextView) convertView.findViewById(R.id.directions_list_item_distance_duration);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.instructions.setText(Html.fromHtml(step.html_instructions));
        viewHolder.distanceDuration.setText(step.distance.text + " - " + step.duration.text);

        // Return the completed view to render on screen
        return convertView;
    }
}
