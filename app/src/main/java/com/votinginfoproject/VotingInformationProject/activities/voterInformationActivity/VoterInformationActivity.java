package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;
import com.votinginfoproject.VotingInformationProject.constants.ExtraConstants;
import com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment.BottomNavigationFragment;
import com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment.PollingSitesFragment;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.views.BottomNavigationBar;


public class VoterInformationActivity extends BaseActivity<VoterInformationPresenter> implements
        VoterInformationView,
        BottomNavigationBar.BottomNavigationBarCallback,
        BottomNavigationFragment.OnBackPressedListener, PollingSitesFragment.PollingSiteOnClickListener {

    private final static String TAG = VoterInformationActivity.class.getSimpleName();
    private final static String TOP_LEVEL_TAG = "VIP_TOP_LEVEL_TAG";

    private BottomNavigationBar mBottomNavigationBar;
    private BottomNavigationFragment lastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_voter_information);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        mBottomNavigationBar.setListener(this);

        //Unwrap Extras
        Bundle extras = getIntent().getExtras();

        if (extras == null) {
            //Finish with errors to display
            finish();
        } else {
            String voterInfoString = extras.getString(ExtraConstants.VOTER_INFO);
            String filter = extras.getString(ExtraConstants.PARTY_FILTER);

            //Inflate Voter Info from Home Activity
            VoterInfo voterInfo = new Gson().fromJson(voterInfoString, VoterInfo.class);

            setPresenter(new VoterInformationPresenterImpl(voterInfo, filter));
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getFragmentManager();

        if (manager.getBackStackEntryCount() > 1) {
            manager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


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
    public void presentParentLevelFragment(BottomNavigationFragment parentLevelFragment) {
        lastFragment = parentLevelFragment;
        parentLevelFragment.setOnBackPressedListener(this);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        manager.popBackStack(TOP_LEVEL_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        transaction.replace(R.id.layout_content, parentLevelFragment, TOP_LEVEL_TAG);

        transaction.addToBackStack(TOP_LEVEL_TAG);


        transaction.commit();
    }

    @Override
    public void presentChildLevelFragment(BottomNavigationFragment childLevelFragment) {
        lastFragment = childLevelFragment;

        childLevelFragment.setOnBackPressedListener(this);

        FragmentManager manager = getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.layout_content, childLevelFragment);

        transaction.addToBackStack(childLevelFragment.getClass().getSimpleName());

        transaction.commit();
    }

    @Override
    public void navigateBack() {
        onBackPressed();
    }

    @Override
    public void updatePollingSitesFilter() {

    }

    @Override
    public void scrollCurrentFragmentToTop() {
        if (lastFragment != null) {
            lastFragment.resetView();
        }
    }

    @Override
    public void navigateToMap() {

    }

    //Polling Site List Interface
    @Override
    public void pollingSiteClicked(PollingLocation location) {
        Log.v(TAG, "Polling locaiton Clicked: ");
    }

    @Override
    public void reportErrorClicked() {
        Log.v(TAG, "Report Error Clicked()");

    }
}
