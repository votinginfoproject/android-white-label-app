package com.votinginfoproject.VotingInformationProject.models.singletons;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.votinginfoproject.VotingInformationProject.R;

import java.util.HashMap;

/**
 * Created by marcvandehey on 3/2/16.
 */
public class GATracker {
    private static GATracker ourInstance = new GATracker();

    private HashMap<TrackerName, Tracker> trackers = new HashMap<>();

    public static void registerTracker(Context context, TrackerName trackerId) {
        if (ourInstance.trackers.get(trackerId) == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);

            //Google Analytics API Key required.
            // Please reference the "Adding API keys for the app" section of the Readme for more details.
            Tracker t = analytics.newTracker(R.xml.app_tracker);
            ourInstance.trackers.put(trackerId, t);
        }
    }

    public static synchronized Tracker getTracker(TrackerName trackerId) {
        return ourInstance.trackers.get(trackerId);
    }

    /**
     * Enum used to identify the Google Analytics tracker.
     * <p/>
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker for this app.  To add other trackers, name them in this enum.
    }
}
