package com.votinginfoproject.VotingInformationProject.activities.directionsActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

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
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Leg;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Location;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Step;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.TabData;

import java.util.Locale;

public class DirectionsActivity extends BaseActivity<DirectionsPresenter> implements DirectionsView,
        TabLayout.OnTabSelectedListener,
        OnMapReadyCallback,
        Toolbar.OnMenuItemClickListener {
    private static final String TAG = DirectionsActivity.class.getSimpleName();

    public static final String ARG_LOCATION_DESTINATION = "Location_destination";

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

    private MapView mMapView;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_directions);

        PollingLocation pollingLocation = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pollingLocation = extras.getParcelable(ARG_LOCATION_DESTINATION);
        }

        if (getPresenter() == null) {
            setPresenter(new DirectionsPresenterImpl(this, pollingLocation));
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

        mLoadingView = findViewById(R.id.loading_view);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.animate();

        mErrorView = findViewById(R.id.error_view);

        Button retryButton = (Button) mErrorView.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().retryButtonPressed();
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
        if (mTabLayout != null) {
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
    public void showRouteOnMap(Route route, @DrawableRes int destinationMarkerRes) {
        if (mMap == null) {
            return;
        }

        mMap.clear();

        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(ContextCompat.getColor(getApplicationContext(), R.color.background_blue));

        for (int legIdx = 0; legIdx < route.legs.size(); legIdx ++) {
            Leg leg = route.legs.get(legIdx);

            for (int stepIdx = 0; stepIdx < leg.steps.size(); stepIdx++) {
                Step step = leg.steps.get(stepIdx);

                //Add the first point to the polyline as the start location of the first step
                if (legIdx == 0 && stepIdx == 0) {
                    Location startLocation = step.start_location;
                    polylineOptions.add(new LatLng(startLocation.lat, startLocation.lng));
                }

                Location endLocation = step.end_location;
                polylineOptions.add(new LatLng(endLocation.lat, endLocation.lng));
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

        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 20);
        mMap.animateCamera(update);

        int numPoints = polylineOptions.getPoints().size();
        if (numPoints > 0 && destinationMarkerRes > 0) {
            LatLng lastPoint = polylineOptions.getPoints().get(numPoints - 1);

            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(destinationMarkerRes);

            MarkerOptions pollMarker = new MarkerOptions().position(lastPoint).icon(bitmapDescriptor);
            mMap.addMarker(pollMarker);
        }
    }

    @Override
    public void navigateToExternalMap(String address) {
        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", address);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @Override
    public void toggleLoading(boolean loading) {
        float toAlpha = loading ? 1f : 0f;

        if (loading) {
            mProgressBar.animate();
        }

        mLoadingView.animate()
                .alpha(toAlpha)
                .setDuration(fade_duration);
    }

    @Override
    public void toggleError(final boolean error) {
        float toAlpha = error ? 1f : 0f;

        if (error) {
            mErrorView.setVisibility(View.VISIBLE);
        }

        mErrorView.animate()
                .alpha(toAlpha)
                .setDuration(fade_duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!error) {
                            mErrorView.setVisibility(View.GONE);
                        }
                    }
                });
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
        //Required empty override method
    }

    //GoogleMaps OnMapReadyCallback
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            mMap.setMyLocationEnabled(true);
        }
        getPresenter().onMapReady();
    }
}
