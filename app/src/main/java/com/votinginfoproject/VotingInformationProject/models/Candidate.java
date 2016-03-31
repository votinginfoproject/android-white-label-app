package com.votinginfoproject.VotingInformationProject.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kathrynkillebrew on 7/15/14.
 */
public class Candidate {
    public String name;
    public String party;
    public String candidateUrl;
    public String phone;
    @SerializedName("photo_url")
    public String photoUrl;
    public String email;
    public Long orderOnBallot;
    public List<SocialMediaChannel> channels;

    /**
     * Default Constructor
     * <p/>
     * Ensures that the channels List is not-null when the class is instantiated
     */
    public Candidate() {
        this.channels = new ArrayList<>(4);
    }

    /**
     * @param type A string that matches to one of the social channel types
     *             returned by the CivicInfo voterInfo query
     * @return SocialMediaChannel that matches the passed type, or null if not found
     */
    public SocialMediaChannel findChannelForType(String type) {
        String cleanType = SocialMediaChannel.getCleanType(type);
        SocialMediaChannel channel = new SocialMediaChannel();
        for (SocialMediaChannel c : channels) {
            if (c.getCleanType().equals(cleanType)) {
                channel = c;
                break;
            }
        }
        return channel;
    }

    /**
     * @return string description of candidate, for use in candidate list view
     */
    @Override
    public String toString() {
        return name + "\n" + party;
    }
}
