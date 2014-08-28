package com.votinginfoproject.VotingInformationProject.models;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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
    public UUID photoCacheKey;

    // keep reference to VoterInfo to put photos in its cache
    private VoterInfo voterInfo;

    /** Default Constructor
     *
     *  Ensures that the channels List is not-null when the class is instantiated
     */
    public Candidate() {
        this.channels = new ArrayList<>(4);
    }

    /**
     *
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
     *
     * @return string description of candidate, for use in candidate list view
     */
    @Override
    public String toString() {
        return name + "\n" + party;
    }

    /**
     * Get photo for this candidate from LRU cache
     *
     * @return bitmap from cache (or null if not in cache)
     */
    public Bitmap getCandidatePhoto() {
        if (photoCacheKey != null) {
            return voterInfo.getImageFromCache(photoCacheKey);
        } else {
            return null;
        }
    }

    /**
     * Add candidate photo to LRU cache
     * @param bitmap image to cache
     */
    public void setCandidatePhoto(Bitmap bitmap) {
        if (voterInfo == null) {
            voterInfo = ((VIPApp)VIPApp.getContext()).getVoterInfo();
        }
        photoCacheKey = voterInfo.addImageToCache(bitmap);
    }
}
