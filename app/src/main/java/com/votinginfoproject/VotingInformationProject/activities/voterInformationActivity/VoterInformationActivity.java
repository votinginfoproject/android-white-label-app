package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.constants.ExtraConstants;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.views.BottomNavigationBar;


public class VoterInformationActivity extends Activity implements BottomNavigationBar.BottomNavigationBarCallback {

    private VoterInformationPresenter mPresenter;
    private BottomNavigationBar mBottomNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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

            mPresenter = new VoterInformationPresenterImpl(new Gson().fromJson(voterInfoString, VoterInfo.class), filter);
        }
    }

    @Override
    public void pollsButtonSelected() {
        mPresenter.pollintSitesButtonClicked();
    }

    @Override
    public void ballotButtonSelected() {
        mPresenter.ballotButtonClicked();
    }

    @Override
    public void detailsButtonSelected() {
        mPresenter.detailsButtonClicked();
    }
}
