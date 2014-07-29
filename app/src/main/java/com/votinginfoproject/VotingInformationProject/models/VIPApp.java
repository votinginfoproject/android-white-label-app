package com.votinginfoproject.VotingInformationProject.models;

import android.app.Application;
import android.util.Log;

import java.util.Locale;

/**
 * Created by kathrynkillebrew on 7/18/14.
 *
 * This singleton class can hold data to be used across the application.
 */
public class VIPApp extends Application {
    private VoterInfo voterInfo;

    public VoterInfo getVoterInfo() {
        return voterInfo;
    }

    public void setVoterInfo(VoterInfo info) {
        this.voterInfo = info;
    }

    /**
     * Use metric units if system language is not US English.
     * @return whether to use metric units or not (false -> use imperial)
     */
    public boolean useMetric() {
        if (Locale.getDefault().getISO3Country().equalsIgnoreCase(Locale.US.getISO3Country())) {
            return false;
        } else {
            return true;
        }
    }
}
