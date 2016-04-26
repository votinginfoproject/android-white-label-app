package com.votinginfoproject.VotingInformationProject.activities.directionsActivity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.geometry.Bounds;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.constants.ExtraConstants;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Leg;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Location;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Step;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.TabData;
import com.votinginfoproject.VotingInformationProject.models.singletons.VoterInformation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DirectionsActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener, DirectionsView, OnMapReadyCallback {
    private static int selected_alpha = 255;
    private static int unselected_alpha = (int) (255 * 0.6);

    private static String ARG_CIVIC_ADDRESS = "arg_civic_address";
    private static String ARG_CURRENT_LOCATION = "arg_current_location";
    private static String ARG_POLLING_LOCATION = "arg_polling_location";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private TabLayout mTabLayout;
    private DirectionsViewPagerAdapter mAdapter;
    private int mMenuLayoutID = R.menu.menu_directions_list;
    private MapView mMapView;
    private GoogleMap mMap;

    public DirectionsPresenter mPresenter;
    public ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        Location destination = null;

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            PollingLocation pollingLocation = (PollingLocation) extras.getParcelable(ExtraConstants.LOCATION_DESTINATION);
            destination = pollingLocation.location;
        }

        mPresenter = new DirectionsPresenterImpl(this, destination);
        mPresenter.setView(this);

        mAdapter = new DirectionsViewPagerAdapter(getFragmentManager(), mPresenter);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPresenter.swipedToDirectionsListAtIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
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
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(mMenuLayoutID, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_map_toggle:
                mPresenter.mapButtonPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Log.v("here", "jere");

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getIcon() != null) {
            tab.getIcon().setAlpha(selected_alpha);

            int tabIndex = mTabLayout.getSelectedTabPosition();
            if (tabIndex >= 0) {
                mPresenter.tabSelectedAtIndex(tabIndex);
            }
        }

//Determine if list is showing or map is showing, then update appropriately
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        if (tab.getIcon() != null) {
            tab.getIcon().setAlpha(unselected_alpha);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
//Reset View
    }

    @Override
    public void refreshDataView() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectTabAtIndex(int index) {
        TabLayout.Tab tab = mTabLayout.getTabAt(index);
        if (tab != null) {
            tab.select();
        }
    }

    @Override
    public void navigateToDirectionsListAtIndex(int index) {
        mViewPager.setCurrentItem(index);
    }

    @Override
    public void setTabs(TabData[] tabs) {
        mTabLayout.removeAllTabs();

        if (tabs.length > 0) {
            mTabLayout.setVisibility(View.VISIBLE);
            for (TabData tabData : tabs) {
                TabLayout.Tab tab = mTabLayout.newTab();
                tab.setIcon(tabData.drawableID);
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
        View invisibleView = displaying ? mViewPager : mMapView;
        View visibleView = displaying ? mMapView : mViewPager;

        invisibleView.setVisibility(View.INVISIBLE);
        visibleView.setVisibility(View.VISIBLE);

        mMenuLayoutID = displaying ? R.menu.menu_directions_map : R.menu.menu_directions_list;

        invalidateOptionsMenu();
    }

    @Override
    public void showRouteOnMap(Route route) {
        mMap.clear();

        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(ContextCompat.getColor(getApplicationContext(), R.color.background_blue));

        for (int legIdx = 0; legIdx < route.legs.size(); legIdx ++) {
            Leg leg = route.legs.get(legIdx);

            for (int stepIdx = 0; stepIdx < leg.steps.size(); stepIdx++) {
                Step step = leg.steps.get(stepIdx);

                if (legIdx == 0 && stepIdx == 0) {
                    Location startLocation = step.start_location;
                    polylineOptions.add(new LatLng(startLocation.lat, startLocation.lng));
                }

                Location endLocation = step.end_location;
                polylineOptions.add(new LatLng(endLocation.lat, endLocation.lng));
            }
        }

        Location northeastLocation = route.bounds.northeast;
        Location southwestLocation = route.bounds.southwest;

        LatLng northeastLatLng = new LatLng(northeastLocation.lat, northeastLocation.lng);
        LatLng southwestLatLng = new LatLng(southwestLocation.lat, southwestLocation.lng);

        LatLngBounds bounds = new LatLngBounds.Builder().include(northeastLatLng).include(southwestLatLng).build();

        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, mMapView.getWidth() / 4);
        mMap.animateCamera(update);

        mMap.addPolyline(polylineOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            mMap.setMyLocationEnabled(true);
        }
    }
}
