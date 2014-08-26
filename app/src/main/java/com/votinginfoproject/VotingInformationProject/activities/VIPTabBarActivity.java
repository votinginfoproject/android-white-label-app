package com.votinginfoproject.VotingInformationProject.activities;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.asynctasks.DirectionsQuery;
import com.votinginfoproject.VotingInformationProject.asynctasks.ReverseGeocodeQuery;
import com.votinginfoproject.VotingInformationProject.fragments.BallotFragment;
import com.votinginfoproject.VotingInformationProject.fragments.CandidateFragment;
import com.votinginfoproject.VotingInformationProject.fragments.ContestFragment;
import com.votinginfoproject.VotingInformationProject.fragments.DirectionsFragment;
import com.votinginfoproject.VotingInformationProject.fragments.ElectionDetailsFragment;
import com.votinginfoproject.VotingInformationProject.fragments.LocationsFragment;
import com.votinginfoproject.VotingInformationProject.fragments.VIPMapFragment;
import com.votinginfoproject.VotingInformationProject.asynctasks.GeocodeQuery;
import com.votinginfoproject.VotingInformationProject.models.CivicApiAddress;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.State;
import com.votinginfoproject.VotingInformationProject.models.VIPApp;
import com.votinginfoproject.VotingInformationProject.models.VIPAppContext;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.googledirections.Bounds;

public class VIPTabBarActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, DirectionsQuery.PolylineCallBackListener  {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    TabsAdapter mTabsAdapter;
    FragmentManager mFragmentManager;
    VIPAppContext mAppContext;
    GeocodeQuery.GeocodeCallBackListener pollingCallBackListener;
    GeocodeQuery.GeocodeCallBackListener homeCallBackListener;
    GeocodeQuery.GeocodeCallBackListener adminBodyCallBackListener;
    ElectionAdministrationBody stateAdmin;
    ElectionAdministrationBody localAdmin;
    ArrayList<PollingLocation> allLocations;
    VoterInfo voterInfo;
    HashMap<String, Integer> locationIds;
    Location homeLocation;
    LocationsFragment locationsFragment;
    DirectionsFragment directionsFragment;
    Context context;
    boolean useMetric;
    LocationClient mLocationClient;
    ReverseGeocodeQuery.ReverseGeocodeCallBackListener reverseGeocodeCallBackListener;
    int selectedOriginItem = 0; // item selected from prompt for directions origin; 0 for user-entered address

    public String getUserLocationAddress() {
        return userLocationAddress;
    }

    String userLocationAddress;

    public LatLng getUserLocation() {
        return userLocation;
    }

    LatLng userLocation;
    String encodedDirectionsPolyline = "";

    public LatLngBounds getPolylineBounds() {
        return polylineBounds;
    }

    LatLngBounds polylineBounds;

    // activity identifier
    static final int PROMPT_ENABLE_LOCATION_SERVICES = 1;

    /**
     * Non-default constructor for testing, to set the application context.
     * @param context Mock context with a VoterInfo object
     */
    public VIPTabBarActivity(VIPAppContext context) {
        super();
        mAppContext = context;
    }

    public VIPTabBarActivity() {
        super();
    }

    public VIPAppContext getAppContext() {
        return mAppContext;
    }

    public PollingLocation getLocationForId(String location_id) {
        if (locationIds.get(location_id) != null) {
            return allLocations.get(locationIds.get(location_id));
        } else {
            Log.e("VIPTabBarActivity", "Did not find ID in hash: " + location_id);
            return null;
        }
    }

    /**
     * Helper function to return address object for either polling location or election admin body.
     * @param location_id Key for polling location, or which admin body
     * @return Address object for key
     */
    public CivicApiAddress getAddressForId(String location_id) {
        if (locationIds.get(location_id) != null) {
            PollingLocation location = allLocations.get(locationIds.get(location_id));
            return location.address;
        } else if (location_id.equals(ElectionAdministrationBody.AdminBody.STATE) && stateAdmin != null) {
            return stateAdmin.physicalAddress;
        } else if (location_id.equals(ElectionAdministrationBody.AdminBody.LOCAL) && localAdmin != null) {
            return localAdmin.physicalAddress;
        } else {
            Log.e("VIPTabBarActivity", "Did not find ID in hash: " + location_id);
            return null;
        }
    }

