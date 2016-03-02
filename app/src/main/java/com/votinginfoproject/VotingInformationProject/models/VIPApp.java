package com.votinginfoproject.VotingInformationProject.models;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.votinginfoproject.VotingInformationProject.R;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by kathrynkillebrew on 7/18/14.
 *
 * This singleton class can hold data to be used across the application.
 */
public class VIPApp extends Application {
    private VoterInfo voterInfo;
    private static Context mContext;
    private Location homeLocation;

    /**
     * Enum used to identify the Google Analytics tracker.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker for this app.  To add other trackers, name them in this enum.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap();

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(R.xml.app_tracker);
            mTrackers.put(trackerId, t);
        }

        return mTrackers.get(trackerId);
    }

    public static Context getContext(){
        return mContext;
    }

    public VoterInfo getVoterInfo() {
        return voterInfo;
    }

    public void setVoterInfo(VoterInfo info, String party) {
        this.voterInfo = info;

        if (info != null) {
            this.voterInfo.setSelectedParty(party);
        } else {
            Log.d("VIPApp", "VoterInfo object is null");
        }
    }

    public void setSelectedParty(String party) {
        if (voterInfo != null) {
            voterInfo.setSelectedParty(party);
        } else {
            Log.d("VIPApp", "Cannot set party on VoterInfo because voterInfo is null");
        }
    }

    public void setHomeLocation(Location location) {
        this.homeLocation = location;
    }

    public Location getHomeLocation() {
        return homeLocation;
    }

    /**
     * Use metric units if system language is not US English.
     * @return whether to use metric units or not (false -> use imperial)
     */
    public boolean useMetric() {
        return !Locale.getDefault().getISO3Country().equalsIgnoreCase(Locale.US.getISO3Country());
    }
}
