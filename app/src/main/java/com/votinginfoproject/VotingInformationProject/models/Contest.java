package com.votinginfoproject.VotingInformationProject.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by kathrynkillebrew on 7/14/14.
 * https://developers.google.com/civic-information/docs/v1/voterinfo/voterInfoQuery
 */
public class Contest implements Parcelable {
    public static final Parcelable.Creator<Contest> CREATOR = new Parcelable.Creator<Contest>() {
        public Contest createFromParcel(Parcel pc) {
            return new Contest(pc);
        }

        public Contest[] newArray(int size) {
            return new Contest[size];
        }
    };
    public String id;
    public String type;
    public String primaryParty;
    public String electorateSpecifications;
    public String special;
    public String office;
    public List<String> level;
    public List<String> roles;
    public District district;
    public Long numberElected;
    public Long numberVotingFor;
    public Long ballotPlacement;
    public List<Candidate> candidates;

    public String referendumTitle;
    public String referendumSubtitle;
    public String referendumUrl;
    public String referendumBrief;
    public String referendumText;
    public String referendumProStatement;
    public String referendumConStatement;
    public String referendumPassageThreshold;
    public String referendumEffectOfAbstain;
    public List<Source> sources;

    public Contest(Parcel parcel) {
        id = parcel.readString();
        type = parcel.readString();
        primaryParty = parcel.readString();
        electorateSpecifications = parcel.readString();
        special = parcel.readString();
        office = parcel.readString();
        parcel.readStringList(level);
        parcel.readStringList(roles);
        district = parcel.readParcelable(District.class.getClassLoader());
        numberElected = parcel.readLong();
        numberVotingFor = parcel.readLong();
        ballotPlacement = parcel.readLong();
        parcel.readList(candidates, Candidate.class.getClassLoader());
        referendumTitle = parcel.readString();
        referendumSubtitle = parcel.readString();
        referendumUrl = parcel.readString();
        referendumBrief = parcel.readString();
        referendumText = parcel.readString();
        referendumProStatement = parcel.readString();
        referendumConStatement = parcel.readString();
        referendumPassageThreshold = parcel.readString();
        referendumEffectOfAbstain = parcel.readString();
        parcel.readList(sources, Source.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(primaryParty);
        dest.writeString(electorateSpecifications);
        dest.writeString(special);
        dest.writeString(office);

        dest.writeStringList(level);
        dest.writeStringList(roles);

        dest.writeParcelable(district, 0);
        dest.writeLong(numberElected);
        dest.writeLong(numberVotingFor);
        dest.writeLong(ballotPlacement);
        dest.writeList(candidates);

        dest.writeString(referendumTitle);
        dest.writeString(referendumSubtitle);
        dest.writeString(referendumUrl);
        dest.writeString(referendumBrief);
        dest.writeString(referendumText);
        dest.writeString(referendumProStatement);
        dest.writeString(referendumConStatement);
        dest.writeString(referendumPassageThreshold);
        dest.writeString(referendumEffectOfAbstain);
        dest.writeList(sources);
    }
}