    /**
     * Helper function to return descriptor for either polling location or election admin body.
     * @param location_id Key for polling location, or which admin body
     * @return String descriptor for key
     */
    public String getDescriptionForId(String location_id) {
        if (locationIds.get(location_id) != null) {
            PollingLocation location = allLocations.get(locationIds.get(location_id));
            if (location.name != null) {
                return location.name;
            }
        if (location_id.equals(ElectionAdministrationBody.AdminBody.STATE)) {
            if (stateAdmin!= null && stateAdmin.name != null) {
                return stateAdmin.name;
            }
        } else if (location_id.equals(ElectionAdministrationBody.AdminBody.LOCAL)) {
            if (localAdmin != null && localAdmin.name != null) {
                return localAdmin.name;
            }
        }
        }

        return "";
    }

    public String getHomeAddress() {
        return voterInfo.normalizedInput.toGeocodeString();
    }

    @Override
    public void polylineCallback(String polyline, Bounds bounds) {
        // got encoded polyline for the directions overview; set it on the Activity for the map to find
        encodedDirectionsPolyline = polyline;

        // get polyline bounds in format map can use
        if (bounds != null) {
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(new LatLng(bounds.northeast.lat, bounds.northeast.lng));
            boundsBuilder.include(new LatLng(bounds.southwest.lat, bounds.southwest.lng));
            polylineBounds = boundsBuilder.build();
        } else {
            polylineBounds = null;
        }
    }

    /**
     * Transition from ballot fragment to contest details fragment when user selects list item.
     *
     * @param position Index of selected contest within the VoterInfo object's list of contests
     */
    public void showContestDetails(int position) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment contestFragment = ContestFragment.newInstance(position);

