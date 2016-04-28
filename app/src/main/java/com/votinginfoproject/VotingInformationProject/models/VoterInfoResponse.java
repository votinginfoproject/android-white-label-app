package com.votinginfoproject.VotingInformationProject.models;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.votinginfoproject.VotingInformationProject.models.api.requests.RequestType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kathrynkillebrew on 7/14/14.
 * VoterInfoResponse object for voterInfoQuery:
 * https://developers.google.com/civic-information/docs/v1/voterinfo/voterInfoQuery
 */
public class VoterInfoResponse implements RequestType {
    // limit drop box locations to first 50, and polling and early voting sites to first 25
    private transient static final int MAX_VOTING_SITES = 25;
    private transient static final int MAX_DROP_BOX_SITES = 50;

    private transient final String TAG = VoterInfoResponse.class.getSimpleName();

    public String kind;
    public Election election;
    public List<Election> otherElections;
    public CivicApiAddress normalizedInput;
    public ArrayList<PollingLocation> pollingLocations;
    public ArrayList<PollingLocation> earlyVoteSites;
    public List<PollingLocation> dropOffLocations;
    public List<Contest> contests;
    public List<State> state;

    private HashMap<String, Integer> locationIds;
    private ArrayList<PollingLocation> allLocations;
    private ArrayList<PollingLocation> openEarlyVoteSites;
    private ArrayList<PollingLocation> openDropOffLocations;
    private ArrayList<PollingLocation> usePollingLocations;
    private boolean mailOnly;

    private CivicApiError error;

    /**
     * Default Constructor
     * <p/>
     * Ensures otherElections is never a null field, and create photo cache for candidate images
     */
    public VoterInfoResponse() {
        this.otherElections = new ArrayList<>();
    }

    public ArrayList<String> getUniqueParties() {
        ArrayList<String> parties = new ArrayList<>();

        if (contests != null) {
            for (Contest contest : contests) {
                for (Candidate candidate : contest.candidates) {
                    if (!candidate.party.isEmpty() && !parties.contains(candidate.party)) {
                        parties.add(candidate.party);
                    }
                }
            }
        }

        return parties;
    }

    public List<Contest> getFilteredContestsForParty(String party) {
        if (contests == null) return new ArrayList<>();

        ArrayList<Contest> filteredContests = new ArrayList<>(contests.size());

        Log.d(TAG, "Filtering contest list for party: " + party);

        // build filtered list of contests based on party
        if (contests != null) {
            // filter contest list for primary party
            if (!party.isEmpty()) {
                for (Contest contest : contests) {
                    if (contest.primaryParty != null && !contest.primaryParty.isEmpty()) {
                        if (contest.primaryParty.equals(party)) {
                            filteredContests.add(contest);
                        }
                    } else {
                        // this contest isn't a primary; show it
                        filteredContests.add(contest);
                    }
                }
            } else {
                // no selected party; show all
                filteredContests.addAll(contests);
            }
        } else {
            Log.d(TAG, "No contests for this election!");
        }

        return filteredContests;
    }

    /**
     * Helper function to be called by main activity once VoterInfoResponse contents have been populated;
     * this will build map of polling/early voting locations by key.
     */
    public void setUpLocations() {
        // get all locations (both polling and early voting)
        allLocations = new ArrayList<>();
        if (pollingLocations != null) {
            // only show first polling locations
            if (pollingLocations.size() > MAX_VOTING_SITES) {
                usePollingLocations = new ArrayList<>(pollingLocations.subList(0, MAX_VOTING_SITES));
            } else {
                usePollingLocations = new ArrayList<>(pollingLocations);

                Collections.copy(usePollingLocations, pollingLocations);
            }

            allLocations.addAll(usePollingLocations);
        } else {
            usePollingLocations = new ArrayList<>();
        }

        if (earlyVoteSites != null) {
            for (PollingLocation location : earlyVoteSites) {
                location.setPollingLocationType(PollingLocation.POLLING_TYPE_EARLY_VOTE);
            }
        }

        if (dropOffLocations != null) {
            for (PollingLocation location : dropOffLocations) {
                location.setPollingLocationType(PollingLocation.POLLING_TYPE_DROP_BOX);
            }
        }

        // only display early voting sites and drop-off locations that haven't closed yet
        // only show first 25 early voting sites found, and first 50 drop-boxes
        openEarlyVoteSites = buildOpenSitesList(earlyVoteSites, MAX_VOTING_SITES);
        openDropOffLocations = buildOpenSitesList(dropOffLocations, MAX_DROP_BOX_SITES);

        Log.d(TAG, "Using " + openEarlyVoteSites.size() + " open early voting sites");
        Log.d(TAG, "Using " + openDropOffLocations.size() + " open drop-off locations");

        allLocations.addAll(openDropOffLocations);
        allLocations.addAll(openEarlyVoteSites);

        // Build map of PollingLocation id to its offset in the list of all locations,
        // to find it later when the distance calculation comes back.
        locationIds = new HashMap<>(allLocations.size());
        for (int i = allLocations.size(); i-- > 0; ) {
            PollingLocation location = allLocations.get(i);

            if (location.id != null) {
                locationIds.put(location.id, i);
            } else {
                // key by address if location has no ID
                locationIds.put(location.address.toGeocodeString(), i);
            }
        }
    }

    public ArrayList<PollingLocation> getPollingLocations() {
        return usePollingLocations;
    }

