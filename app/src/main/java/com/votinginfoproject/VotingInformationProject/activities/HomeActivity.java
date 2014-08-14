package com.votinginfoproject.VotingInformationProject.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.asynctasks.ReverseGeocodeQuery;
import com.votinginfoproject.VotingInformationProject.fragments.HomeFragment;
import com.votinginfoproject.VotingInformationProject.models.VIPApp;
import com.votinginfoproject.VotingInformationProject.models.VIPAppContext;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;


public class HomeActivity extends FragmentActivity implements HomeFragment.OnInteractionListener,
        LoaderManager.LoaderCallbacks<Cursor>,GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    static final int PICK_CONTACT_REQUEST = 1;
    VIPApp app;
    Uri contactUri;
    EditText addressView;
    LoaderManager loaderManager;
    LocationClient mLocationClient;
    ReverseGeocodeQuery.ReverseGeocodeCallBackListener reverseGeocodeCallBackListener;

    public String getSelectedParty() {
        return selectedParty;
    }

    public void setSelectedParty(String selectedParty) {
        this.selectedParty = selectedParty;
        app.setSelectedParty(selectedParty);
    }

    String selectedParty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        app = new VIPAppContext((VIPApp) getApplicationContext()).getVIPApp();
        loaderManager = getLoaderManager();
        selectedParty = "";
        contactUri = null;
        mLocationClient = new LocationClient(this, this, this);
        addressView = (EditText)findViewById(R.id.home_edittext_address);

        // set up callback listener for reverse-geocode address result
        reverseGeocodeCallBackListener = new ReverseGeocodeQuery.ReverseGeocodeCallBackListener() {
            @Override
            public void callback(String address) {
                Log.d("HomeActivity", "Got reverse-geocoded address " + address);
                if (address != null || !address.isEmpty()) {
                    addressView.setText(address);
                } else {
                    Log.e("HomeActivity", "Got empty address result!");
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void onGoButtonPressed(View view) {
        Intent intent = new Intent(this, VIPTabBarActivity.class);
        startActivity(intent);
    }

    public void onUseLocationButtonPressed(View view) {
        // TODO: check for Google Play services availability here (instead of in VIPTabBarActivity)
        Location currentLocation = mLocationClient.getLastLocation();
        if (currentLocation != null) {
            Log.d("HomeActivity", "Current location is: " + currentLocation.getLatitude() + "," + currentLocation.getLongitude());
            // now go reverse-geocode to find address for current location
            new ReverseGeocodeQuery(reverseGeocodeCallBackListener).execute(currentLocation);
        } else {
            Log.e("HomeActivity", "Current location not found!  Are Location services enabled?");

            // TODO: user has probably diabled Location services
            // prompt them to go turn it on?
        }
    }

    public void onSelectContactButtonPressed(View view) {
        // contact picker intent to return an address; will only show contacts that have an address
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                // Get the URI that points to the selected contact
                contactUri = data.getData();

                // restart loader, if a contact was already selected
                loaderManager.destroyLoader(PICK_CONTACT_REQUEST);

                // start async query to get contact info
                loaderManager.initLoader(PICK_CONTACT_REQUEST, null, this);
            } else {
                // PASS (user didn't pick an address)
            }
        }
    }

    public void searchedAddress(VoterInfo voterInfo) {
        // set VoterInfo object on app singleton
        app.setVoterInfo(voterInfo, selectedParty);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (contactUri == null) {
            return null;
        }

        String[] projection = { ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS };
        return new CursorLoader(this, contactUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            data.moveToFirst();
            // get result with single column, named data1
            String address = data.getString(0);
            Log.d("HomeActivity", "Got cursor result: " + address);

            // set address found in view
            addressView.setText(address);
        } else {
            Log.e("HomeActivity", "Cursor got no results!");
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
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("HomeActivity", "Location services connected.");
    }

    @Override
    public void onDisconnected() {
        Log.d("HomeActivity", "Location services disconnected.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("HomeActivity", "Location services failed.");
    }
}
