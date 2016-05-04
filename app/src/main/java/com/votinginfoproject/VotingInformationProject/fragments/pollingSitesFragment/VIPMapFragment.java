package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity.VoterInformationActivity;
import com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity.VoterInformationView;
import com.votinginfoproject.VotingInformationProject.application.LocationPermissions;
import com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment.BottomNavigationFragment;
import com.votinginfoproject.VotingInformationProject.models.CivicApiAddress;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.singletons.VoterInformation;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.PollingSiteViewHolder;

import java.util.ArrayList;

public class VIPMapFragment extends MapFragment implements Toolbar.OnMenuItemClickListener,
        PollingSitesView,
        BottomNavigationFragment,
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback,
        View.OnClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnCameraChangeListener {
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
    String homeAddress;
    String encodedPolyline;
    LatLngBounds polylineBounds;
    boolean haveElectionAdminBody;

    private PollingSitesListFragment.PollingSitesListener mListener;

    private PollingSitesPresenter mPresenter;

    private PollingSiteViewHolder mBottomCardViewHolder;

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

        if (context instanceof PollingSitesListFragment.PollingSitesListener) {
            mListener = (PollingSitesListFragment.PollingSitesListener) context;
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

        allLocations = VoterInformation.getAllPollingLocations();

        homeLocation = VoterInformation.getHomeAddress().getLocation();

        //TODO get current location
//        currentLocation = mActivity.getUserLocation();
//        currentAddress = mActivity.getUserLocationAddress();

        homeAddress = VoterInformation.getHomeAddress().toGeocodeString();

        //TODO rework how we are getting polylines
//        polylineBounds = mActivity.getPolylineBounds();

        // check if this map view is for an election administration body
        if (locationId.equals(ElectionAdministrationBody.AdminBody.STATE) ||
                locationId.equals(ElectionAdministrationBody.AdminBody.LOCAL)) {
            haveElectionAdminBody = true;
        } else {
            haveElectionAdminBody = false;
        }

        //TODO Send in This Location instead of using hashmap
        // set selected location to zoom to
        if (locationId.equals(HOME)) {
            thisLocation = homeLocation;
        } else if (locationId.equals(ElectionAdministrationBody.AdminBody.STATE)) {
            //TODO rework this
            if (VoterInformation.getStateAdministrationBody() != null) {
                thisLocation = VoterInformation.getStateAdministrationBody().getPhysicalAddress().getLocation();
            }

        } else if (locationId.equals(ElectionAdministrationBody.AdminBody.LOCAL)) {
            if (VoterInformation.getLocalAdministrationBody() != null) {
                thisLocation = VoterInformation.getLocalAdministrationBody().getPhysicalAddress().getLocation();
            }
        } else {
            Log.d(TAG, "Have location ID: " + locationId);

            //TODO rework this
//            selectedLocation = VoterInformation.getVoterInfoResponse().getLocationForId(locationId);

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

                //If there aren't any polling locations
                if (!mPresenter.hasPollingLocations() &&
                        !mPresenter.hasEarlyVotingLocations() &&
                        !mPresenter.hasDropBoxLocations()) {
                    mToolbar.getMenu().removeGroup(R.id.filter_polling_sites);
                } else {
                    mToolbar.getMenu().findItem(mPresenter.getCurrentSort()).setChecked(true);
                }
            }

            View mBottomCardLayout = view.findViewById(R.id.bottom_card_container);
            mBottomCardLayout.measure(0, 0);
            mBottomCardLayout.setTranslationY(mBottomCardLayout.getMeasuredHeight());
            mBottomCardLayout.bringToFront();

            mBottomCardViewHolder = new PollingSiteViewHolder(mBottomCardLayout);
            mBottomCardViewHolder.getView().setOnClickListener(this);
        }
    }

    public void attemptToGetLocation() {
        //Check for location Permissions
        if (LocationPermissions.granted(getContext())) {
            //Do location stuff
            Log.e(TAG, "Location granted!!");
            if (map != null) {
                mListener.startPollingLocation();

                try {
                    map.setMyLocationEnabled(true);

                } catch(SecurityException ex) {
                    Log.wtf(TAG, "Location permissions appear granted, but still encountered exception.");
                }
            }
        } else if (!LocationPermissions.grantedForApplication(getContext())) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            attemptToGetLocation();
        } else {
            //Show error
            Snackbar.make(mBottomCardViewHolder.getView(), R.string.activity_vip_tab_enable_location_services_prompt, Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);

        Bundle args = getArguments();
        if (args != null) {
            locationId = args.getString(LOCATION_ID);
            encodedPolyline = args.getString(POLYLINE);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        mPresenter.menuItemClicked(item.getItemId());
        item.setChecked(true);

        hideLocationCard();

        return false;
    }

    @Override
    public void navigateToDirections(PollingLocation pollingLocation) {
        if (mListener != null) {
            mListener.navigateToDirections(pollingLocation);
        }
    }

    @Override
    public void navigateToErrorForm() {
        //Not implemented
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
            mPresenter.onMapNeedsLayout(map);
        }
    }

    @Override
    public void showLocationCard(PollingLocation location) {
        if (mBottomCardViewHolder.getView().getTranslationY() > 0) {
            mBottomCardViewHolder.setPollingLocation(getContext(), location);

            //Card not shown
            mBottomCardViewHolder.getView().setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mBottomCardViewHolder.getView(), "translationY", mBottomCardViewHolder.getView().getTranslationY(), 0);
            animator.setDuration(300);
            animator.start();
        } else {
            mBottomCardViewHolder.setPollingLocation(getContext(), location, true);
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
        return mPresenter.mapMarkerClicked(map, marker);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        attemptToGetLocation();

        setupMapView(googleMap);
        mPresenter.onMapNeedsLayout(googleMap);

        resetView();

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

        //Override default options so we can implement on click and show our own info window.
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //Empty
            }
        });

        //Brings Selected Map Pin to the front without showing a view
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return new View(getContext());
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        map.setOnMapClickListener(this);

        map.setOnCameraChangeListener(this);
    }

    private void hideLocationCard() {
        mPresenter.clearSelectedMapMarkers();

        mBottomCardViewHolder.getView().measure(0, 0);
        int height = mBottomCardViewHolder.getView().getMeasuredHeight();

        ObjectAnimator hideCardAnimator = ObjectAnimator.ofFloat(mBottomCardViewHolder.getView(), "translationY", mBottomCardViewHolder.getView().getTranslationY(), height);
        hideCardAnimator.setDuration(300);
        hideCardAnimator.start();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBottomCardViewHolder.getView())) {
            mPresenter.pollingItemSelected();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        hideLocationCard();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        hideLocationCard();
    }
}
