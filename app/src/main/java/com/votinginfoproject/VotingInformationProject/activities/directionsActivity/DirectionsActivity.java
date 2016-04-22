package com.votinginfoproject.VotingInformationProject.activities.directionsActivity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.fragments.directionsListViewPagerFragment.DirectionsListViewPagerFragment;

public class DirectionsActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {
    private static int selected_alpha = 255;
    private static int unselected_alpha = (int) (255 * 0.6);

    private static String ARG_CIVIC_ADDRESS = "arg_civic_address";
    private static String ARG_CURRENT_LOCATION = "arg_current_location";
    private static String ARG_POLLING_LOCATION = "arg_polling_location";


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.

        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        setupTabs();


        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        DirectionsListViewPagerFragment pager = new DirectionsListViewPagerFragment();

        transaction.replace(R.id.container, pager, "D");


        transaction.addToBackStack("D");

        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_directions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Log.v("here", "jere");

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getIcon() != null) {
            tab.getIcon().setAlpha(selected_alpha);
        }

//Determine if list is showing or map is showing, then update appropriately
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        if (tab.getIcon() != null) {
            tab.getIcon().setAlpha(unselected_alpha);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
//Reset View
    }

    private void setupTabs() {
        mTabLayout.removeAllTabs();

        TabLayout.Tab driveTab = mTabLayout.newTab().setIcon(R.drawable.ic_directions_car);
        driveTab.setContentDescription(R.string.directions_label_drive_button);

        TabLayout.Tab busTab = mTabLayout.newTab().setIcon(R.drawable.ic_directions_bus);
        busTab.setContentDescription(R.string.directions_label_transit_button);

        TabLayout.Tab bikeTab = mTabLayout.newTab().setIcon(R.drawable.ic_directions_bike);
        bikeTab.setContentDescription(R.string.directions_label_bike_button);

        TabLayout.Tab walkTab = mTabLayout.newTab().setIcon(R.drawable.ic_directions_walk);
        walkTab.setContentDescription(R.string.directions_label_walk_button);

        mTabLayout.addTab(driveTab);
        mTabLayout.addTab(busTab);
        mTabLayout.addTab(bikeTab);
        mTabLayout.addTab(walkTab);

        mTabLayout.setOnTabSelectedListener(this);

        if (driveTab.getIcon() != null) {
            driveTab.getIcon().setAlpha(selected_alpha);
        }

        if (busTab.getIcon() != null) {
            busTab.getIcon().setAlpha(unselected_alpha);
        }

        if (bikeTab.getIcon() != null) {
            bikeTab.getIcon().setAlpha(unselected_alpha);
        }

        if (walkTab.getIcon() != null) {
            walkTab.getIcon().setAlpha(unselected_alpha);
        }
    }
}
