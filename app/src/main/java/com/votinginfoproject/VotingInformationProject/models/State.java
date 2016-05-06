package com.votinginfoproject.VotingInformationProject.models;

/**
 * Created by kathrynkillebrew on 7/14/14.
 * <p/>
 * Local election information for voter's state:
 * https://developers.google.com/civic-information/docs/v1/voterinfo/voterInfoQuery
 */
public class State {
    public String id;
    public String name;
    public ElectionAdministrationBody electionAdministrationBody;
    public AdministrationRegion local_jurisdiction;
}
