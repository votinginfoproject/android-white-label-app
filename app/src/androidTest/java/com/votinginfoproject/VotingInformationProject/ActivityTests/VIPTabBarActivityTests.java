package com.votinginfoproject.VotingInformationProject.ActivityTests;

import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;
import com.votinginfoproject.VotingInformationProject.fragments.BallotFragment;
import com.votinginfoproject.VotingInformationProject.fragments.ContestFragment;
import com.votinginfoproject.VotingInformationProject.models.VoterInfoResponse;
import com.votinginfoproject.VotingInformationProject.models.singletons.VoterInformation;

/**
 * Created by kathrynkillebrew on 7/22/14.
 */
public class VIPTabBarActivityTests extends ActivityInstrumentationTestCase2<VIPTabBarActivity> {

    VIPTabBarActivity tabBarActivity;
    FragmentManager fragmentManager;
    BallotFragment ballotFragment;
    ContestFragment contestFragment;

    public VIPTabBarActivityTests() { super(VIPTabBarActivity.class); }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // use non-default constructor for activity to set mock data
        setActivity(new VIPTabBarActivity());

        refreshActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        tabBarActivity.finish();
    }

    @UiThreadTest
    public void testVIPTabBarHasVoterInfo() {
        VoterInfoResponse voterInfoResponse = VoterInformation.getVoterInfo();
        assertEquals(voterInfoResponse.election.name, "Test Election");
    }

    @UiThreadTest
    public void testVIPTabBarActivityExists() {
        assertNotNull(tabBarActivity);
    }

    @UiThreadTest
    public void testFragmentManagerExists() {
        assertNotNull(fragmentManager);

        // TODO: action bar is null here
        //ActionBar actionBar = tabBarActivity.getActionBar();

    }

    public void refreshActivity() {
        tabBarActivity = getActivity();
        fragmentManager = tabBarActivity.getSupportFragmentManager();

    }
}
