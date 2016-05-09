package com.votinginfoproject.VotingInformationProject.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kathrynkillebrew on 7/15/14.
 */
public class Candidate implements Parcelable {
    public static final Parcelable.Creator<Candidate> CREATOR = new Parcelable.Creator<Candidate>() {
        public Candidate createFromParcel(Parcel pc) {
            return new Candidate(pc);
        }

        public Candidate[] newArray(int size) {
            return new Candidate[size];
        }
    };

    public String name;
    public String party;
    public String phone;
    @SerializedName("photo_url")
    public String email;
    public Long orderOnBallot;
    public List<SocialMediaChannel> channels;

    private String candidateUrl;
    private String photoUrl;

    public Candidate(Parcel parcel) {
        name = parcel.readString();
        party = parcel.readString();
        candidateUrl = parcel.readString();
        phone = parcel.readString();
        photoUrl = parcel.readString();
        email = parcel.readString();
        orderOnBallot = parcel.readLong();
        parcel.readList(channels, SocialMediaChannel.class.getClassLoader());
    }

    /**
     * Default Constructor
     * <p/>
     * Ensures that the channels List is not-null when the class is instantiated
     */
    public Candidate() {
        this.channels = new ArrayList<>(4);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(party);
        dest.writeString(candidateUrl);
        dest.writeString(phone);
        dest.writeString(photoUrl);
        dest.writeString(email);
        dest.writeLong(orderOnBallot);
        dest.writeList(channels);
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

    public String getPhotoUrl() {
        if (photoUrl != null) {
            return photoUrl.toLowerCase();
        }
        return null;
    }

    public String getCandidateUrl() {
        if (candidateUrl != null) {
            return candidateUrl.toLowerCase();
        }
        return null;
    }
}