package com.votinginfoproject.VotingInformationProject.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;
import com.votinginfoproject.VotingInformationProject.asynctasks.DirectionsQuery;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;

import java.util.HashMap;


public class DirectionsFragment extends Fragment {

    private static final String LOCATION_ID = "location_id";
    private String location_id;
    private PollingLocation location;
    private LatLng homeLatLng;
    private ViewGroup mContainer;
    private View rootView;
    private TextView noneFoundMessage;
    private ListView directionsList;

    HashMap<String, String> directionsFlags;

    // track which location filter button was last clicked, and only refresh list if it changed
    int lastSelectedButtonId = R.id.directions_walk_button;
    Button lastSelectedButton;

    int selectedTextColor;
    int unselectedTextColor;

    String directionsMode = "walking";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            location_id = getArguments().getString(LOCATION_ID);
            Log.d("DirectionsFragment", "Got location " + location_id);
        }
    }

    public static DirectionsFragment newInstance(String key) {
        DirectionsFragment fragment = new DirectionsFragment();
        Bundle args = new Bundle();
        args.putString(LOCATION_ID, key);
        fragment.setArguments(args);
        return fragment;
    }

    public DirectionsFragment() {
        // see comment here regarding undocumented dirflg parameter to get Google Maps to open
        // with a given transit mode pre-selected:
        // http://stackoverflow.com/questions/14161591/ability-to-choose-direction-type-for-google-maps-intent
        directionsFlags = new HashMap<String, String>(4) {{
            put("walking", "w");
            put("transit", "r");
            put("bicycling", "b");
            put("driving", "d");
        }};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContainer = container;
        Log.d("DirectionsFragment:onCreateView", "Hiding location list container's view");
        container.getChildAt(0).setVisibility(View.INVISIBLE);

        rootView = inflater.inflate(R.layout.fragment_directions, container, false);
        final VIPTabBarActivity myActivity = (VIPTabBarActivity)this.getActivity();

        Resources res = myActivity.getResources();
        unselectedTextColor = res.getColor(R.color.button_blue);
        selectedTextColor = res.getColor(R.color.white);

        // get selected location
        location = myActivity.getLocationForId(location_id);

        // get user entered address' location
        homeLatLng = myActivity.getHomeLatLng();

        // location labels
        TextView directions_title_label = (TextView)rootView.findViewById(R.id.directions_title);
        TextView directions_subtitle_label = (TextView)rootView.findViewById(R.id.directions_subtitle);

        if (location.name != null && !location.name.isEmpty()) {
            directions_title_label.setText(location.name);
            directions_subtitle_label.setText(location.address.toGeocodeString());
        } else if (location.address.locationName != null && !location.address.locationName.isEmpty()) {
            directions_title_label.setText(location.address.locationName);
            directions_subtitle_label.setText(location.address.toGeocodeString());
        } else {
            directions_title_label.setText(location.address.toString());
            directions_subtitle_label.setVisibility(View.GONE);
        }

        // add click handlers for button bar buttons
        setButtonInBarClickListener(R.id.directions_bike_button);
        setButtonInBarClickListener(R.id.directions_transit_button);
        setButtonInBarClickListener(R.id.directions_walk_button);
        setButtonInBarClickListener(R.id.directions_drive_button);

        // click handler for view-in-maps button
        rootView.findViewById(R.id.directions_open_in_maps_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open Maps intent (or Maps in browser)
                String uri = "https://maps.google.com/maps?saddr=" + homeLatLng.latitude + "," + homeLatLng.longitude;
                uri += "&daddr=" + location.address.latitude + "," + location.address.longitude;
                uri += "&dirflg=" + directionsFlags.get(directionsMode);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });

        // highlight default button
        Button walkButton = (Button)rootView.findViewById(R.id.directions_walk_button);
        walkButton.setTextColor(selectedTextColor);
        walkButton.setBackgroundResource(R.drawable.button_bar_button_selected);
        lastSelectedButton = walkButton;

        // set up directions list
        directionsList = (ListView)rootView.findViewById(R.id.directions_list);
        noneFoundMessage = (TextView) rootView.findViewById(R.id.directions_none_found_message);
        queryDirections();

        return rootView;
    }

    /**
     * Helper function to set click handlers for the list filter buttons
     * @param buttonId R id of the button to listen to
     */
    private void setButtonInBarClickListener(final int buttonId) {

        rootView.findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonId == lastSelectedButtonId) {
                    return; // ignore button click if already viewing that list
                }

                Button btn = (Button)v;

                // highlight current selection (and un-highlight last button)
                btn.setTextColor(selectedTextColor);
                btn.setBackgroundResource(R.drawable.button_bar_button_selected);
                lastSelectedButton.setTextColor(unselectedTextColor);
                lastSelectedButton.setBackgroundResource(R.drawable.button_bar_button);

                // change directions mode and re-query
                if (buttonId == R.id.directions_walk_button) {
                    directionsMode = "walking";
                } else if (buttonId == R.id.directions_bike_button) {
                    directionsMode = "bicycling";
                } else if (buttonId == R.id.directions_transit_button) {
                    directionsMode = "transit";
                } else {
                    directionsMode = "driving";
                }

                Log.d("DirectionsFragment", "New directions mode is " + directionsMode);

                queryDirections();

                lastSelectedButtonId = buttonId;
                lastSelectedButton = btn;
            }
        });
    }

    private void queryDirections() {
        String homeCoords = homeLatLng.latitude + "," + homeLatLng.longitude;
        String locationCoords = location.address.latitude + "," + location.address.longitude;
        new DirectionsQuery(directionsList, noneFoundMessage, homeCoords, locationCoords, directionsMode).execute();
    }

    @Override
    public void onDetach() {
        Log.d("DirectionsFragment:onDetach", "Showing location list container's view again");
        mContainer.getChildAt(0).setVisibility(View.VISIBLE);

        super.onDetach();
    }

}
