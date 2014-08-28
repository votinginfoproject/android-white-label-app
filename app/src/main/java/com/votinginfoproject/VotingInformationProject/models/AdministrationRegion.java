package com.votinginfoproject.VotingInformationProject.models;

import java.util.List;

/**
 * Created by kathrynkillebrew on 7/14/14.
 */
public class AdministrationRegion {
    public String id;
    public String name;
    public ElectionAdministrationBody electionAdministrationBody;
    public List<Source> sources;
}
