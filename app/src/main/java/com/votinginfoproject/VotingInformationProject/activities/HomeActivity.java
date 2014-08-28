package com.votinginfoproject.VotingInformationProject.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.fragments.HomeFragment;
import com.votinginfoproject.VotingInformationProject.models.VIPApp;
import com.votinginfoproject.VotingInformationProject.models.VIPAppContext;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;


public class HomeActivity extends FragmentActivity implements HomeFragment.OnInteractionListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    static final int PICK_CONTACT_REQUEST = 1;
    VIPApp app;
    Uri contactUri;
    EditText addressView;
    LoaderManager loaderManager;

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
        addressView = (EditText)findViewById(R.id.home_edittext_address);
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

            // initate search with new address
            HomeFragment myFragment = (HomeFragment)getSupportFragmentManager().findFragmentById(R.id.home_fragment);
            myFragment.makeElectionQuery();
        } else {
            Log.e("HomeActivity", "Cursor got no results!");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // PASS
    }
}