        // Ballot fragment is not actually removed by `replace` call here, because it's in a tab bar.
        // Contest fragment will hide the ballot fragment components, then show them again
        // when user navigates back.
        fragmentTransaction.replace(R.id.ballot_fragment, contestFragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

	/**
     * Transition from contest fragment to candidate fragment when user selects list item.
     *
     * @param contestPosition Index of selected contest within the VoterInfo object's list of contests
     * @param candidatePosition Index of selected candidate within the contest's list of candidates
     */
    public void showCandidateDetails(int contestPosition, int candidatePosition) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment candidateFragment = CandidateFragment.newInstance(contestPosition, candidatePosition);

        // See showContestDetails for info on how the fragment transition occurs
        fragmentTransaction.replace(R.id.ballot_fragment, candidateFragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void showDirections(String item) {
        Log.d("VIPTabBarActivity", "Going to show directions to " + item + "...");
        // first ask user where to get directions from:  entered address, or current location?
        promptForDirectionsOrigin(item);
    }

    private void promptForDirectionsOrigin(final String item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(R.string.tabbar_directions_origin_prompt);

        // persist user's last choice in list by passing selectedOriginItem as the selected index
        builder.setSingleChoiceItems(R.array.tabbar_directions_origin_options, selectedOriginItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("VIPTabBarActivity", "You selected: " + which);
                selectedOriginItem = which;
            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // user clicked ok
                Log.d("VIPTabBarActivity", "Final selection: " + selectedOriginItem);

                // flag to tell directions which origin to use
                boolean use_location = false;
                if (selectedOriginItem > 0) {
                    use_location = true;
                }

                dialog.dismiss();

                // show directions fragment now we have an answer
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                directionsFragment = DirectionsFragment.newInstance(item, use_location);
                // have to hide/reshow parent within DirectionsFragment, as replace doesn't actually replace
                if (item.equals(ElectionAdministrationBody.AdminBody.STATE) || item.equals(ElectionAdministrationBody.AdminBody.LOCAL)) {
                    // got here from details tab
                    fragmentTransaction.replace(R.id.election_details_fragment, directionsFragment);
                } else {
                    // got here from locations tab
                    fragmentTransaction.replace(R.id.locations_list_fragment, directionsFragment);
                }
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // user cancelled; do not show directions
                Log.d("VIPTabBarActivity", "User cancelled dialog.");
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean playServicesAvailable() {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
            Log.d("VIPTabBarActivity", "Google Play services are available!");
            return true;
        } else {
            Log.e("VIPTabBarActivity", "Google Play services are unavailable!");
            // alert user
            CharSequence errorMessage = context.getResources().getText(R.string.locations_map_play_services_unavailable);
            Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
    }

    public void showMap(String destinationLocationIndex) {
        // make sure Google Play services are available first
        if (!playServicesAvailable()) {
            return;
        }

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        SupportMapFragment mapFragment = VIPMapFragment.newInstance(destinationLocationIndex, encodedDirectionsPolyline);
        if (destinationLocationIndex.equals(ElectionAdministrationBody.AdminBody.STATE) ||
                destinationLocationIndex.equals(ElectionAdministrationBody.AdminBody.LOCAL)) {
            // got here from details tab
            fragmentTransaction.replace(R.id.election_details_fragment, mapFragment);
        } else {
            // got here from locations tab
            fragmentTransaction.replace(R.id.locations_list_fragment, mapFragment);
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public VoterInfo getVoterInfo() {
        return mAppContext.getVIPApp().getVoterInfo();
    }

    public LatLng getHomeLatLng() {
        if (homeLocation != null) {
            return new LatLng(homeLocation.getLatitude(), homeLocation.getLongitude());
        }
        return null;
    }

    public LatLng getAdminBodyLatLng(String body) {
        if (body.equals(ElectionAdministrationBody.AdminBody.STATE)) {
            if (stateAdmin != null) {
                return new LatLng(stateAdmin.physicalAddress.latitude, stateAdmin.physicalAddress.longitude);
            }
        } else if (body.equals(ElectionAdministrationBody.AdminBody.LOCAL)) {
            if (localAdmin != null) {
                return new LatLng(localAdmin.physicalAddress.latitude, localAdmin.physicalAddress.longitude);
            }
        }

        return null;
    }

    public String getAdminBodyAddress(String body) {
        if (body.equals(ElectionAdministrationBody.AdminBody.STATE)) {
            if (stateAdmin != null) {
                return stateAdmin.getPhysicalAddress();
            }
        } else if (body.equals(ElectionAdministrationBody.AdminBody.LOCAL)) {
            return localAdmin.getPhysicalAddress();
        }

        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viptab_bar);

        mFragmentManager = getSupportFragmentManager();
        mAppContext = new VIPAppContext((VIPApp) getApplicationContext());
        context = VIPApp.getContext();

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up ViewPager
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.pager);
        setContentView(mViewPager);

        // Set up TabsAdapter
        mTabsAdapter = new TabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(actionBar.newTab().setText(R.string.tabbar_ballot_tab), BallotFragment.class, "ballot_tab", null);
        mTabsAdapter.addTab(actionBar.newTab().setText(R.string.tabbar_where_to_vote_tab), LocationsFragment.class, "locations_tab", null);
        mTabsAdapter.addTab(actionBar.newTab().setText(R.string.tabbar_details_tab), ElectionDetailsFragment.class, "details_tab", null);

        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }

        setUpGeocodings();

        mLocationClient = new LocationClient(this, this, this);
    }

    /**
     * Helper function to geocode entered address and the polling location results for that address
     * when the tab bar is loaded.
     */
    private void setUpGeocodings() {
        // get LocationsFragment's root view
        locationsFragment = (LocationsFragment)mTabsAdapter.getItem(1);
        voterInfo = getVoterInfo();
        setAllLocations();
        setLocationIds();

        useMetric = mAppContext.useMetric();

        // set up callback listener for reverse-geocode address result
        reverseGeocodeCallBackListener = new ReverseGeocodeQuery.ReverseGeocodeCallBackListener() {
            @Override
            public void callback(String address) {
                Log.d("HomeActivity", "Got reverse-geocoded address " + address);
                if (address != null && !address.isEmpty()) {
                    userLocationAddress = address;
                } else {
                    Log.e("HomeActivity", "Got empty address result!");
                    userLocationAddress = "";
                }
            }
        };

        // Callback for polling location geocode result
        pollingCallBackListener = new GeocodeQuery.GeocodeCallBackListener() {

            @Override
            public void callback(String key, double lat, double lon, double distance) {
                if (key.equals("error")) {
                    Log.e("VIPTabBarActivity", "Geocode failed!");
                    return;
                }

                // find object and set values on it
                PollingLocation foundLoc = allLocations.get(locationIds.get(key));
                foundLoc.address.latitude = lat;
                foundLoc.address.longitude = lon;
                foundLoc.address.distance = distance;
            }
        };

        // callback for home address geocode result
        homeCallBackListener = new GeocodeQuery.GeocodeCallBackListener() {
            @Override
            public void callback(String key, double lat, double lon, double distance) {
                if (key.equals("error")) {
                    Log.e("VIPTabBarActivity", "Failed to geocode home address!");
                    return;
                }

                homeLocation = new Location("home");
                homeLocation.setLatitude(lat);
                homeLocation.setLongitude(lon);
                mAppContext.setHomeLocation(homeLocation);

                // start background geocode tasks for polling locations
                for (PollingLocation location : allLocations) {
                    // key by address, if location has no ID
                    if (location.id != null) {
                        new GeocodeQuery(context, pollingCallBackListener, location.id,
                                location.address.toGeocodeString(), homeLocation, useMetric, null).execute();
                    } else {
                        new GeocodeQuery(context, pollingCallBackListener, location.address.toGeocodeString(),
                                location.address.toGeocodeString(), homeLocation, useMetric, null).execute();
                    }
                }

                // start background geocode tasks for election admin bodies' physical addresses
                State state = voterInfo.state.get(0);
                stateAdmin = state.electionAdministrationBody;
                if (stateAdmin != null) {
                    if (!stateAdmin.getPhysicalAddress().isEmpty()) {
                        new GeocodeQuery(context, adminBodyCallBackListener, ElectionAdministrationBody.AdminBody.STATE,
                                stateAdmin.physicalAddress.toGeocodeString(), homeLocation, useMetric, null).execute();
                    }
                }

                if (state.local_jurisdiction != null && state.local_jurisdiction.electionAdministrationBody != null) {
                    localAdmin = state.local_jurisdiction.electionAdministrationBody;
                    if (!localAdmin.getPhysicalAddress().isEmpty()) {
                        new GeocodeQuery(context, adminBodyCallBackListener, ElectionAdministrationBody.AdminBody.LOCAL,
                                localAdmin.physicalAddress.toGeocodeString(), homeLocation, useMetric, null).execute();
                    }
                }
            }
        };

        // Callback for election administration body address geocode result
        adminBodyCallBackListener = new GeocodeQuery.GeocodeCallBackListener() {
            @Override
            public void callback(String key, double lat, double lon, double distance) {
                if (key.equals("error")) {
                    Log.e("VIPTabBarActivity", "Failed to geocode administrative body physical address!");
                    return;
                }

                try {
                    CivicApiAddress address = null;
                    if (key.equals(ElectionAdministrationBody.AdminBody.STATE)) {
                        address = stateAdmin.physicalAddress;
                    } else if (key.equals(ElectionAdministrationBody.AdminBody.LOCAL)) {
                        address = localAdmin.physicalAddress;
                    }

                    address.latitude = lat;
                    address.longitude = lon;
                    address.distance = distance;

                } catch (Exception ex) {
                    Log.e("VIPTabBarActivity", "Failed to set geocode result on election admin body!");
                    ex.printStackTrace();
                }
            }
        };

        if (playServicesAvailable()) {
            // geocode home address; once result returned, geocode polling and admin body locations
            new GeocodeQuery(context, homeCallBackListener, "home", voterInfo.normalizedInput.toGeocodeString(),
                    null, useMetric, null).execute();
        }
    }

    private void setAllLocations() {
        // get all locations (both polling and early voting)
        allLocations = new ArrayList<PollingLocation>();
        if (voterInfo.pollingLocations != null) {
            allLocations.addAll(voterInfo.pollingLocations);
        }

        if (voterInfo.earlyVoteSites != null) {
            allLocations.addAll(voterInfo.earlyVoteSites);
        }
    }

    public ArrayList<PollingLocation> getAllLocations() {
        return allLocations;
    }

    /**
     * Build map of PollingLocation id to its offset in the list of all locations,
     * to find it later when the distance calculation comes back.
     */
    private void setLocationIds() {
        locationIds = new HashMap<String, Integer>(allLocations.size());
        for (int i = allLocations.size(); i--> 0;) {
            PollingLocation location = allLocations.get(i);
            if (location.id != null) {
                locationIds.put(location.id, i);
            } else {
                locationIds.put(location.address.toGeocodeString(), i);
            }
        }
    }

    public LatLng getUserLocation(boolean showPrompt) {
        // check for play services first
        if (!playServicesAvailable()) {
            return null;
        }

        if (!mLocationClient.isConnected()) {
            // location services aren't ready yet (maybe just turned on)
            // wait for onConnected() to be called, then try again
            if (!mLocationClient.isConnecting()) {
                Log.d("VIPTabBarActivity", "Location client not connected; try connecting...");
                mLocationClient.disconnect(); // call connect from disconnect
                return null;
            } else {
                Log.d("VIPTabBarActivity", "Location client is connecting...");
                return null;
            }
        }

        Location currentLocation = mLocationClient.getLastLocation();
        if (currentLocation != null) {
            userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Log.d("HomeActivity", "Current location is: " + currentLocation.getLatitude() + "," + currentLocation.getLongitude());
            // now go reverse-geocode to find address for current location
            userLocationAddress = "";
            new ReverseGeocodeQuery(reverseGeocodeCallBackListener).execute(currentLocation);
            return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            Log.e("HomeActivity", "Current location not found!  Are Location services enabled?");
            userLocation = null;

            if (showPrompt) {
                // user has probably disabled Location services; prompt them to go turn it on
                final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
                builder.setMessage(R.string.tabbar_enable_location_services_prompt);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, PROMPT_ENABLE_LOCATION_SERVICES);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.create().show();
            }
        }

        return null;
    }

    /**
     * Helper function to retry location services check.  Calls back to directions fragment if found.
     */
    private void retryGetDirections() {
        if (selectedOriginItem == 0) {
            Log.e("VIPTabBarActivity", "No longer using current location for directions!  Ignoring.");
            return;
        }

        LatLng location = getUserLocation(false);
        if (location != null) {
            // have successfully gotten location after prompting to enable location services
            // call back to directions fragment to try getting directions now

            if (directionsFragment != null) {
                directionsFragment.setUpWithOrigin(location);
            } else {
                Log.e("VIPTabBarActivity", "Could not find directions fragment!");
            }
        } else {
            Log.d("VIPTabBarActivity", "Location is still null");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PROMPT_ENABLE_LOCATION_SERVICES) {
            // User got back from location services screen; try to get their location again.
            // Do not prompt them again to enable location services.
           retryGetDirections();
        } else {
            Log.e("VIPTabBarActivity", "Got result for unrecognized activity!");
        }
    }

    /**
     * Called when Activity becomes visible
     */
    @Override
    protected void onStart() {
        super.onStart();
        // connect to location service
        mLocationClient.connect();
    }

    /**
     * Disconnect location client when app is no longer going to be visible
     */
    @Override
    protected void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("VIPTabBarActivity", "Location services connected.");
        retryGetDirections(); // try to get found location to directions fragment
    }

    @Override
    public void onDisconnected() {
        Log.d("VIPTabBarActivity", "Location services disconnected; try connecting.");
        mLocationClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("VIPTabBarActivity", "Location services failed.");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.viptab_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Navigate to HomeActivity from main TabBar
            NavUtils.navigateUpFromSameTask(this);
        }
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between pages.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct paged in the ViewPager whenever the selected
     * tab changes.
     *
     * NOTE: This class is lifted directly from the ViewPager class docs:
     *       http://developer.android.com/reference/android/support/v4/view/ViewPager.html
     */
    public static class TabsAdapter extends FragmentPagerAdapter
            implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>(3);
        private LocationsFragment locationsFragment;
        private FragmentManager tabsFragmentManager;

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }

        public TabsAdapter(FragmentActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mActionBar = activity.getActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, String tag, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(tag);
            tab.setTabListener(this);
            mTabs.add(info);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            // return the proper Fragment type given the tab
            switch (position) {
                case 0: {
                    return BallotFragment.newInstance();
                }
                case 1: {
                    locationsFragment = LocationsFragment.newInstance();
                    return locationsFragment;
                }
                case 2: {
                    return ElectionDetailsFragment.newInstance();
                }
                default: {
                    Log.e("VIPTabBarActivity", "GETTING DEFAULT FRAGMENT FOR POSITION " + position);
                    return LocationsFragment.newInstance();
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            // Clear back stack on tab change.  Otherwise stack will keep history for all tabs,
            // which can result in inconsistent state and/or unexpected behavior.
            tabsFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        /**
         * tell polling locations list to refresh when switching to that tab, to get the
         * geocoding results have returned since its fragment was created
         * (re-select is always triggered on select)
         */
        @Override
        public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            if (tab.getTag() == "locations_tab") {
                locationsFragment.refreshList();
            }
        }
    }
}
