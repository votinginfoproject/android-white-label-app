package com.votinginfoproject.VotingInformationProject.models;

import android.net.Uri;

import java.util.Arrays;

/**
 * Created by kathrynkillebrew on 7/14/14.
 *
 * To Add a new SocialMediaChannel, do the following:
 * 1. Add type returned from the VoterInfo query to getChannelTypes array
 * 2. Add type added to array as a case in getUri()
 * 3. Add image for the social media channel to res/drawable
 * 4. Add TableRow in fragment_candidate where cleantype = type.toLowerCase().trim():
 *    As an example for YouTube, cleantype below would be youtube
 *      img id: @+id/candidate_social_<cleantype>_image
 *      img src: @drawable/<whatever you named it in 3 above>
 *      textview id: @+id/candidate_social_<cleantype>_text
 */
public class SocialMediaChannel {
    public String id;
    public String type;

    /** Default Constructor
     * Initializes all properties to null
     */
    public SocialMediaChannel() {
        this(null, null);
    }

    /** Create a new SocialMediaChannel object with passed id/type.
     *  If passed type is not in SocialMediaChannel.getChannelTypes(),
     *  SocialMediaChannel properties will be set to null
     *
     * @param id The unique public ID for the candidate's channel
     * @param type Type of channel. Valid channels defined in getChannelTypes()
     */
    public SocialMediaChannel(String id, String type) {
        if (Arrays.asList(SocialMediaChannel.getChannelTypes()).contains(type)) {
            this.id = id;
            this.type = type;
        } else {
            this.id = null;
            this.type = null;
        }
    }

    /**
     *
     * @return A lower-case, trimmed version of type
     */
    public String getCleanType () {
        return SocialMediaChannel.getCleanType(type);
    }

    /**
     *
     * @param type String type to clean
     * @return A lower-case, trimmed version of type
     */
    public static String getCleanType(String type) {
        return type.toLowerCase().trim();
    }

    /**
     *
     * @return Uri object with valid URI to candidate social channel website
     *         Returns null if invalid type or type == null
     */
    public Uri getUri() {
        if (type == null) {
            return null;
        }
        switch (type) {
            case "Twitter":
                return Uri.parse("https://twitter.com/" + id);
            case "Facebook":
                return Uri.parse("https://facebook.com/" + id);
            case "GooglePlus":
                return Uri.parse("https://plus.google.com/+" + id);
            case "YouTube":
                return Uri.parse("https://www.youtube.com/user/" + id);
            default:
                return null;
        }
    }

    /**
     *
     * @return A string array of the valid social media channel types
     */
    public static String[] getChannelTypes() {
        return new String[] {"Twitter", "Facebook", "GooglePlus", "YouTube"};
    }
}

