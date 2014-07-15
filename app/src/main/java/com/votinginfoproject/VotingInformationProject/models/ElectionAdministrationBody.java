package com.votinginfoproject.VotingInformationProject.models;

import java.util.List;

/**
 * Created by kathrynkillebrew on 7/14/14.
 * https://developers.google.com/civic-information/docs/v1/voterinfo/voterInfoQuery
 */
public class ElectionAdministrationBody {
    public String name;
    public String electionInfoUrl;
    public String electionRegistrationUrl;
    public String electionRegistrationConfirmationUrl;
    public String absenteeVotingInfoUrl;
    public String votingLocationFinderUrl;
    public String ballotInfoUrl;
    public String electionRulesUrl;
    public List<String> voter_services;
    public String hoursOfOperation;
    public Address correspondenceAddress;
    public Address physicalAddress;
    public List<ElectionOfficial> electionOfficials;
}
