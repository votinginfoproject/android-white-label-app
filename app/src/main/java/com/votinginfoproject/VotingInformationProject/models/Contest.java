package com.votinginfoproject.VotingInformationProject.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
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

    public final String id;
    public final String type;
    public final String primaryParty;
    public final String electorateSpecifications;
    public final String special;
    public final String office;
    public final List<String> level;
    public final List<String> roles;
    public final District district;
    public final Long numberElected;
    public final Long numberVotingFor;
    public final Long ballotPlacement;
    public final List<Candidate> candidates;
    public final String referendumTitle;
    public final String referendumSubtitle;
    public final String referendumUrl;
    public final String referendumBrief;
    public final String referendumText;
    public final String referendumProStatement;
    public final String referendumConStatement;
    public final String referendumPassageThreshold;
    public final String referendumEffectOfAbstain;
    public final List<Source> sources;

    public Contest(Parcel parcel) {
        level = new LinkedList<>();
        roles = new LinkedList<>();
        candidates = new LinkedList<>();
        sources = new LinkedList<>();

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
