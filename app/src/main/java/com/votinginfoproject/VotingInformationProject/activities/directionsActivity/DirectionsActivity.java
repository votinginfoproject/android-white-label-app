package com.votinginfoproject.VotingInformationProject.activities.directionsActivity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;
import com.votinginfoproject.VotingInformationProject.fragments.directionsListFragment.DirectionsListFragment;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Leg;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Location;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Step;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.TabData;
import com.votinginfoproject.VotingInformationProject.models.singletons.VoterInformation;

import java.util.List;
import java.util.Locale;

public class DirectionsActivity extends BaseActivity<DirectionsPresenter> implements DirectionsView,
        TabLayout.OnTabSelectedListener,
        OnMapReadyCallback,
        Toolbar.OnMenuItemClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = DirectionsActivity.class.getSimpleName();

    public static final String ARG_LOCATION_DESTINATION = "Location_destination";
    public static final String ARG_USE_LAST_KNOWN_LOCATION = "Use_last_known_location";

    private static final String KEY_MAP_STATE = "Map_state";

    private static int selected_alpha = 255;
    private static int unselected_alpha = (int) (255 * 0.6);
    private static int fade_duration = 250;

    private Toolbar mToolbar;

    private TabLayout mTabLayout;

    public ViewPager mViewPager;
    private DirectionsViewPagerAdapter mAdapter;

    private View mLoadingView;
    private ProgressBar mProgressBar;

    private View mErrorView;
    private View mEnableLocationView;

    private MapView mMapView;
    private GoogleMap mMap;

    private CameraUpdate mZoomToRouteUpdate;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_directions);

        PollingLocation pollingLocation = null;
        boolean useLastKnownLocation = false;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pollingLocation = extras.getParcelable(ARG_LOCATION_DESTINATION);
            useLastKnownLocation = extras.getBoolean(ARG_USE_LAST_KNOWN_LOCATION);
        }

        if (getPresenter() == null) {
            setPresenter(new DirectionsPresenterImpl(this, pollingLocation, useLastKnownLocation));
        }

        getPresenter().onCreate(savedInstanceState);

        mAdapter = new DirectionsViewPagerAdapter(getFragmentManager(), getPresenter());

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Required empty override method
            }

            @Override
            public void onPageSelected(int position) {
                getPresenter().swipedToDirectionsListAtIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Required empty override method
            }
        });

        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.getMapAsync(this);

        Bundle mapState = null;
        if (savedInstanceState != null) {
            mapState = savedInstanceState.getBundle(KEY_MAP_STATE);
        }

        mMapView.onCreate(mapState);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        mLoadingView = findViewById(R.id.loading);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.animate();

        mErrorView = findViewById(R.id.connection_error);

        Button retryButton = (Button) mErrorView.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().retryButtonPressed();
            }
        });

        mEnableLocationView = findViewById(R.id.enable_location);

        Button locationSettingsButton = (Button) mEnableLocationView.findViewById(R.id.location_settings_button);
        locationSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().launchSettingsButtonPressed();
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_activity_directions);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.inflateMenu(R.menu.menu_directions_list);
        mToolbar.setOnMenuItemClickListener(this);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getPresenter().setView(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        mMapView.onResume();

        getPresenter().checkLocationPermissions();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Bundle mapState = new Bundle();
        mMapView.onSaveInstanceState(mapState);
        outState.putBundle(KEY_MAP_STATE, mapState);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
            case R.id.action_open_in_maps:
                getPresenter().externalMapButtonPressed();

                return true;
            case R.id.action_map_toggle:
                getPresenter().mapButtonPressed();

                return true;
        }

        return false;
    }

    @Override
    public void refreshViewData() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void selectTabAtIndex(int index) {
        if (mTabLayout != null && index != mTabLayout.getSelectedTabPosition()) {
            TabLayout.Tab tab = mTabLayout.getTabAt(index);

            if (tab != null) {
                tab.select();
            }
        }
    }

    @Override
    public void navigateToDirectionsListAtIndex(int index) {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(index);
        }
    }

    @Override
    public void navigateToAppSettings() {
        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
    }

    @Override
    public void setTabs(TabData[] tabs) {
        if (mTabLayout == null) {
            return;
        }

        mTabLayout.removeAllTabs();

        if (tabs.length > 0) {
            mTabLayout.setVisibility(View.VISIBLE);

            for (TabData tabData : tabs) {
                TabLayout.Tab tab = mTabLayout.newTab();

                tab.setIcon(tabData.drawableID);
                tab.getIcon().setAlpha(unselected_alpha);

                tab.setContentDescription(tabData.contentDescriptionID);

                mTabLayout.addTab(tab);
            }
        } else {
            mTabLayout.setVisibility(View.GONE);
        }

        mTabLayout.setOnTabSelectedListener(this);
    }

    @Override
    public void toggleMapDisplaying(boolean displaying) {
        final View invisibleView = displaying ? mViewPager : mMapView;
        final View visibleView = displaying ? mMapView : mViewPager;

        visibleView.setVisibility(View.VISIBLE);
        visibleView.setAlpha(0f);
        visibleView.animate()
                .alpha(1f)
                .setDuration(fade_duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        visibleView.setVisibility(View.VISIBLE);
                    }
                });

        invisibleView.animate()
                .alpha(0f)
                .setDuration(fade_duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        invisibleView.setVisibility(View.GONE);
                    }
                });

        int menuLayoutID = displaying ? R.menu.menu_directions_map : R.menu.menu_directions_list;
        mToolbar.getMenu().clear();
        mToolbar.inflateMenu(menuLayoutID);
    }

    @Override
    public void showRouteOnMap(Route route, @DrawableRes int destinationMarkerRes, boolean displayHomeMarker) {
        if (mMap == null) {
            return;
        }

        mMap.clear();

        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(ContextCompat.getColor(getApplicationContext(), R.color.background_blue));

        for (Leg leg : route.legs) {
            for (Step step : leg.steps) {
                List<LatLng> polylinePoints = PolyUtil.decode(step.polyline.points);

                for (LatLng point : polylinePoints) {
                    polylineOptions.add(point);
                }
            }
        }

        mMap.addPolyline(polylineOptions);

        Location northeastLocation = route.bounds.northeast;
        Location southwestLocation = route.bounds.southwest;
        LatLng northeastLatLng = new LatLng(northeastLocation.lat, northeastLocation.lng);
        LatLng southwestLatLng = new LatLng(southwestLocation.lat, southwestLocation.lng);

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(northeastLatLng)
                .include(southwestLatLng)
                .build();

        mZoomToRouteUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        mMap.animateCamera(mZoomToRouteUpdate);

        int numPoints = polylineOptions.getPoints().size();
        if (numPoints > 0) {
            if (destinationMarkerRes > 0) {
                LatLng lastPoint = polylineOptions.getPoints().get(numPoints - 1);

                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(destinationMarkerRes);

                MarkerOptions pollMarker = new MarkerOptions().position(lastPoint).icon(bitmapDescriptor);
                mMap.addMarker(pollMarker);
            }

            if (displayHomeMarker) {
                LatLng firstPoint = polylineOptions.getPoints().get(0);

                BitmapDescriptor homeMarkerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_address);

                MarkerOptions homeMarker = new MarkerOptions().position(firstPoint).icon(homeMarkerDescriptor);
                mMap.addMarker(homeMarker);
            }
        }
    }

    @Override
    public void navigateToExternalMap(String address) {
        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", address);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @Override
    public void toggleLoadingView(boolean loading) {
        float toAlpha = loading ? 1f : 0f;

        if (loading) {
            mProgressBar.animate();
        }

        mLoadingView.animate()
                .alpha(toAlpha)
                .setDuration(fade_duration);
    }

    @Override
    public void toggleEnableGlobalLocationView(boolean showing) {
        fadeView(mEnableLocationView, showing);
    }

    @Override
    public void toggleConnectionErrorView(final boolean error) {
        fadeView(mErrorView, error);
    }

    private void fadeView(final View view, final boolean showing) {
        float toAlpha = showing ? 1f : 0f;

        if (showing) {
            view.setVisibility(View.VISIBLE);
        }

        view.animate()
                .alpha(toAlpha)
                .setDuration(fade_duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!showing) {
                            view.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void attemptToGetLocation() {
        if (getPresenter().locationServicesEnabled()) {
            try {
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);
                }
            } catch (SecurityException ex) {
                Log.wtf(TAG, "Permissions error");
            }

            startPollingLocation();
        }
    }

    @Override
    public void showEnableAppLocationPrompt() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
    }

    //TabLayout.OnTabSelectedListener
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getIcon() != null) {
            tab.getIcon().setAlpha(selected_alpha);

            int tabIndex = mTabLayout.getSelectedTabPosition();
            if (tabIndex >= 0) {
                getPresenter().tabSelectedAtIndex(tabIndex);
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        if (tab.getIcon() != null) {
            tab.getIcon().setAlpha(unselected_alpha);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        getPresenter().currentTabReselected();
    }

    @Override
    public void resetView() {
        if (mMap != null && mZoomToRouteUpdate != null) {
            mMap.animateCamera(mZoomToRouteUpdate);
        }

        int currentIndex = mViewPager.getCurrentItem();

        DirectionsListFragment fragment = (DirectionsListFragment) mAdapter.instantiateItem(mViewPager, currentIndex);
        fragment.resetView();
    }

    private void startPollingLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mGoogleApiClient.reconnect();
    }

    //GoogleMaps OnMapReadyCallback
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getPresenter().onMapReady();
    }

    //GoogleAPI ConnectionCallback
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (getPresenter().locationServicesEnabled()) {
            android.location.Location lastLocation = null;

            try {
                lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } catch (SecurityException ex) {
                Log.wtf(TAG, "Failed to get location when services enabled.");
            }

            if (lastLocation != null) {
                com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Location formattedLocation =
                    new com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Location();

                formattedLocation.lat = (float) lastLocation.getLatitude();
                formattedLocation.lng = (float) lastLocation.getLongitude();

                VoterInformation.setLastKnownLocation(formattedLocation);

                getPresenter().lastKnownLocationUpdated();
            } else {
                getPresenter().checkLocationPermissions();
                //TODO decide what to do when there's a null location (location unavailable or turned off globally)
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Not implemented
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Not implemented
    }
}
