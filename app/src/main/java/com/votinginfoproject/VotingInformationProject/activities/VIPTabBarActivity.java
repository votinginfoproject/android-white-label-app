package com.votinginfoproject.VotingInformationProject.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.asynctasks.GeocodeQuery;
import com.votinginfoproject.VotingInformationProject.asynctasks.ReverseGeocodeQuery;
import com.votinginfoproject.VotingInformationProject.fragments.BallotFragment;
import com.votinginfoproject.VotingInformationProject.fragments.CandidateFragment;
import com.votinginfoproject.VotingInformationProject.fragments.ContestFragment;
import com.votinginfoproject.VotingInformationProject.fragments.DirectionsFragment;
import com.votinginfoproject.VotingInformationProject.fragments.ElectionDetailsFragment;
import com.votinginfoproject.VotingInformationProject.fragments.LocationsFragment;
import com.votinginfoproject.VotingInformationProject.fragments.SupportWebViewFragment;
import com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment.VIPMapFragment;
import com.votinginfoproject.VotingInformationProject.models.CivicApiAddress;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.FilterLabels;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Bounds;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.singletons.GATracker;
import com.votinginfoproject.VotingInformationProject.models.singletons.UserPreferences;

import java.util.ArrayList;
import java.util.Stack;

public class VIPTabBarActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    // activity identifier
    static final int PROMPT_ENABLE_LOCATION_SERVICES = 1;
    static TabsAdapter mTabsAdapter;
    private final String TAG = VIPTabBarActivity.class.getSimpleName();
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    FragmentManager mFragmentManager;
    GeocodeQuery.GeocodeCallBackListener pollingCallBackListener;
    GeocodeQuery.GeocodeCallBackListener homeCallBackListener;
    GeocodeQuery.GeocodeCallBackListener adminBodyCallBackListener;
    VoterInfo voterInfo;
    Location homeLocation;
    LocationsFragment locationsFragment;
    DirectionsFragment directionsFragment;
    boolean useMetric;
    ReverseGeocodeQuery.ReverseGeocodeCallBackListener reverseGeocodeCallBackListener;
    int selectedOriginItem = 0; // item selected from prompt for directions origin; 0 for user-entered address
    boolean loadingFeedBackForm = false;
    // track filters
    FilterLabels filterLabels = null;
    String userLocationAddress;
    LatLng userLocation;
    String encodedDirectionsPolyline = "";
    LatLngBounds polylineBounds;
    private GoogleApiClient mGoogleApiClient;
    // track what the current fragment is and the history of selected fragments (for moving back)
    private int currentFragment = R.id.ballot_fragment;
    private Stack<Integer> fragmentHistory = new Stack<>();

    /**
     * Non-default constructor for testing, to set the application context.
     *
     * @param context Mock context with a VoterInfo object
     */
    public VIPTabBarActivity() {
        super();
        fragmentHistory.push(currentFragment);
    }

    public boolean isLoadingFeedBackForm() {
        return loadingFeedBackForm;
    }

    public void setLoadingFeedBackForm(boolean loadingFeedBackForm) {
        this.loadingFeedBackForm = loadingFeedBackForm;
    }

    /**
     * OnClick listener for feedback form link
     *
     * @param v The clickable text field for supplying feedback
     */
    @Override
    public void onClick(View v) {
        if (isLoadingFeedBackForm()) {
            Log.d(TAG, "Already loading feedback form.");

            return;
        }

        Log.d(TAG, "Feedback button text clicked");
        loadingFeedBackForm = true;
        // load browser in app

        // Send in reference to the calling view's parent, so it can be found again later.
        // Necessary because there are multiple references to the feedback text view, so the scope
        // of search must be restricted to find ~this~ feedback text view again.
        View parent = (View) v.getParent().getParent();
        SupportWebViewFragment webViewFragment = SupportWebViewFragment.newInstance(parent.getId());
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        // swap out current fragment with support web fragment,
        // and put current fragment on back stack
        ft.replace(getCurrentFragment(), webViewFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        // set the current fragment when back pressed
        if (!fragmentHistory.isEmpty()) {
            currentFragment = fragmentHistory.pop();
        }
        super.onBackPressed();
    }

    public int getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(int currentFragment) {
        this.currentFragment = currentFragment;
        fragmentHistory.push(currentFragment);
    }

    public void clearFragmentHistory() {
        fragmentHistory.clear();
    }

    public FilterLabels getFilterLabels() {
        if (filterLabels == null) {
            Resources res = this.getResources();
            filterLabels = new FilterLabels(res.getString(R.string.locations_list_label_all_sites),
                    res.getString(R.string.locations_list_label_early_voting),
                    res.getString(R.string.locations_list_label_polling_sites),
                    res.getString(R.string.locations_list_label_drop_off));
        }
        return filterLabels;
    }

    public String getUserLocationAddress() {
        return userLocationAddress;
    }

    public LatLng getUserLocation() {
        return userLocation;
    }

    public LatLngBounds getPolylineBounds() {
        return polylineBounds;
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
        setCurrentFragment(R.id.contest_fragment);

        fragmentTransaction.addToBackStack("contest");
        fragmentTransaction.commit();
    }

    /**
     * Transition from contest fragment to candidate fragment when user selects list item.
     *
     * @param contestPosition   Index of selected contest within the VoterInfo object's list of contests
     * @param candidatePosition Index of selected candidate within the contest's list of candidates
     */
    public void showCandidateDetails(int contestPosition, int candidatePosition) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment candidateFragment = CandidateFragment.newInstance(contestPosition, candidatePosition);

        // See showContestDetails for info on how the fragment transition occurs
        fragmentTransaction.replace(R.id.ballot_fragment, candidateFragment);

        fragmentTransaction.addToBackStack("candidate");
        fragmentTransaction.commit();
    }

    public void showDirections(String item) {
        Log.d(TAG, "Going to show directions to " + item + "...");
        // first ask user where to get directions from:  entered address, or current location?
        promptForDirectionsOrigin(item);
    }

    private void promptForDirectionsOrigin(final String item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(R.string.activity_vip_tab_directions_origin_prompt);

        // persist user's last choice in list by passing selectedOriginItem as the selected index
        builder.setSingleChoiceItems(R.array.activity_vip_tab_directions_origin_options, selectedOriginItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "You selected: " + which);
                selectedOriginItem = which;
            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // user clicked ok
                Log.d(TAG, "Final selection: " + selectedOriginItem);

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
                fragmentTransaction.addToBackStack("directions");
                fragmentTransaction.commit();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // user cancelled; do not show directions
                Log.d(TAG, "User cancelled dialog.");

                dialog.cancel();
            }
        });

        builder.create().show();
    }

    private boolean playServicesAvailable() {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google Play services are available!");

            return true;
        } else {
            Log.e(TAG, "Google Play services are unavailable!");
            // alert user
            CharSequence errorMessage = this.getResources().getText(R.string.locations_map_error_play_services_unavailable);
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
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

        // first check if location to show actually has a successful geocode result
        boolean haveLocation = false;

        if (destinationLocationIndex.equals("home")) {
            // check for user-entered address geocode result
            if (homeLocation != null && (homeLocation.getLongitude() != 0 || homeLocation.getLongitude() != 0)) {
                haveLocation = true;
            }
        } else {
            // have either PollingLocation or ElectionAdministrationBody location
            CivicApiAddress address = voterInfo.getAddressForId(destinationLocationIndex);
            if (address != null && (address.longitude != 0 || address.latitude != 0)) {
                haveLocation = true;
            }
        }
        if (!haveLocation) {
            // alert user if no location to show on map
            CharSequence errorMessage = this.getResources().getText(R.string.locations_map_error_geocode);
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            return;
        }

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        MapFragment mapFragment = VIPMapFragment.newInstance(getBaseContext(), destinationLocationIndex, encodedDirectionsPolyline);
        if (destinationLocationIndex.equals(ElectionAdministrationBody.AdminBody.STATE) ||
                destinationLocationIndex.equals(ElectionAdministrationBody.AdminBody.LOCAL)) {
            // got here from details tab
//            fragmentTransaction.replace(R.id.election_details_fragment, mapFragment);
        } else {
            // got here from locations tab
//            fragmentTransaction.replace(R.id.locations_list_fragment, mapFragment);
        }
        fragmentTransaction.addToBackStack("map");
        fragmentTransaction.commit();
    }

    public LatLng getHomeLatLng() {
        if (homeLocation != null) {
            return new LatLng(homeLocation.getLatitude(), homeLocation.getLongitude());
        }

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viptab_bar);

        mFragmentManager = getSupportFragmentManager();

        // Get analytics tracker (should auto-report)
        GATracker.getTracker(GATracker.TrackerName.APP_TRACKER);

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
        mTabsAdapter.addTab(actionBar.newTab().setText(R.string.bottom_navigation_title_ballot), BallotFragment.class, "ballot_tab", null);
        mTabsAdapter.addTab(actionBar.newTab().setText(R.string.bottom_navigation_title_polls), LocationsFragment.class, "locations_tab", null);
        mTabsAdapter.addTab(actionBar.newTab().setText(R.string.bottom_navigation_title_details), ElectionDetailsFragment.class, "details_tab", null);

        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }

        // geocode addresses when activity launches
        setUpGeocodePreferences();

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).
                addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
    }

    /**
     * Helper function to geocode entered address and the polling location results for that address
     * when the tab bar is loaded.
     */
    private void setUpGeocodePreferences() {
        // get LocationsFragment's root view
        locationsFragment = (LocationsFragment) mTabsAdapter.getItem(1);
        voterInfo = UserPreferences.getVoterInfo();
        voterInfo.setUpLocations();

        useMetric = UserPreferences.useMetric();

        // set up callback listener for reverse-geocode address result
        reverseGeocodeCallBackListener = new ReverseGeocodeQuery.ReverseGeocodeCallBackListener() {
            @Override
            public void callback(String address) {
                Log.d(TAG, "Got reverse-geocode address " + address);
                if (address != null && !address.isEmpty()) {
                    userLocationAddress = address;
                } else {
                    Log.e(TAG, "Got empty address result!");
                    userLocationAddress = "";
                }
            }
        };

        // Callback for polling location geocode result
        pollingCallBackListener = new GeocodeQuery.GeocodeCallBackListener() {
            @Override
            public void callback(String key, double lat, double lon, double distance) {
                if (key.equals("error")) {
                    Log.e(TAG, "Geocode failed!");
                    return;
                }

                // find object and set values on it
                PollingLocation foundLoc = voterInfo.getLocationForId(key);
                if (foundLoc != null) {
                    foundLoc.address.latitude = lat;
                    foundLoc.address.longitude = lon;
                    foundLoc.address.distance = distance;
                } else {
                    Log.e(TAG, "Could not find location " + key + " to set geocoding result!");
                }
            }
        };

        // callback for home address geocode result
        homeCallBackListener = new GeocodeQuery.GeocodeCallBackListener() {
            @Override
            public void callback(String key, double lat, double lon, double distance) {
                if (key.equals("error")) {
                    Log.e(TAG, "Failed to geocode home address!");

                    return;
                }

                homeLocation = new Location("home");
                homeLocation.setLatitude(lat);
                homeLocation.setLongitude(lon);
                UserPreferences.setHomeLocation(homeLocation);

                // start background geocode tasks for polling locations
                ArrayList<PollingLocation> allLocations = voterInfo.getAllLocations();
                for (PollingLocation location : allLocations) {
                    // key by address, if location has no ID
                    if (location.id != null) {
                        new GeocodeQuery(getBaseContext(), pollingCallBackListener, location.id,
                                location.address.toGeocodeString(), homeLocation, useMetric, null).execute();
                    } else {
                        new GeocodeQuery(getBaseContext(), pollingCallBackListener, location.address.toGeocodeString(),
                                location.address.toGeocodeString(), homeLocation, useMetric, null).execute();
                    }
                }

                // start background geocode tasks for election admin bodies' physical addresses

                // state
                CivicApiAddress stateAdminAddress = voterInfo.getAdminAddress(ElectionAdministrationBody.AdminBody.STATE);
                if (stateAdminAddress != null) {
                    new GeocodeQuery(getBaseContext(), adminBodyCallBackListener, ElectionAdministrationBody.AdminBody.STATE,
                            stateAdminAddress.toGeocodeString(), homeLocation, useMetric, null).execute();
                }

                // local
                CivicApiAddress localAdminAddress = voterInfo.getAdminAddress(ElectionAdministrationBody.AdminBody.LOCAL);
                if (localAdminAddress != null) {
                    new GeocodeQuery(getBaseContext(), adminBodyCallBackListener, ElectionAdministrationBody.AdminBody.LOCAL,
                            localAdminAddress.toGeocodeString(), homeLocation, useMetric, null).execute();
                }
            }
        };

        // Callback for election administration body address geocode result
        adminBodyCallBackListener = new GeocodeQuery.GeocodeCallBackListener() {
            @Override
            public void callback(String key, double lat, double lon, double distance) {
                if (key.equals("error")) {
                    Log.e(TAG, "Failed to geocode administrative body physical address!");

                    return;
                }

                CivicApiAddress address = voterInfo.getAdminAddress(key);
                if (address != null) {
                    address.latitude = lat;
                    address.longitude = lon;
                    address.distance = distance;
                } else {
                    Log.e(TAG, "Failed to set geocode result on election admin body!");
                }
            }
        };

        if (playServicesAvailable()) {
            // geocode home address; once result returned, geocode polling and admin body locations
            new GeocodeQuery(getBaseContext(), homeCallBackListener, "home", voterInfo.normalizedInput.toGeocodeString(),
                    null, useMetric, null).execute();
        }
    }

    public LatLng getUserLocation(boolean showPrompt) {
        // check for play services first
        if (!playServicesAvailable()) {
            return null;
        }

        if (!mGoogleApiClient.isConnected()) {
            // location services aren't ready yet (maybe just turned on)
            // wait for onConnected() to be called, then try again
            if (!mGoogleApiClient.isConnecting()) {
                Log.d(TAG, "Location client not connected; try connecting...");
                mGoogleApiClient.disconnect(); // call connect from disconnect

                return null;
            } else {
                Log.d(TAG, "Location client is connecting...");

                return null;
            }
        }

        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (currentLocation != null) {
            userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            Log.d(TAG, "Current location is: " + currentLocation.getLatitude() + "," + currentLocation.getLongitude());

            // now go reverse-geocode to find address for current location
            userLocationAddress = "";
            new ReverseGeocodeQuery(getBaseContext(), reverseGeocodeCallBackListener).execute(currentLocation);

            return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            Log.e(TAG, "Current location not found!  Are Location services enabled?");
            userLocation = null;

            if (showPrompt) {
                // user has probably disabled Location services; prompt them to go turn it on
                final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
                builder.setMessage(R.string.activity_vip_tab_enable_location_services_prompt);
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
            Log.e(TAG, "No longer using current location for directions!  Ignoring.");

            return;
        }

        LatLng location = getUserLocation(false);
        if (location != null) {
            // have successfully gotten location after prompting to enable location services
            // call back to directions fragment to try getting directions now

            if (directionsFragment != null) {
                directionsFragment.setUpWithOrigin(location);
            } else {
                Log.e(TAG, "Could not find directions fragment!");
            }
        } else {
            Log.d(TAG, "Location is still null");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PROMPT_ENABLE_LOCATION_SERVICES) {
            // User got back from location services screen; try to get their location again.
            // Do not prompt them again to enable location services.
            retryGetDirections();
        } else {
            Log.e(TAG, "Got result for unrecognized activity!");
        }
    }

    /**
     * Called when Activity becomes visible
     */
    @Override
    protected void onStart() {
        super.onStart();
        // connect to location service
        mGoogleApiClient.connect();
        //Get an Analytics tracker to report app starts, uncaught exceptions, etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    /**
     * Disconnect location client when app is no longer going to be visible
     */
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();

        //Stop analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Location services connected.");
        retryGetDirections(); // try to get found location to directions fragment
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Location services failed.");
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

    public void setMapPolylines(String polyline, Bounds bounds) {
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

    public void clearPolylines() {
        encodedDirectionsPolyline = null;
        polylineBounds = null;
    }
}
