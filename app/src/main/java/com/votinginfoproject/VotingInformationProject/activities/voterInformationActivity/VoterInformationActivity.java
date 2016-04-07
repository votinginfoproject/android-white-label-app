package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;
import com.votinginfoproject.VotingInformationProject.constants.ExtraConstants;
import com.votinginfoproject.VotingInformationProject.fragments.BallotFragment;
import com.votinginfoproject.VotingInformationProject.fragments.ContestFragment;
import com.votinginfoproject.VotingInformationProject.fragments.LocationsFragment;
import com.votinginfoproject.VotingInformationProject.fragments.TestFragment;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.views.BottomNavigationBar;

import java.util.ArrayList;


public class VoterInformationActivity extends BaseActivity<VoterInformationPresenter> implements VoterInformationView, BottomNavigationBar.BottomNavigationBarCallback {

    private BottomNavigationBar mBottomNavigationBar;

    private FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_voter_information);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setSupportActionBar(toolbar);

        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        mBottomNavigationBar.setListener(this);

        mContainer = (FrameLayout) findViewById(R.id.layout_content);

        FragmentManager manager = getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.layout_content, TestFragment.newInstance("initial", "poo"), "TAG");
        transaction.commit();


        //Unwrap Extras
        Bundle extras = getIntent().getExtras();

        if (extras == null) {
            //Finish with errors to display
            finish();
        } else {
            String voterInfoString = extras.getString(ExtraConstants.VOTER_INFO);
            String filter = extras.getString(ExtraConstants.PARTY_FILTER);

            setPresenter(new VoterInformationPresenterImpl(new Gson().fromJson(voterInfoString, VoterInfo.class), filter));
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getFragmentManager();

        if (manager.getBackStackEntryCount() > 0) {
            manager.beginTransaction();
            manager.popBackStack();
            manager.executePendingTransactions();

            FragmentManager.BackStackEntry entry = manager.getBackStackEntryAt(0);
            entry.getId();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void pollsButtonSelected() {
        getPresenter().pollingSitesButtonClicked();
        this.setTitle("Polling sites");

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.layout_content, TestFragment.newInstance("polling " + manager.getBackStackEntryCount(), "poo"), "TAG");
        transaction.addToBackStack("TAG" + manager.getBackStackEntryCount());
        transaction.commit();
    }

    @Override
    public void ballotButtonSelected() {
        getPresenter().ballotButtonClicked();
        this.setTitle("Ballot");

    }

    @Override
    public void detailsButtonSelected() {
        getPresenter().detailsButtonClicked();
        this.setTitle("Details");

    }

    @Override
    public void showPollingSiteFragment() {

    }

    @Override
    public void showBallotFragment() {

    }

    @Override
    public void showDetailsFragment() {

    }

    @Override
    public void showMapDetailFragment() {

    }

    @Override
    public void updateFilter() {

    }
}
