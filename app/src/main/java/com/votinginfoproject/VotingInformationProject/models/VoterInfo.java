package com.votinginfoproject.VotingInformationProject.models;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by kathrynkillebrew on 7/14/14.
 * Response object for voterInfoQuery:
 * https://developers.google.com/civic-information/docs/v1/voterinfo/voterInfoQuery
 */
public class VoterInfo {
    public String kind;
    public Election election;
    public List<Election> otherElections;
    public CivicApiAddress normalizedInput;
    public List<PollingLocation> pollingLocations;
    public List<PollingLocation> earlyVoteSites;
    public List<Contest> contests;
    public List<State> state;

    private CandidatePhotoCache candidatePhotoCache;
    private String selectedParty;

    public List<Contest> getFilteredContests() {
        return filteredContests;
    }

    /**
     * Find a contest at a particular offset in the party-filtered list.  For use with list item
     * found in ContestsAdapter list.
     *
     * @param position Index in the filteredContest list
     * @return Contest at the given index
     */
    public Contest getContestAt(int position) {
        return filteredContests.get(position);
    }

    private List<Contest> filteredContests;

    public String getSelectedParty() {
        return selectedParty;
    }

    public void setSelectedParty(String party) {
        this.selectedParty = party;

        Log.d("VoterInfo", "Filtering contest list for party: " + party);

        // build filtered list of contests based on party
        filteredContests = new ArrayList<Contest>(contests.size());
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

        Log.d("VoterInfo", "Have filtered contests:");
        for (Contest contest : filteredContests) {
            Log.d("VoterInfo", "Contest of type: " + contest.type);
        }
    }

    /**
     * Default Constructor
     *
     * Ensures otherElections is never a null field, and create photo cache for candidate images
     */
    public VoterInfo() {
        this.otherElections = new ArrayList<Election>();
        candidatePhotoCache = new CandidatePhotoCache();
    }

    public Bitmap getImageFromCache(UUID key) {
        return candidatePhotoCache.getBitmapFromMemCache(key);
    }

    public UUID addImageToCache(Bitmap bitmap) {
        return candidatePhotoCache.addBitmapToMemoryCache(bitmap);
    }
}
