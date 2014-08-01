package com.votinginfoproject.VotingInformationProject.fragments;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;
import com.votinginfoproject.VotingInformationProject.adapters.LocationInfoWindow;
import com.votinginfoproject.VotingInformationProject.models.CivicApiAddress;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VIPAppContext;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.util.ArrayList;
import java.util.HashMap;


public class VIPMapFragment extends SupportMapFragment {

    private static final String LOCATION_ID = "location_id";
    VoterInfo voterInfo;
    VIPTabBarActivity mActivity;
    static final Resources mResources = VIPAppContext.getContext().getResources();
    View mapView;
    RelativeLayout rootView;
    LayoutInflater layoutInflater;
    ArrayList<PollingLocation> allLocations;
    GoogleMap map;
    String locationId;
    PollingLocation selectedLocation;
    LatLng thisLocation;
    LatLng homeLocation;

    HashMap<String, MarkerOptions> markers;
    // track the internally-assigned ID for each marker and map it to the location's key
    HashMap<String, String> markerIds;

    Button allButton;
    Button earlyButton;
    Button pollingButton;

    // track filters
    boolean showPolling = true;
    boolean showEarly = true;


    public static VIPMapFragment newInstance(String tag) {
        // instantiate with map options
        GoogleMapOptions options = new GoogleMapOptions();
        VIPMapFragment fragment = VIPMapFragment.newInstance(options);

        Bundle args = new Bundle();
        args.putString(LOCATION_ID, tag);
        fragment.setArguments(args);

        return fragment;
    }

    public static VIPMapFragment newInstance(GoogleMapOptions options) {
        Bundle args = new Bundle();
        // need to send API key to initialize map
        args.putParcelable(mResources.getString(R.string.google_api_android_key), options);
        VIPMapFragment fragment = new VIPMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public VIPMapFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // programmatically add map view, so button bar appears on top
        mapView = super.onCreateView(inflater, container, savedInstanceState);
        rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_map, container, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.locations_map_button_bar);
        rootView.addView(mapView, layoutParams);

        mActivity = (VIPTabBarActivity) this.getActivity();

        voterInfo = ((VIPTabBarActivity) mActivity).getVoterInfo();
        allLocations = mActivity.getAllLocations();
        homeLocation = mActivity.getHomeLatLng();

        // set selected location to zoom to
        if (locationId.equals("home")) {
            thisLocation = homeLocation;
        } else {
            selectedLocation = mActivity.getLocationForId(locationId);
            CivicApiAddress address = selectedLocation.address;
            thisLocation = new LatLng(address.latitude, address.longitude);
        }

        // check if already instantiated
        if (map == null) {
            map = getMap();
            map.setInfoWindowAdapter(new LocationInfoWindow(layoutInflater));

            // start asynchronous task to add markers to map
            new AddMarkersTask().execute(locationId);

            // wait for map layout to occur before zooming to location
            final ViewTreeObserver observer = mapView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (observer.isAlive()) {
                        observer.removeOnGlobalLayoutListener(this);
                    }

                    // add marker for user-entered address
                    map.addMarker(new MarkerOptions()
                                    .position(homeLocation)
                                    .title(mResources.getString(R.string.locations_map_user_address_label))
                    );
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(thisLocation, 15));
                }
            });
        } else {
            map.clear();
        }

        allButton = (Button)rootView.findViewById(R.id.locations_map_all_button);
        pollingButton = (Button)rootView.findViewById(R.id.locations_map_polling_button);
        earlyButton = (Button)rootView.findViewById(R.id.locations_map_early_button);

        // highlight default button
        allButton.setBackgroundColor(Color.LTGRAY);

        // set click handlers for filter buttons
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEarly = true;
                showPolling = true;
                allButton.setBackgroundColor(Color.LTGRAY);
                earlyButton.setBackgroundColor(Color.TRANSPARENT);
                pollingButton.setBackgroundColor(Color.TRANSPARENT);
                refreshMapView();
            }
        });

        earlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEarly = true;
                showPolling = false;
                earlyButton.setBackgroundColor(Color.LTGRAY);
                pollingButton.setBackgroundColor(Color.TRANSPARENT);
                allButton.setBackgroundColor(Color.TRANSPARENT);
                refreshMapView();
            }
        });

        pollingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEarly = false;
                showPolling = true;
                pollingButton.setBackgroundColor(Color.LTGRAY);
                earlyButton.setBackgroundColor(Color.TRANSPARENT);
                allButton.setBackgroundColor(Color.TRANSPARENT);
                refreshMapView();
            }
        });

        // set click handler for info window (to go to directions list)
        // info window is just a bitmap, so can't listen for clicks on elements within it.
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // get location key for this marker's ID
                String key = markerIds.get(marker.getId());
                Log.d("LocationsFragment", "Clicked marker for " + key);
                mActivity.showDirections(key);
            }
        });

        return rootView;
    }

    private void refreshMapView() {
        map.clear();
        new AddMarkersTask().execute(locationId);

        // add marker for user-entered address
        map.addMarker(new MarkerOptions()
                        .position(homeLocation)
                        .title(mResources.getString(R.string.locations_map_user_address_label))
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);

        layoutInflater = getLayoutInflater(savedInstanceState);

        if (getArguments() != null) {
            locationId = getArguments().getString(LOCATION_ID);
        }
    }

    private class AddMarkersTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... select_locations) {
            markers = new HashMap<String, MarkerOptions>(allLocations.size());
            markerIds = new HashMap<String, String>(allLocations.size());

            // use green markers for early voting sites
            if (voterInfo.earlyVoteSites != null && showEarly) {
                for (PollingLocation location : voterInfo.earlyVoteSites) {
                    if (location.address.latitude == 0) {
                        Log.d("VIPMapFragment", "Skipping adding to map location " + location.name);
                        continue;
                    }
                    markers.put(location.address.toGeocodeString(), createMarkerOptions(location, BitmapDescriptorFactory.HUE_GREEN));
                }
            }

            // use blue markers for polling locations
            if (voterInfo.pollingLocations != null && showPolling) {
                for (PollingLocation location : voterInfo.pollingLocations) {
                    if (location.address.latitude == 0) {
                        Log.d("VIPMapFragment", "Skipping adding to map location " + location.name);
                        continue;
                    }
                    markers.put(location.address.toGeocodeString(), createMarkerOptions(location, BitmapDescriptorFactory.HUE_AZURE));
                }
            }

            return locationId;
        }

        @Override
        protected  void onPostExecute(String checkId) {
            for (String key : markers.keySet()) {
                Marker marker = map.addMarker(markers.get(key));
                markerIds.put(marker.getId(), key);
                if (key.equals(locationId)) {
                    // show popup for marker at selected location
                    marker.showInfoWindow();
                }
            }
        }
    }

    private MarkerOptions createMarkerOptions(PollingLocation location, float color) {

        String showTitle = location.name;
        if (showTitle == null || showTitle.isEmpty()) {
            showTitle = location.address.locationName;
        }

        String showSnippet = location.address.toGeocodeString();
        showSnippet += "\n\nHours:\n" + location.pollingHours;

        return new MarkerOptions()
                .position(new LatLng(location.address.latitude, location.address.longitude))
                .title(showTitle)
                .snippet(showSnippet)
                .icon(BitmapDescriptorFactory.defaultMarker(color))
        ;
    }

}