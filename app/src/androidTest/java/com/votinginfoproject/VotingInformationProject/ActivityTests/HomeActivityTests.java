package com.votinginfoproject.VotingInformationProject.ActivityTests;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.HomeActivity;
import com.votinginfoproject.VotingInformationProject.fragments.HomeFragment;

/**
 * Created by andrew on 7/17/14.
 */
public class HomeActivityTests extends ActivityInstrumentationTestCase2<HomeActivity> {

    HomeActivity homeActivity;
    FragmentManager fragmentManager;
    HomeFragment homeFragment;

    EditText homeEditTextAddress;

    public HomeActivityTests() {
        super(HomeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Turns off touch mode in device/emulator. If any test methods send key events, must
        //  call this before starting any activities, otherwise this call is ignored.
        setActivityInitialTouchMode(false);

        refreshActivity();

        homeFragment.setAddress("");
    }

    @Override
    protected void tearDown() throws Exception {
        homeActivity.finish();
    }

    public void testHomeActivityExists() {
        assertNotNull(homeActivity);
    }

    public void testHomeFragmentExists() {
        assertNotNull(fragmentManager);
        assertNotNull(homeFragment);
    }

    public void testHomeFragmentCommitsAddressToSharedPreferences() {
        SharedPreferences preferences = homeActivity.getPreferences(Context.MODE_PRIVATE);
        String addressKey = homeFragment.getString(R.string.LAST_ADDRESS_KEY);
        assertEquals(homeFragment.getAddress(), "");

        String testCommitAddress = "123 Main St, Richmond VA";
        homeFragment.setAddress(testCommitAddress);
        assertEquals(preferences.getString(addressKey, null), testCommitAddress);
    }

    public void testHomeFragmentPullsAddressFromSharedPreferences() {
        SharedPreferences preferences = homeActivity.getPreferences(Context.MODE_PRIVATE);
        String addressKey = homeFragment.getString(R.string.LAST_ADDRESS_KEY);
        assertEquals(homeFragment.getAddress(), "");

        String testPullAddress = "123 Test Address, County, State";
        preferences.edit().putString(addressKey, testPullAddress).apply();
        assertEquals(homeFragment.getAddress(), testPullAddress);
    }

    public void refreshActivity() {
        homeActivity = (HomeActivity)getActivity();
        fragmentManager = homeActivity.getSupportFragmentManager();
        homeFragment = (HomeFragment)fragmentManager.findFragmentById(R.id.home_fragment);
    }


}
