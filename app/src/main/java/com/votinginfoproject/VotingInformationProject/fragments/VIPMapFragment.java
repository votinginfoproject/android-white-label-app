package com.votinginfoproject.VotingInformationProject.fragments;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;
import com.votinginfoproject.VotingInformationProject.adapters.LocationInfoWindow;
import com.votinginfoproject.VotingInformationProject.models.CivicApiAddress;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VIPAppContext;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class VIPMapFragment extends SupportMapFragment {

    private static final String LOCATION_ID = "location_id";
    private static final String POLYLINE = "polyline";

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
    LatLng currentLocation;
    String homeAddress;
    String currentAddress;
    String encodedPolyline;
    LatLngBounds polylineBounds;
    boolean haveElectionAdminBody;

    HashMap<String, MarkerOptions> markers;
    // track the internally-assigned ID for each marker and map it to the location's key
    HashMap<String, String> markerIds;

    int selectedButtonTextColor;
    int unselectedButtonTextColor;
    int lastSelectedButtonId = R.id.locations_map_all_button;
    Button lastSelectedButton;

    // track filters
    boolean showPolling = true;
    boolean showEarly = true;


    public static VIPMapFragment newInstance(String tag, String polyline) {
        // instantiate with map options
        GoogleMapOptions options = new GoogleMapOptions();
        VIPMapFragment fragment = VIPMapFragment.newInstance(options);

        Bundle args = new Bundle();
        args.putString(LOCATION_ID, tag);
        args.putString(POLYLINE, polyline);
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
        Resources res = mActivity.getResources();
        unselectedButtonTextColor = res.getColor(R.color.button_blue);
        selectedButtonTextColor = res.getColor(R.color.white);

        voterInfo = mActivity.getVoterInfo();
        allLocations = mActivity.getAllLocations();
        homeLocation = mActivity.getHomeLatLng();
        currentLocation = mActivity.getUserLocation();
        currentAddress = mActivity.getUserLocationAddress();
        homeAddress = mActivity.getHomeAddress();

        // check if this map view is for an election administration body
        if (locationId.equals(ElectionAdministrationBody.AdminBody.STATE) ||
                locationId.equals(ElectionAdministrationBody.AdminBody.LOCAL)) {
            haveElectionAdminBody = true;
        } else {
            haveElectionAdminBody = false;
        }

        // set selected location to zoom to
        if (locationId.equals("home")) {
            thisLocation = homeLocation;
        } else if (haveElectionAdminBody) {
            thisLocation = mActivity.getAdminBodyLatLng(locationId);

        } else {
            Log.d("VIPMapFragment", "Have location ID: " + locationId);
            selectedLocation = mActivity.getLocationForId(locationId);
            CivicApiAddress address = selectedLocation.address;
            thisLocation = new LatLng(address.latitude, address.longitude);
        }

        // check if already instantiated
        if (map == null) {
            map = getMap();
            map.setMyLocationEnabled(true);
            map.setInfoWindowAdapter(new LocationInfoWindow(layoutInflater));

            // start asynchronous task to add markers to map
            new AddMarkersTask().execute(locationId);

            // wait for map layout to occur before zooming to location
            final ViewTreeObserver observer = mapView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (observer.isAlive()) {

                        // deal with SDK compatibility
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            //noinspection deprecation
                            observer.removeGlobalOnLayoutListener(this);
                        } else {
                            observer.removeOnGlobalLayoutListener(this);
                        }
                    }

                    if (homeLocation != null) {
                        // add marker for user-entered address
                        map.addMarker(new MarkerOptions()
                                        .position(homeLocation)
                                        .title(mResources.getString(R.string.locations_map_user_address_label))
                                        .snippet(homeAddress)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_map))
                        );
                    }

                    if (currentLocation != null) {
                        // add marker for current user location (used for directions)
                        map.addMarker(new MarkerOptions()
                                        .position(currentLocation)
                                        .title(mResources.getString(R.string.locations_map_user_location_label))
                                        .snippet(currentAddress)
                                        .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation))
                        );
                    }

                    if (haveElectionAdminBody) {
                        // add marker for state or local election administration body
                        map.addMarker(new MarkerOptions()
                                        .position(thisLocation)
                                        .title(mResources.getString(R.string.locations_map_election_administration_body_label))
                                        .snippet(mActivity.getAdminBodyAddress(locationId))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        ).showInfoWindow();
                    }

                    if (encodedPolyline != null && !encodedPolyline.isEmpty()) {
                        // show directions line on map
                        PolylineOptions polylineOptions = new PolylineOptions();
                        List<LatLng> pts = PolyUtil.decode(encodedPolyline);
                        polylineOptions.addAll(pts);
                        polylineOptions.color(mResources.getColor(R.color.brand_name_text));
                        map.addPolyline(polylineOptions);
                        polylineBounds = mActivity.getPolylineBounds();
                    }

                    // zoom to fit polyline, with padding in pixels
                    if (polylineBounds != null) {
                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(polylineBounds, 40));
                    } else if (thisLocation != null) {
                        // zoom to selected location
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(thisLocation, 15));
                    }
                }
            });
        } else {
            map.clear();
        }

        // highlight default button
        Button allButton = (Button)rootView.findViewById(R.id.locations_map_all_button);
        allButton.setTextColor(selectedButtonTextColor);
        allButton.setBackgroundResource(R.drawable.button_bar_button_selected);
        lastSelectedButton = allButton;

        // set click handlers for filter buttons
        setButtonBarClickHandlers(R.id.locations_map_early_button);
        setButtonBarClickHandlers(R.id.locations_map_polling_button);
        setButtonBarClickHandlers(R.id.locations_map_all_button);

        // set click handler for info window (to go to directions list)
        // info window is just a bitmap, so can't listen for clicks on elements within it.
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // get location key for this marker's ID
                String key = markerIds.get(marker.getId());

                if (key == null) {
                    // allow for getting directions from election admin body location
                    if (haveElectionAdminBody) {
                        key = locationId;
                    } else {
                        return;  // do nothing for taps on user address info window
                    }
                }
                
                Log.d("LocationsFragment", "Clicked marker for " + key);
                mActivity.showDirections(key);
            }
        });

        return rootView;
    }

    private void setButtonBarClickHandlers(final int buttonId) {
        rootView.findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonId == lastSelectedButtonId) {
                    return; // ignore button click if already viewing that list
                }

                Button btn = (Button)v;

                // highlight current selection (and un-highlight others)
                btn.setBackgroundResource(R.drawable.button_bar_button_selected);
                btn.setTextColor(selectedButtonTextColor);
                lastSelectedButton.setTextColor(unselectedButtonTextColor);
                lastSelectedButton.setBackgroundResource(R.drawable.button_bar_button);

                if (buttonId == R.id.locations_map_early_button) {
                    showEarly = true;
                    showPolling = false;
                } else if (buttonId == R.id.locations_map_polling_button) {
                    showEarly = false;
                    showPolling = true;
                } else {
                    // show all
                    showEarly = true;
                    showPolling = true;
                }

                lastSelectedButtonId = buttonId;
                lastSelectedButton = btn;

                refreshMapView();
            }
        });
    }

    private void refreshMapView() {
        map.clear();
        new AddMarkersTask().execute(locationId);

        // add marker for user-entered address
        if (homeLocation != null) {
            map.addMarker(new MarkerOptions()
                            .position(homeLocation)
                            .title(mResources.getString(R.string.locations_map_user_address_label))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_map))
            );
        }

        if (currentLocation != null) {
            // add marker for current user location (used for directions)
            map.addMarker(new MarkerOptions()
                            .position(currentLocation)
                            .title(mResources.getString(R.string.locations_map_user_location_label))
                            .snippet(currentAddress)
                            .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation))
            );
        }

        if (haveElectionAdminBody) {
            // add marker for state or local election administration body
            map.addMarker(new MarkerOptions()
                            .position(thisLocation)
                            .title(mResources.getString(R.string.locations_map_election_administration_body_label))
                            .snippet(mActivity.getAdminBodyAddress(locationId))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            );
        }

        if (encodedPolyline != null && !encodedPolyline.isEmpty()) {
            // show directions line on map
            PolylineOptions polylineOptions = new PolylineOptions();
            List<LatLng> pts = PolyUtil.decode(encodedPolyline);
            polylineOptions.addAll(pts);
            polylineOptions.color(mResources.getColor(R.color.brand_name_text));
            map.addPolyline(polylineOptions);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);

        layoutInflater = getLayoutInflater(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            locationId = args.getString(LOCATION_ID);
            encodedPolyline = args.getString(POLYLINE);
        }
    }

    private class AddMarkersTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... select_locations) {
            markers = new HashMap<String, MarkerOptions>(allLocations.size());
            markerIds = new HashMap<String, String>(allLocations.size());

            // use red markers for early voting sites
            if (voterInfo.earlyVoteSites != null && showEarly) {
                for (PollingLocation location : voterInfo.earlyVoteSites) {
                    if (location.address.latitude == 0) {
                        Log.d("VIPMapFragment", "Skipping adding to map location " + location.name);
                        continue;
                    }
                    markers.put(location.address.toGeocodeString(), createMarkerOptions(location, BitmapDescriptorFactory.HUE_RED));
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

        StringBuilder showSnippet = new StringBuilder(location.address.toGeocodeString());

        if (location.pollingHours != null && !location.pollingHours.isEmpty()) {
            showSnippet.append("\n\n");
            showSnippet.append(mResources.getString(R.string.locations_map_polling_location_hours_label));
            showSnippet.append("\n");
            showSnippet.append(location.pollingHours);
        } else {
            // display placeholder for no hours
            showSnippet.append("\n\n");
            showSnippet.append(mResources.getString(R.string.locations_map_polling_location_hours_not_found));
        }

        return new MarkerOptions()
                .position(new LatLng(location.address.latitude, location.address.longitude))
                .title(showTitle)
                .snippet(showSnippet.toString())
                .icon(BitmapDescriptorFactory.defaultMarker(color))
        ;
    }

}