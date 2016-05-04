package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;
import com.votinginfoproject.VotingInformationProject.activities.directionsActivity.DirectionsActivity;
import com.votinginfoproject.VotingInformationProject.activities.reportErrorActivity.ReportErrorActivity;
import com.votinginfoproject.VotingInformationProject.activities.reportErrorActivity.ReportErrorPresenter;
import com.votinginfoproject.VotingInformationProject.activities.reportErrorActivity.ReportErrorPresenterImpl;
import com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.ballotFragment.ContestListFragment;
import com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.candidateInformationFragment.CandidateInformationFragment;
import com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.contestInformationFragment.ContestInformationListFragment;
import com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment.BottomNavigationFragment;
import com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment.ElectionDetailsListFragment;
import com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment.PollingSitesListFragment;
import com.votinginfoproject.VotingInformationProject.models.Candidate;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.singletons.VoterInformation;
import com.votinginfoproject.VotingInformationProject.views.BottomNavigationBar;

public class VoterInformationActivity extends BaseActivity<VoterInformationPresenter> implements
        VoterInformationView,
        BottomNavigationBar.BottomNavigationBarCallback,
        PollingSitesListFragment.PollingSitesListener,
        ElectionDetailsListFragment.ElectionDetailsListFragmentCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ContestListFragment.ContestListListener,
        ContestInformationListFragment.ContestInformationListener,
        CandidateInformationFragment.CandidateInformationListener {
    private final static String TAG = VoterInformationActivity.class.getSimpleName();
    private final static String TOP_LEVEL_TAG = "VIP_TOP_LEVEL_TAG";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_voter_information);

        BottomNavigationBar mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        if (mBottomNavigationBar != null) {
            mBottomNavigationBar.setListener(this);
        }

        setPresenter(new VoterInformationPresenterImpl());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startPollingLocation();
        }

        if (savedInstanceState != null) {
            VoterInformation.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Do not call super here until the Support Toolbar Parcelable crash is fixed
        VoterInformation.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            VoterInformation.onRestoreInstanceState(savedInstanceState);
        }

        setPresenter(new VoterInformationPresenterImpl());
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getFragmentManager();

        Log.v(TAG, "back count: " + manager.getBackStackEntryCount());

        if (manager.getBackStackEntryCount() > 1) {
            manager.popBackStack();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getPresenter().backNavigationBarButtonClicked();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void pollsButtonSelected() {
        getPresenter().pollingSitesButtonClicked();
    }

    @Override
    public void ballotButtonSelected() {
        getPresenter().ballotButtonClicked();
    }

    @Override
    public void detailsButtonSelected() {
        getPresenter().detailsButtonClicked();
    }

    @Override
    public void presentParentLevelFragment(Fragment parentLevelFragment) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        manager.popBackStack(TOP_LEVEL_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        transaction.replace(R.id.layout_content, parentLevelFragment, TOP_LEVEL_TAG);

        transaction.addToBackStack(TOP_LEVEL_TAG);

        transaction.commit();
    }

    @Override
    public void presentChildLevelFragment(Fragment childLevelFragment) {
        FragmentManager manager = getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        String fragmentTag = String.valueOf(childLevelFragment.hashCode());
        transaction.replace(R.id.layout_content, childLevelFragment, fragmentTag);

        transaction.addToBackStack(fragmentTag);

        transaction.commit();
    }

    @Override
    public void navigateBack() {
        onBackPressed();
    }

    @Override
    public void scrollCurrentFragmentToTop() {
        FragmentManager manager = getFragmentManager();
        int entryCount = manager.getBackStackEntryCount();

        if (entryCount > 0) {
            FragmentManager.BackStackEntry entry = manager.getBackStackEntryAt(entryCount - 1);
            Fragment lastFragment = manager.findFragmentByTag(entry.getName());

            if (lastFragment instanceof BottomNavigationFragment) {
                ((BottomNavigationFragment) lastFragment).resetView();
            }
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void mapButtonClicked(@LayoutRes int currentSort) {
        getPresenter().mapButtonClicked(currentSort);
    }

    @Override
    public void listButtonClicked(@LayoutRes int currentSort) {
        getPresenter().listButtonClicked(currentSort);
    }

    //Polling Site List Interface
    @Override
    public void navigateToDirections(final PollingLocation location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.activity_vip_tab_directions_origin_prompt);

        builder.setItems(R.array.activity_vip_tab_directions_origin_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openDirectionsActivity(location, false);
                        break;
                    case 1:
                        openDirectionsActivity(location, true);
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openDirectionsActivity(PollingLocation location, boolean useLastKnownLocation) {
        Intent intent = new Intent(this, DirectionsActivity.class);
        intent.putExtra(DirectionsActivity.ARG_LOCATION_DESTINATION, location);
        intent.putExtra(DirectionsActivity.ARG_USE_LAST_KNOWN_LOCATION, useLastKnownLocation);
        startActivity(intent);
    }

    @Override
    public void navigateToReportErrorActivity(ReportErrorPresenter presenter) {
        Intent intent = new Intent(this, ReportErrorActivity.class);

        intent.putExtra(ReportErrorActivity.ARG_PRESENTER, presenter);

        startActivity(intent);
    }

    @Override
    public void contestClicked(Contest contest) {
        getPresenter().contestClicked(contest);
    }

    @Override
    public void candidateClicked(Candidate candidate) {
        getPresenter().candidateClicked(candidate);
    }

    @Override
    public void phoneNumberClicked(String phoneNumber) {
        //Navigate to phone number
        Intent dial = new Intent();
        dial.setAction("android.intent.action.DIAL");
        dial.setData(Uri.parse("tel:" + phoneNumber));

        startActivity(Intent.createChooser(dial, getString(R.string.accessibility_description_phone)));
    }

    @Override
    public void emailClicked(String email) {
        //navigate to email
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, email);

        startActivity(Intent.createChooser(intent, getString(R.string.accessibility_description_email)));
    }

    @Override
    public void reportErrorClicked() {
        getPresenter().reportErrorButtonClicked();
    }

    //Election Details Interface
    @Override
    public void navigateToURL(String urlString) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.background_blue));

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(urlString));
    }

    @Override
    public void reportErrorButtonClicked() {
        getPresenter().reportErrorButtonClicked();
    }

    @Override
    public void navigateToDirectionsView(String address) {
        //TODO add other things here
        Log.v(TAG, "Address selected: " + address);
    }

    @Override
    public void startPollingLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (lastLocation != null) {
                com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Location formattedLocation =
                        new com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Location();

                formattedLocation.lat = (float) lastLocation.getLatitude();
                formattedLocation.lng = (float) lastLocation.getLongitude();

                VoterInformation.setLastKnownLocation(formattedLocation);
            } else {
                Log.e(TAG, "Expected location but got none");
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