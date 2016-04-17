package com.votinginfoproject.VotingInformationProject.models.singletons;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.votinginfoproject.VotingInformationProject.models.CivicApiAddress;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by marcvandehey on 3/2/16.
 */
public class UserPreferences {
    private static final String TAG = UserPreferences.class.getSimpleName();

    private static UserPreferences ourInstance = new UserPreferences();
    ArrayList<PollingLocation> pollingLocations;
    ArrayList<PollingLocation> earlyVotingLocations;
    ArrayList<PollingLocation> dropBoxLocations;
    ArrayList<PollingLocation> allLocations;
    private String selectedParty;
    private boolean useMetric;
    private Location homeLocation;
    private VoterInfo voterInfo;
    private CivicApiAddress stateAdminAddress;
    private CivicApiAddress localAdminAddress;

    private UserPreferences() {
        this.useMetric = !Locale.getDefault().getISO3Country().equalsIgnoreCase(Locale.US.getISO3Country());
        this.homeLocation = null;
        this.voterInfo = null;
        this.selectedParty = "";

        pollingLocations = new ArrayList<>();
        earlyVotingLocations = new ArrayList<>();
        dropBoxLocations = new ArrayList<>();
        allLocations = new ArrayList<>();
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

    public static CivicApiAddress getStateAdminAddress() {
        return ourInstance.stateAdminAddress;
    }

    public static void setStateAdminAddress(CivicApiAddress stateAdminAddress) {
        ourInstance.stateAdminAddress = stateAdminAddress;
    }

    public static CivicApiAddress getLocalAdminAddress() {
        return ourInstance.localAdminAddress;
    }

    public static void setLocalAdminAddress(CivicApiAddress localAdminAddress) {
        ourInstance.localAdminAddress = localAdminAddress;
    }

    public static void setPollingLocations(ArrayList<PollingLocation> pollingLocations, ArrayList<PollingLocation> earlyVotingLocations, ArrayList<PollingLocation> dropBoxLocations) {
        ourInstance.pollingLocations = pollingLocations;
        ourInstance.earlyVotingLocations = earlyVotingLocations;
        ourInstance.dropBoxLocations = dropBoxLocations;

        ArrayList<PollingLocation> sortableLocations = new ArrayList<>();
        ArrayList<PollingLocation> noCoordinatesAddress = new ArrayList<>();

        //Separate all polling locations by those with and without coordinates
        for (PollingLocation pollingLocation : pollingLocations) {
            if (pollingLocation.distance != -1) {
                sortableLocations.add(pollingLocation);
            } else {
                noCoordinatesAddress.add(pollingLocation);
            }
        }

        for (PollingLocation pollingLocation : earlyVotingLocations) {
            if (pollingLocation.distance != -1) {
                sortableLocations.add(pollingLocation);
            } else {
                noCoordinatesAddress.add(pollingLocation);
            }
        }

        for (PollingLocation pollingLocation : dropBoxLocations) {
            if (pollingLocation.distance != -1) {
                sortableLocations.add(pollingLocation);
            } else {
                noCoordinatesAddress.add(pollingLocation);
            }
        }

        //Sort polling locations with coordinates by distance from home and those without by location name

        Collections.sort(sortableLocations, new Comparator<PollingLocation>() {
            @Override
            public int compare(PollingLocation lhs, PollingLocation rhs) {
                if (lhs.distance == rhs.distance) {
                    return lhs.address.locationName.compareTo(rhs.address.locationName);
                } else {
                    return (lhs.distance < rhs.distance) ? -1 : 1;
                }
            }
        });

        Collections.sort(noCoordinatesAddress, new Comparator<PollingLocation>() {
            @Override
            public int compare(PollingLocation lhs, PollingLocation rhs) {
                return lhs.address.locationName.compareTo(rhs.address.locationName);
            }
        });

        //Append Distance sorted list with alphabetically sorted list
        sortableLocations.addAll(noCoordinatesAddress);

        ourInstance.allLocations = sortableLocations;

        for (PollingLocation location : ourInstance.allLocations) {
            Log.v(TAG, location.distance + " " + location.address.locationName);
        }
    }

    public static ArrayList<PollingLocation> getAllPollingLocations() {
        return ourInstance.allLocations;
    }

    public static ArrayList<PollingLocation> getPollingLocations() {
        return ourInstance.pollingLocations;
    }

    public static ArrayList<PollingLocation> getEarlyVotingLocations() {
        return ourInstance.earlyVotingLocations;
    }

    public static ArrayList<PollingLocation> getDropBoxLocations() {
        return ourInstance.dropBoxLocations;
    }
}
