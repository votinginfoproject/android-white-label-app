package com.votinginfoproject.VotingInformationProject.models;

import android.app.Application;
import android.content.Context;
import java.util.Locale;

/**
 * Created by kathrynkillebrew on 7/18/14.
 *
 * This singleton class can hold data to be used across the application.
 */
public class VIPApp extends Application {
    private VoterInfo voterInfo;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }

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
        return !Locale.getDefault().getISO3Country().equalsIgnoreCase(Locale.US.getISO3Country());
    }
}
