package com.votinginfoproject.VotingInformationProject.application;

import android.app.Application;
import android.location.Location;

import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.singletons.GATracker;

/**
 * Created by kathrynkillebrew on 7/18/14.
 * <p/>
 * This singleton class can hold data to be used across the application.
 */
public class VIPApp extends Application {
    private VoterInfo voterInfo;
    private Location homeLocation;

    @Override
    public void onCreate() {
        super.onCreate();

        GATracker.registerTracker(this, GATracker.TrackerName.APP_TRACKER);
    }
}
