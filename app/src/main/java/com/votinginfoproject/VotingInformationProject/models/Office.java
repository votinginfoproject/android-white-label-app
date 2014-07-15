package com.votinginfoproject.VotingInformationProject.models;

import java.util.List;

/**
 * Created by kathrynkillebrew on 7/14/14.
 * https://developers.google.com/civic-information/docs/v1/representatives#resource
 */
public class Office {
    public String divisionId;
    public List<String> level;
    public String name;
    public List<String> officialIndexes;
    public List<String> roles;
    public List<Source> sources;
}
