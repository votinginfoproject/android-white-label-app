package com.votinginfoproject.VotingInformationProject.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.fragments.BallotFragment;
import com.votinginfoproject.VotingInformationProject.fragments.ElectionDetailsFragment;
import com.votinginfoproject.VotingInformationProject.fragments.LocationsFragment;

import java.util.ArrayList;

/**
 * This is a helper class that implements the management of tabs and all
 * details of connecting a ViewPager with associated TabHost.  It relies on a
 * trick.  Normally a tab host has a simple API for supplying a View or
 * Intent that each tab will show.  This is not sufficient for switching
 * between pages.  So instead we make the content part of the tab host
 * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
 * view to show as the tab content.  It listens to changes in tabs, and takes
 * care of switch to the correct paged in the ViewPager whenever the selected
 * tab changes.
 *
 * NOTE: This class is lifted directly from the ViewPager class docs:
 *       http://developer.android.com/reference/android/support/v4/view/ViewPager.html
 */
public class TabsAdapter extends FragmentPagerAdapter
        implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
    private final ActionBar mActionBar;
    private final ViewPager mViewPager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>(3);
    private LocationsFragment locationsFragment;
    private FragmentManager tabsFragmentManager;
    private VIPTabBarActivity myActivity;

    static final class TabInfo {
        private final Class<?> clss;
        private final Bundle args;

        TabInfo(Class<?> _class, Bundle _args) {
            clss = _class;
            args = _args;
        }
    }

    public TabsAdapter(FragmentActivity activity, ViewPager pager) {
        super(activity.getSupportFragmentManager());
        myActivity = (VIPTabBarActivity) activity;
        mActionBar = activity.getActionBar();
        tabsFragmentManager = activity.getSupportFragmentManager();
        mViewPager = pager;
        mViewPager.setAdapter(this);
        mViewPager.setOnPageChangeListener(this);
    }

    public void addTab(ActionBar.Tab tab, Class<?> clss, String tag, Bundle args) {
        TabInfo info = new TabInfo(clss, args);
        tab.setTag(tag);
        tab.setTabListener(this);
        mTabs.add(info);
        mActionBar.addTab(tab);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        // return the proper Fragment type given the tab
        switch (position) {
            case 0: {
                return BallotFragment.newInstance();
            }
            case 1: {
                locationsFragment = LocationsFragment.newInstance();
                return locationsFragment;
            }
            case 2: {
                return ElectionDetailsFragment.newInstance();
            }
            default: {
                Log.e("VIPTabBarActivity", "GETTING DEFAULT FRAGMENT FOR POSITION " + position);
                return LocationsFragment.newInstance();
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mActionBar.setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        // Clear back stack on tab change.  Otherwise stack will keep history for all tabs,
        // which can result in inconsistent state and/or unexpected behavior.
        tabsFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        myActivity.clearFragmentHistory();
    }

    /**
     * tell polling locations list to refresh when switching to that tab, to get the
     * geocoding results have returned since its fragment was created
     * (re-select is always triggered on select)
     */
    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        // tell activity which tab has been selected
        String tag = tab.getTag().toString();
        if (tag.equals("ballot_tab")) {
            myActivity.setCurrentFragment(R.id.ballot_fragment);
        } else if (tag.equals("locations_tab")) {
            locationsFragment.refreshList();
            myActivity.setCurrentFragment(R.id.locations_list_fragment);
        } else if (tag.equals("details_tab")) {
            myActivity.setCurrentFragment(R.id.election_details_fragment);
        } else {
            Log.e("TabsAdapter", "Tab " + tag + " is unrecognized!");
        }

    }


}