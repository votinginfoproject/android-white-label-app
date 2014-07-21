package com.votinginfoproject.VotingInformationProject.models;

import android.app.Application;

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
}
