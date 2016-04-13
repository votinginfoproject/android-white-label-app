package com.votinginfoproject.VotingInformationProject.models.singletons;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.util.Locale;

/**
 * Created by marcvandehey on 3/2/16.
 */
public class UserPreferences {
    private static final String TAG = UserPreferences.class.getSimpleName();

    private static UserPreferences ourInstance = new UserPreferences();

    private String selectedParty;

    private boolean useMetric;
    private Location homeLocation;
    private VoterInfo voterInfo;

    private UserPreferences() {
        this.useMetric = !Locale.getDefault().getISO3Country().equalsIgnoreCase(Locale.US.getISO3Country());
        this.homeLocation = null;
        this.voterInfo = null;
        this.selectedParty = "";
    }

    private static UserPreferences getInstance() {
        return ourInstance;
    }

    public static Location getHomeLocation() {
        return getInstance().homeLocation;
    }

    public static void setHomeLocation(Location homeLocation) {
        getInstance().homeLocation = homeLocation;
    }

    public static LatLng getHomeLatLong() {
        double latitude = 0.0;
        double longitude = 0.0;

        if (getInstance().homeLocation != null) {
            latitude = getInstance().homeLocation.getLatitude();
            longitude = getInstance().homeLocation.getLongitude();
        }

        return new LatLng(latitude, longitude);
    }

    public static boolean useMetric() {
        return getInstance().useMetric;
    }

    public static VoterInfo getVoterInfo() {
        return getInstance().voterInfo;
    }

    public static void setVoterInfo(VoterInfo voterInfo) {
        voterInfo.setUpLocations();
        getInstance().voterInfo = voterInfo;
    }

    public static String getSelectedParty() {
        return ourInstance.selectedParty;
    }

    public static void setSelectedParty(String selectedParty) {
        ourInstance.selectedParty = selectedParty;
    }
}
