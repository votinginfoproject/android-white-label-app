package com.votinginfoproject.VotingInformationProject.models.singletons;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.votinginfoproject.VotingInformationProject.models.Candidate;
import com.votinginfoproject.VotingInformationProject.models.CivicApiAddress;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Location;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VoterInfoResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by marcvandehey on 3/2/16.
 */
public class VoterInformation {
    private static final String TAG = VoterInformation.class.getSimpleName();

    private static final String ARG_POLLING_LOCATIONS = "polling_locations";
    private static final String ARG_EARLY_LOCATIONS = "early_locations";
    private static final String ARG_DROP_LOCATIONS = "drop_locations";
    private static final String ARG_ALL_LOCATIONS = "all_locations";

    private static final String ARG_SELECTED_ELECTION = "selected_election";
    private static final String ARG_SELECTED_PARTY = "selected_party";

    private static final String ARG_LAST_LOCATION = "last_location";

    private static final String ARG_HOME_ADDRESS = "home_address";
    private static final String ARG_STATE_ADDRESS = "state_admin_address";
    private static final String ARG_LOCAL_ADDRESS = "local_admin_address";

    private static final VoterInformation ourInstance = new VoterInformation();
    ArrayList<PollingLocation> pollingLocations;
    ArrayList<PollingLocation> earlyVotingLocations;
    ArrayList<PollingLocation> dropBoxLocations;
    ArrayList<PollingLocation> allLocations;

    private CivicApiAddress homeAddress;
    private String selectedParty;
    private boolean useMetric;

    private ElectionAdministrationBody stateAdministrationBody;
    private ElectionAdministrationBody localAdministrationBody;

    private CivicApiAddress stateAdminAddress;
    private CivicApiAddress localAdminAddress;

    private ArrayList<Contest> contests;

    private Election selectedElection;

    private Location lastKnownLocation;

    private VoterInformation() {
        this.useMetric = !Locale.getDefault().getISO3Country().equalsIgnoreCase(Locale.US.getISO3Country());

        this.selectedElection = null;

        this.selectedParty = "";

        contests = new ArrayList<>();
        pollingLocations = new ArrayList<>();
        earlyVotingLocations = new ArrayList<>();
        dropBoxLocations = new ArrayList<>();
        allLocations = new ArrayList<>();
    }

    private static VoterInformation getInstance() {
        return ourInstance;
    }

    public static void updateWithVoterInfoResponse(VoterInfoResponse response) {
        if (response.contests != null) {
            ourInstance.contests = new ArrayList<>(response.contests);
        }

        response.setUpLocations();
    }

    public static void clear() {
        ourInstance.selectedElection = null;
        ourInstance.selectedParty = "";
        ourInstance.contests = new ArrayList<>();
        ourInstance.pollingLocations = new ArrayList<>();
        ourInstance.earlyVotingLocations = new ArrayList<>();
        ourInstance.dropBoxLocations = new ArrayList<>();
        ourInstance.allLocations = new ArrayList<>();
        ourInstance.homeAddress = null;
        ourInstance.localAdministrationBody = null;
        ourInstance.stateAdministrationBody = null;
    }

    public static Election getElection() {
        return ourInstance.selectedElection;
    }

    public static void setElection(Election election) {
        ourInstance.selectedElection = election;
    }

    public static CivicApiAddress getHomeAddress() {
        return ourInstance.homeAddress;
    }

    public static void setHomeAddress(CivicApiAddress homeAddress) {
        ourInstance.homeAddress = homeAddress;
    }

    public static boolean useMetric() {
        return getInstance().useMetric;
    }

    public static String getSelectedParty() {
        return ourInstance.selectedParty;
    }

    public static void setPartyFilter(String selectedParty) {
        if (selectedParty != null) {
            ourInstance.selectedParty = selectedParty;
        } else {
            ourInstance.selectedParty = "";
        }
    }

    public static ElectionAdministrationBody getStateAdministrationBody() {
        return ourInstance.stateAdministrationBody;
    }

