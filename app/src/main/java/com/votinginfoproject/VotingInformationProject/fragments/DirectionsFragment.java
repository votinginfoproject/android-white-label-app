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
import com.votinginfoproject.VotingInformationProject.models.CivicApiAddress;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.singletons.UserPreferences;

import java.util.HashMap;


public class DirectionsFragment extends Fragment {
    private final String TAG = DirectionsFragment.class.getSimpleName();

    private static final String LOCATION_ID = "location_id";
    private static final String USE_CURRENT_LOCATION = "use_location";

    private String location_id;
    private boolean use_location;

    private CivicApiAddress locationAddress;
    private LatLng homeLatLng;
    private ViewGroup mContainer;
    VIPTabBarActivity myActivity;
    private View rootView;
    private TextView noneFoundMessage;
    private ListView directionsList;
    private Button openInMapsButton;

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
        Bundle args = getArguments();

        if (getArguments() != null) {
            location_id = args.getString(LOCATION_ID);
            Log.d(TAG, "Got location " + location_id);
            use_location = args.getBoolean(USE_CURRENT_LOCATION);

            if (use_location) {
                Log.d(TAG, "Showing directions from current location");
            } else {
                Log.d(TAG, "Showing directions from entered address");
            }
        }
    }

    public static DirectionsFragment newInstance(String key, boolean use_location) {
        DirectionsFragment fragment = new DirectionsFragment();
        Bundle args = new Bundle();
        args.putString(LOCATION_ID, key);
        args.putBoolean(USE_CURRENT_LOCATION, use_location);
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

        Log.d(TAG + ":onCreateView", "Hiding location list container's view");

        container.getChildAt(0).setVisibility(View.INVISIBLE);

        rootView = inflater.inflate(R.layout.fragment_directions, container, false);
        myActivity = (VIPTabBarActivity) this.getActivity();
        VoterInfo voterInfo = UserPreferences.getVoterInfo();

        Resources res = myActivity.getResources();
        unselectedTextColor = res.getColor(R.color.button_blue);
        selectedTextColor = res.getColor(R.color.white);

        // get selected location
        locationAddress = voterInfo.getAddressForId(location_id);

        if (use_location) {
            // get user's current location
            homeLatLng = myActivity.getUserLocation(true);
        } else {
            // get user entered address' location
            homeLatLng = myActivity.getHomeLatLng();
        }

        // location labels
        TextView directions_title_label = (TextView) rootView.findViewById(R.id.directions_title);
        TextView directions_subtitle_label = (TextView) rootView.findViewById(R.id.directions_subtitle);

        String locationName = voterInfo.getDescriptionForId(location_id);

        if (!locationName.isEmpty()) {
            directions_title_label.setText(locationName);
            directions_subtitle_label.setText(locationAddress.toGeocodeString());
        } else if (locationAddress.locationName != null && !locationAddress.locationName.isEmpty()) {
            directions_title_label.setText(locationAddress.locationName);
            directions_subtitle_label.setText(locationAddress.toGeocodeString());
        } else {
            directions_title_label.setText(locationAddress.toString());
            directions_subtitle_label.setVisibility(View.GONE);
        }

        directionsList = (ListView) rootView.findViewById(R.id.directions_list);
        noneFoundMessage = (TextView) rootView.findViewById(R.id.directions_none_found_message);
        openInMapsButton = (Button) rootView.findViewById(R.id.directions_open_in_maps_button);

        setUpWithOrigin(homeLatLng);

        // highlight default button
        Button walkButton = (Button) rootView.findViewById(R.id.directions_walk_button);
        walkButton.setTextColor(selectedTextColor);
        walkButton.setBackgroundResource(R.drawable.button_bar_button_selected);
        lastSelectedButton = walkButton;

        return rootView;
    }

    /**
     * Helper function to set origin location for directions list; public so activity can call it
     * in case user location found after view shown.
     *
     * @param newLocation origin co-ordinates to set
     */
    public void setUpWithOrigin(LatLng newLocation) {
        homeLatLng = newLocation;

        // check for missing origin
        if (homeLatLng == null) {
            Log.d(TAG, "Got null origin location");
            directionsList.setVisibility(View.GONE);
            noneFoundMessage.setText(R.string.directions_no_origin_message);
            noneFoundMessage.setVisibility(View.VISIBLE);
            openInMapsButton.setVisibility(View.GONE);
            // clear last directions polyline
            myActivity.polylineCallback("", null);
            return;
        }

        Log.d(TAG, "Got origin location; setting up directions list");

        // add click handlers for button bar buttons
        setButtonInBarClickListener(R.id.directions_bike_button);
        setButtonInBarClickListener(R.id.directions_transit_button);
        setButtonInBarClickListener(R.id.directions_walk_button);
        setButtonInBarClickListener(R.id.directions_drive_button);

        // click handler for view-in-maps button
        openInMapsButton.setVisibility(View.VISIBLE);
        openInMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open Maps intent (or Maps in browser)
                String uri = "https://maps.google.com/maps?saddr=" + homeLatLng.latitude + "," + homeLatLng.longitude;
                uri += "&daddr=" + locationAddress.latitude + "," + locationAddress.longitude;
                uri += "&dirflg=" + directionsFlags.get(directionsMode);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });

        // click handler for map button
        rootView.findViewById(R.id.directions_map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myActivity.showMap(location_id);
            }
        });

        // set up directions list
        queryDirections();
    }

    /**
     * Helper function to set click handlers for the list filter buttons
     *
     * @param buttonId R id of the button to listen to
     */
    private void setButtonInBarClickListener(final int buttonId) {
        rootView.findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonId == lastSelectedButtonId) {
                    return; // ignore button click if already viewing that list
                }

                Button btn = (Button) v;

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

                Log.d(TAG, "New directions mode is " + directionsMode);
                queryDirections();

                lastSelectedButtonId = buttonId;
                lastSelectedButton = btn;
            }
        });
    }

    /**
     * Helper function to fetch directions from asynchronous query.
     */
    private void queryDirections() {
        // clear last directions polyline
        myActivity.polylineCallback("", null);
        String homeCoords = homeLatLng.latitude + "," + homeLatLng.longitude;
        String locationCoords = locationAddress.latitude + "," + locationAddress.longitude;

        new DirectionsQuery(getActivity(), directionsList, noneFoundMessage, homeCoords, locationCoords, myActivity).execute(directionsMode);
    }

    @Override
    public void onDetach() {
        Log.d(TAG + ":onDetach", "Showing location list container's view again");
        mContainer.getChildAt(0).setVisibility(View.VISIBLE);

        super.onDetach();
    }
}
