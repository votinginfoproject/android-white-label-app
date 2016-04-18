package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity.VoterInformationActivity;
import com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity.VoterInformationView;
import com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment.BottomNavigationFragment;
import com.votinginfoproject.VotingInformationProject.models.CivicApiAddress;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.singletons.UserPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VIPMapFragment extends MapFragment implements Toolbar.OnMenuItemClickListener, PollingSitesView, BottomNavigationFragment, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    private static final String LOCATION_ID = "location_id";
    private static final String POLYLINE = "polyline";
    private static final String HOME = "home";
    private static final String ARG_CURRENT_SORT = "current_sort";
    private static final String CURRENT_LOCATION = "current";
    private final String TAG = VIPMapFragment.class.getSimpleName();

    View mapView;
    RelativeLayout rootView;
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

    HashMap<Marker, PollingLocation> mappedPollingLocations;

    private PollingSitesListFragment.PollingSiteOnClickListener mListener;
    private PollingSitesPresenter mPresenter;

    private Toolbar mToolbar;

    public VIPMapFragment() {
        super();
    }

    /**
     * Default newInstance Method.
     *
     * @param context
     * @param tag
     * @param polyline
     * @return
     */
    public static VIPMapFragment newInstance(Context context, String tag, String polyline) {
        // instantiate with map options
        GoogleMapOptions options = new GoogleMapOptions();
        VIPMapFragment fragment = VIPMapFragment.newInstance(context, options);

        Bundle args = new Bundle();
        args.putString(LOCATION_ID, tag);
        args.putString(POLYLINE, polyline);
        fragment.setArguments(args);

        return fragment;
    }

    public static VIPMapFragment newInstance(Context context, String tag, @LayoutRes int currentSort) {
        // instantiate with map options
        GoogleMapOptions options = new GoogleMapOptions();
        VIPMapFragment fragment = VIPMapFragment.newInstance(context, options);

        Bundle args = new Bundle();
        args.putString(LOCATION_ID, tag);
        args.putInt(ARG_CURRENT_SORT, currentSort);

        fragment.setArguments(args);

        return fragment;
    }

    public static VIPMapFragment newInstance(Context context, GoogleMapOptions options) {
        Bundle args = new Bundle();
        // need to send API key to initialize map
        args.putParcelable(context.getString(R.string.google_api_android_key), options);

        VIPMapFragment fragment = new VIPMapFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PollingSitesListFragment.PollingSiteOnClickListener) {
            mListener = (PollingSitesListFragment.PollingSiteOnClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // programmatically add map view, so filter drop-down appears on top
        mapView = super.onCreateView(inflater, container, savedInstanceState);
        rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_map, container, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
        rootView.addView(mapView, layoutParams);

        @LayoutRes int selectedSort = R.id.sort_all;
        if (getArguments() != null) {
            selectedSort = getArguments().getInt(ARG_CURRENT_SORT);
        }

        mPresenter = new PollingSitesPresenterImpl(this, selectedSort);

        allLocations = UserPreferences.getAllPollingLocations();

        homeLocation = UserPreferences.getHomeLatLong();

        //TODO get current location
//        currentLocation = mActivity.getUserLocation();
//        currentAddress = mActivity.getUserLocationAddress();

        homeAddress = UserPreferences.getVoterInfo().normalizedInput.toGeocodeString();

        //TODO rework how we are getting polylines
//        polylineBounds = mActivity.getPolylineBounds();

        // check if this map view is for an election administration body
        if (locationId.equals(ElectionAdministrationBody.AdminBody.STATE) ||
                locationId.equals(ElectionAdministrationBody.AdminBody.LOCAL)) {
            haveElectionAdminBody = true;
        } else {
            haveElectionAdminBody = false;
        }

        // set selected location to zoom to
        if (locationId.equals(HOME)) {
            thisLocation = homeLocation;
        } else if (haveElectionAdminBody) {
            //TODO rework this
            thisLocation = UserPreferences.getVoterInfo().getAdminBodyLatLng(locationId);
        } else {
            Log.d(TAG, "Have location ID: " + locationId);

            //TODO rework this
            selectedLocation = UserPreferences.getVoterInfo().getLocationForId(locationId);

            CivicApiAddress address = selectedLocation.address;
            thisLocation = new LatLng(address.latitude, address.longitude);
        }

        // check if already instantiated
        if (map == null) {
            getMapAsync(this);
        } else {
            map.clear();
            setupMapView(map);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (view != null) {
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);

            if (mToolbar == null) {
                Log.e(TAG, "No toolbar found in class: " + getClass().getSimpleName());
            } else {
                mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                mToolbar.setTitle(R.string.bottom_navigation_title_polls);
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getActivity() instanceof VoterInformationActivity) {
                            ((VoterInformationView) getActivity()).navigateBack();
                        }
                    }
                });

                mToolbar.setOnMenuItemClickListener(this);
                mToolbar.inflateMenu(R.menu.polling_sites_map);
                mToolbar.getMenu().findItem(mPresenter.getCurrentSort()).setChecked(true);

                //Remove any sorts that are missing locations
                if (!mPresenter.hasPollingLocations()) {
                    mToolbar.getMenu().removeItem(R.id.sort_polling_locations);
                }

                if (!mPresenter.hasEarlyVotingLocations()) {
                    mToolbar.getMenu().removeItem(R.id.sort_early_vote);
                }

                if (!mPresenter.hasDropBoxLocations()) {
                    mToolbar.getMenu().removeItem(R.id.sort_drop_boxes);
                }
            }
        }
    }

    /**
     * Helper function to add everything that isn't a polling site to the map
     */
    private void addNonPollingToMap() {
        // add marker for user-entered address
        if (homeLocation != null) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(UserPreferences.getHomeLatLong())
                    .title(getContext().getString(R.string.locations_map_label_user_address))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_address))
            );
        }

        if (currentLocation != null) {
            // add marker for current user location (used for directions)
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(currentLocation)
                    .title(getContext().getString(R.string.locations_map_label_user_location))
                    .snippet(currentAddress)
                    .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation))
            );
        }

        if (haveElectionAdminBody) {
            // add marker for state or local election administration body
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(thisLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_leg_body_map))
            );

            marker.showInfoWindow();
            // allow for getting directions from election admin body location
        }

        if (encodedPolyline != null && !encodedPolyline.isEmpty()) {
            // show directions line on map
            PolylineOptions polylineOptions = new PolylineOptions();
            List<LatLng> pts = PolyUtil.decode(encodedPolyline);
            polylineOptions.addAll(pts);
            polylineOptions.color(getContext().getResources().getColor(R.color.brand));
            map.addPolyline(polylineOptions);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            locationId = args.getString(LOCATION_ID);
            encodedPolyline = args.getString(POLYLINE);
        }
    }

    private MarkerOptions createMarkerOptions(PollingLocation location, @DrawableRes int drawable) {
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(location.address.latitude, location.address.longitude))
                .icon(BitmapDescriptorFactory.fromResource(drawable));

        return options;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        mPresenter.menuItemClicked(item.getItemId());
        item.setChecked(true);

        return false;
    }

    @Override
    public void navigateToDirections(PollingLocation pollingLocation) {

    }

    @Override
    public void navigateToErrorForm() {

    }

    @Override
    public void navigateToMap(@LayoutRes int currentSort) {
        //Not implemented
    }

    @Override
    public void navigateToList(@LayoutRes int currentSort) {
        mListener.listButtonClicked(currentSort);
    }

    @Override
    public void updateList(ArrayList<PollingLocation> locations) {
        if (map != null) {
            map.clear();
            updateMapWithLocations(locations);
            addNonPollingToMap();
        }
    }

    private void updateMapWithLocations(ArrayList<PollingLocation> locations) {
        mappedPollingLocations = new HashMap<>();

        for (PollingLocation location : locations) {
            MarkerOptions markerOptions;
            switch (location.pollingLocationType) {
                case PollingLocation.POLLING_TYPE_DROP_BOX:

                    markerOptions = createMarkerOptions(location, R.drawable.ic_marker_drop_box);

                    break;
                case PollingLocation.POLLING_TYPE_EARLY_VOTE:
                    markerOptions = createMarkerOptions(location, R.drawable.ic_marker_early_voting);

                    break;
                case PollingLocation.POLLING_TYPE_LOCATION:
                default:
                    markerOptions = createMarkerOptions(location, R.drawable.ic_marker_poll);

                    break;
            }

            markerOptions.title(location.address.locationName);

            if (location.address != null) {
                markerOptions.position(location.getLatLongLocation());
                Marker marker = map.addMarker(markerOptions);

                mappedPollingLocations.put(marker, location);
            }
        }
    }

    @Override
    public void resetView() {
        if (polylineBounds != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(polylineBounds, 60));
        } else if (thisLocation != null) {
            // zoom to selected location
            if (thisLocation == homeLocation) {
                // move out further when viewing general map centered on home
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(thisLocation, 8));
            } else {
                // move to specific polling location or other point of interest
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(thisLocation, 15));
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //TODO handle layout here
        //Unselect all markers
        //select current marker

//        marker.setIcon();

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //TODO enable my location in M
//            map.setMyLocationEnabled(true);

        setupMapView(googleMap);

        updateMapWithLocations(mPresenter.getSortedLocations());

        addNonPollingToMap();

        //This will be the same as reset View, but not animated
        if (polylineBounds != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(polylineBounds, 60));
        } else if (thisLocation != null) {
            // zoom to selected location
            if (thisLocation == homeLocation) {
                // move out further when viewing general map centered on home
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(thisLocation, 8));
            } else {
                // move to specific polling location or other point of interest
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(thisLocation, 15));
            }
        }
    }

    // set click handler for info window (to go to directions list)
    // info window is just a bitmap, so can't listen for clicks on elements within it.
    private void setupMapView(GoogleMap map) {
        map.setOnMarkerClickListener(this);
        map.getUiSettings().setMapToolbarEnabled(false);

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
//TODO do this
            }
        });
    }
}
