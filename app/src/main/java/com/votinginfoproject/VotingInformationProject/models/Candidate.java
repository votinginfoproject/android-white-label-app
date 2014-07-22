package com.votinginfoproject.VotingInformationProject.models;

import java.util.List;

/**
 * Created by kathrynkillebrew on 7/15/14.
 */
public class Candidate {
    public String name;
    public String party;
    public String candidateUrl;
    public String phone;
    public String photoUrl;
    public String email;
    public Long orderOnBallot;
    public List<SocialMediaChannel> channels;

    /**
     *
     * @return string description of candidate, for use in candidate list view
     */
    @Override
    public String toString() {
        return name + "\n" + party;
    }
}
