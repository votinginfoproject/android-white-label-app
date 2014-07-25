package com.votinginfoproject.VotingInformationProject.models;

import java.util.List;

/**
 * Created by kathrynkillebrew on 7/14/14.
 *
 * Local election information for voter's state:
 * https://developers.google.com/civic-information/docs/v1/voterinfo/voterInfoQuery
 */
public class State {
    public String id;
    public String name;
    public ElectionAdministrationBody electionAdministrationBody;
    public AdministrationRegion local_jurisdiction;
    public List<Source> sources;
}
