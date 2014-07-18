package com.votinginfoproject.VotingInformationProject.models;

import java.util.List;

/**
 * Created by kathrynkillebrew on 7/14/14.
 * https://developers.google.com/civic-information/docs/v1/voterinfo/voterInfoQuery
 */
public class Contest {
    public String id;
    public String type;
    public String primaryParty;
    public String electorateSpecifications;
    public String special;
    public String office;
    public String level;
    public District district;
    public Long numberElected;
    public Long numberVotingFor;
    public Long ballotPlacement;
    public List<Candidate> candidates;
    public String referendumTitle;
    public String referendumSubtitle;
    public String referendumUrl;
    public List <Source> sources;

    /**
     * TODO: populate with more appropriate fields (referendumTitle and subtitle are empty in test data)
     * @return string description of contest, for use in contest list view
     */
    @Override
    public String toString() {
        return office + "\n" + type;
    }
}
