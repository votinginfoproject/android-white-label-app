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
    public List <String> level;
    public List <String> roles;
    public District district;
    public Long numberElected;
    public Long numberVotingFor;
    public Long ballotPlacement;
    public List<Candidate> candidates;
    public String referendumTitle;
    public String referendumSubtitle;
    public String referendumUrl;
    public String referendumBrief;
    public String referendumText;
    public String referendumProStatement;
    public String referendumConStatement;
    public String referendumPassageThreshold;
    public String referendumEffectOfAbstain;
    public List <Source> sources;
}
