package com.votinginfoproject.VotingInformationProject.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import com.votinginfoproject.VotingInformationProject.R;

import java.util.Arrays;

/**
 * Created by kathrynkillebrew on 7/14/14.
 * <p/>
 * To Add a new SocialMediaChannel, do the following:
 * 1. Add type returned from the VoterInfoResponse query to getChannelTypes array
 * 2. Add type added to array as a case in getUri()
 * 3. Add image for the social media channel to res/drawable
 * 4. Add TableRow in fragment_candidate where cleantype = type.toLowerCase().trim():
 * As an example for YouTube, cleantype below would be youtube
 * img id: @+id/candidate_social_<cleantype>_image
 * img src: @drawable/<whatever you named it in 3 above>
 * textview id: @+id/candidate_social_<cleantype>_text
 */
public class SocialMediaChannel implements Parcelable {
    public static final Parcelable.Creator<SocialMediaChannel> CREATOR = new Parcelable.Creator<SocialMediaChannel>() {
        public SocialMediaChannel createFromParcel(Parcel pc) {
            return new SocialMediaChannel(pc);
        }

        public SocialMediaChannel[] newArray(int size) {
            return new SocialMediaChannel[size];
        }
    };

    public String id;
    public String type;

    public SocialMediaChannel(Parcel parcel) {
        id = parcel.readString();
        type = parcel.readString();
    }

    /**
     * Default Constructor
     * Initializes all properties to null
     */
    public SocialMediaChannel() {
        this(null, null);
    }

    /**
     * Create a new SocialMediaChannel object with passed id/type.
     * If passed type is not in SocialMediaChannel.getChannelTypes(),
     * SocialMediaChannel properties will be set to null
     *
     * @param id   The unique public ID for the candidate's channel
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
     * @param type String type to clean
     * @return A lower-case, trimmed version of type
     */
    public static String getCleanType(String type) {
        return type.toLowerCase().trim();
    }

    /**
     * @return A string array of the valid social media channel types
     */
    public static String[] getChannelTypes() {
        return new String[]{"Twitter", "Facebook", "GooglePlus", "YouTube"};
    }

    /**
     * @return A lower-case, trimmed version of type
     */
    public String getCleanType() {
        return SocialMediaChannel.getCleanType(type);
    }

    /**
     * @return Uri object with valid URI to candidate social channel website
     * Returns null if invalid type or type == null
     */
    public Uri getUri() {
        if (type == null) {
            return null;
        }

        switch (type) {
            case "Twitter":
                return Uri.parse(id);
            case "Facebook":
                return Uri.parse(id);
            case "GooglePlus":
                return Uri.parse(id);
            case "YouTube":
                return Uri.parse(id);
            default:
                return null;
        }
    }

    @DrawableRes
    public int getDrawable() {
        if (type == null) {
            return -1;
        }

        switch (type) {
            case "Twitter":
                return R.drawable.ic_twitter;
            case "Facebook":
                return R.drawable.ic_facebook;
            case "GooglePlus":
                return R.drawable.ic_google_plus;
            case "YouTube":
                return R.drawable.ic_youtube;
            default:
                return R.drawable.ic_computer;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
    }
}