    public static void setStateAdministrationBody(ElectionAdministrationBody stateAdministrationBody) {
        ourInstance.stateAdministrationBody = stateAdministrationBody;
    }

    public static ElectionAdministrationBody getLocalAdministrationBody() {
        return ourInstance.localAdministrationBody;
    }

    public static void setLocalAdministrationBody(ElectionAdministrationBody localAdministrationBody) {
        ourInstance.localAdministrationBody = localAdministrationBody;
    }

    public static ArrayList<Contest> getContests() {
        if (getSelectedParty().isEmpty()) {
            return ourInstance.contests;
        } else {
            ArrayList<Contest> contests = new ArrayList<>();

            Log.v("selected party", "Selected Party: " + getSelectedParty());

            for (Contest contest : ourInstance.contests) {
                Contest contestCopy = contest.copy();
                contestCopy.candidates = new ArrayList<>();

                for (int i = 0; i < contest.candidates.size(); i++) {
                    Candidate candidate = contest.candidates.get(i);

                    //Remove candidate from the list that does not belong to the selected party
                    if (candidate.party != null && candidate.party.equals(getSelectedParty())) {
                        contestCopy.candidates.add(candidate);
                    }
                }

                if (contestCopy.candidates.size() > 0) {
                    contests.add(contestCopy);
                }
            }

            return contests;
        }
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

    //Save User Preferences State and bind it to needed activity
    public static void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(ARG_POLLING_LOCATIONS, ourInstance.pollingLocations);
        outState.putParcelableArrayList(ARG_EARLY_LOCATIONS, ourInstance.earlyVotingLocations);
        outState.putParcelableArrayList(ARG_DROP_LOCATIONS, ourInstance.dropBoxLocations);
        outState.putParcelableArrayList(ARG_ALL_LOCATIONS, ourInstance.allLocations);

        outState.putParcelable(ARG_SELECTED_ELECTION, ourInstance.selectedElection);
        outState.putString(ARG_SELECTED_PARTY, ourInstance.selectedParty);

        outState.putParcelable(ARG_LAST_LOCATION, ourInstance.lastKnownLocation);

        outState.putParcelable(ARG_HOME_ADDRESS, ourInstance.homeAddress);
        outState.putParcelable(ARG_STATE_ADDRESS, ourInstance.stateAdminAddress);
        outState.putParcelable(ARG_LOCAL_ADDRESS, ourInstance.localAdminAddress);
    }

    //Restore User Preferences State if available
    public static void onRestoreInstanceState(Bundle savedInstance) {
        ourInstance.useMetric = !Locale.getDefault().getISO3Country().equalsIgnoreCase(Locale.US.getISO3Country());

        ourInstance.pollingLocations = savedInstance.getParcelableArrayList(ARG_POLLING_LOCATIONS);
        ourInstance.earlyVotingLocations = savedInstance.getParcelableArrayList(ARG_EARLY_LOCATIONS);
        ourInstance.dropBoxLocations = savedInstance.getParcelableArrayList(ARG_DROP_LOCATIONS);
        ourInstance.allLocations = savedInstance.getParcelableArrayList(ARG_ALL_LOCATIONS);

        ourInstance.selectedElection = savedInstance.getParcelable(ARG_SELECTED_ELECTION);
        ourInstance.selectedParty = savedInstance.getString(ARG_SELECTED_PARTY, "");

        ourInstance.lastKnownLocation = savedInstance.getParcelable(ARG_LAST_LOCATION);

        ourInstance.homeAddress = savedInstance.getParcelable(ARG_HOME_ADDRESS);
        ourInstance.stateAdminAddress = savedInstance.getParcelable(ARG_STATE_ADDRESS);
        ourInstance.localAdminAddress = savedInstance.getParcelable(ARG_LOCAL_ADDRESS);
    }

    @Nullable
    public static Location getLastKnownLocation() {
        return getInstance().lastKnownLocation;
    }

    public static void setLastKnownLocation(Location lastKnownLocation) {
        getInstance().lastKnownLocation = lastKnownLocation;
    }
}
