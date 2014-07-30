package com.votinginfoproject.VotingInformationProject.models;

import android.content.Context;

/**
 * This class abstracts fetching the application, so that the application context
 * may be mocked for testing.
 *
 * Created by kathrynkillebrew on 7/23/14.
 */
public class VIPAppContext {
    private VIPApp app;

    public VIPAppContext() {}

    /**
     *
     * @param vip_app Initialize from within the app with (VIPApp) getApplicationContext()
     */
    public VIPAppContext(VIPApp vip_app) {
        app = vip_app;
    }

    public VIPApp getVIPApp() {
        return app;
    }

    public void setVIPApp(VIPApp vip_app) {
        app = vip_app;
    }

    /**
     * For testing, override this method to return a mock context
     * @return this application's context
     */
    public static Context getContext() {
        return VIPApp.getContext();
    }

    public boolean useMetric() {
        return app.useMetric();
    }
}