    /**
     * Helper function to build a list of open polling locations from a given list
     *
     * @param fromList List of early voting or drop-off locations to check
     * @param limit    Maximum number of locations to return for this location type
     * @return List of locations found that have not closed yet
     */
    private ArrayList<PollingLocation> buildOpenSitesList(List<PollingLocation> fromList, int limit) {
        ArrayList<PollingLocation> returnList = new ArrayList<>();

        if (fromList != null) {
            Date today = election.getCurrentDay();
            int counter = 0;

            for (PollingLocation loc : fromList) {
                counter += 1;

                if (counter > limit) {
                    return returnList;
                }

                Date start = election.getDayFromString(loc.startDate);
                Date end = election.getDayFromString(loc.endDate);

                if (start == null || end == null) {
                    // missing site start/date, so just display it
                    returnList.add(loc);
                } else if (!end.before(today)) {
                    //  closes today or later
                    returnList.add(loc);
                }
            }
        }
        return returnList;
    }

    public ArrayList<PollingLocation> getOpenDropOffLocations() {
        return openDropOffLocations;
    }

    public ArrayList<PollingLocation> getOpenEarlyVoteSites() {
        return openEarlyVoteSites;
    }

    /**
     * Helper function to find the state administrative body.  For elections in the US, there is
     * only one state.
     *
     * @return state election administration body (or null if there is none)
     */
    public ElectionAdministrationBody getStateAdmin() {
        if (state != null && state.get(0).electionAdministrationBody != null) {
            Log.d(TAG + ":getStateAdmin", "state is: " + state.size());

            return state.get(0).electionAdministrationBody;
        }

        Log.d(TAG + ":getStateAdmin", "there is no state or address!");

        return null;
    }

    /**
     * Helper function to find the local administrative body.  For elections in the US, there is
     * only one state.
     *
     * @return local election administration body (or null if not found)
     */
    public ElectionAdministrationBody getLocalAdmin() {
        if (state != null) {
            State thisState = state.get(0);

            if (thisState != null && thisState.local_jurisdiction != null && thisState.local_jurisdiction.electionAdministrationBody != null) {
                return thisState.local_jurisdiction.electionAdministrationBody;
            }
        }

        return null;
    }

    /**
     * Helper function to get the physical address for state or local administration body
     *
     * @param admin_type ElectionAdministrationBody.AdminBody string constant for state or local
     * @return CivicApiAddress that is the physical address for the admin body (or null if not found)
     */
    public CivicApiAddress getAdminAddress(String admin_type) {
        ElectionAdministrationBody admin = null;

        if (admin_type.equals(ElectionAdministrationBody.AdminBody.STATE)) {
            admin = getStateAdmin();
        } else if (admin_type.equals(ElectionAdministrationBody.AdminBody.LOCAL)) {
            admin = getLocalAdmin();
        }

        if (admin != null) {
            return admin.getPhysicalAddress();
        }

        return null;
    }

    public ArrayList<PollingLocation> getAllLocations() {
        return allLocations;
    }

    /**
     * Find polling/early voting location in the map of all locations
     *
     * @param location_id Key for location (either its ID or address, if no ID)
     * @return PollingLocation in the map (or null if not found)
     */
    public PollingLocation getLocationForId(String location_id) {
        if (locationIds.get(location_id) != null) {
            return allLocations.get(locationIds.get(location_id));
        } else {
            Log.e(TAG, "Did not find location ID in hash: " + location_id);

            return null;
        }
    }

    /**
     * Helper function to return address object for either polling location or election admin body.
     *
     * @param location_id Key for polling location, or which admin body
     * @return Address object for key
     */
    public CivicApiAddress getAddressForId(String location_id) {
        if (locationIds.get(location_id) != null) {
            PollingLocation location = allLocations.get(locationIds.get(location_id));
            return location.address;
        } else {
            // get address for election administration body key (state or local)
            return getAdminAddress(location_id);
        }
    }

    /**
     * Helper function to return descriptor for either polling location or election admin body.
     *
     * @param location_id Key for polling location, or which admin body
     * @return String descriptor for key
     */
    public String getDescriptionForId(String location_id) {
        if (locationIds.get(location_id) != null) {
            PollingLocation location = allLocations.get(locationIds.get(location_id));
            if (location.name != null) {
                return location.name;
            }
        } else if (location_id.equals(ElectionAdministrationBody.AdminBody.STATE)) {
            ElectionAdministrationBody stateAdmin = getStateAdmin();
            if (stateAdmin != null && stateAdmin.name != null) {
                return stateAdmin.name;
            }
        } else if (location_id.equals(ElectionAdministrationBody.AdminBody.LOCAL)) {
            ElectionAdministrationBody localAdmin = getLocalAdmin();
            if (localAdmin != null && localAdmin.name != null) {
                return localAdmin.name;
            }
        }

        return "";
    }

    /**
     * Get the co-ordinates of an election administration body. (Call after it's been geocoded.)
     *
     * @param body ElectionAdministrationBody.AdminBody string constant for type (state or local)
     * @return LatLng for admin body's physical address, if it has one
     */
    public LatLng getAdminBodyLatLng(String body) {
        CivicApiAddress address = getAdminAddress(body);
        if (address != null) {
            return new LatLng(address.latitude, address.longitude);
        }

        return null;
    }

    /**
     * Specifies whether voters in the precinct vote only by mailing their ballots (with the
     * possible option of dropping off their ballots as well).
     */
    public boolean isMailOnly() {
        return mailOnly;
    }

    /**
     * Get the error if present
     *
     * @return
     */
    public CivicApiError getError() {
        return error;
    }

    /**
     * Checks to see if there is an error, if an error is present will return false
     *
     * @return
     */
    public boolean isSuccessful() {
        return error == null;
    }

    @Override
    public String buildQueryString() {
        return null;
    }
}
