package com.votinginfoproject.VotingInformationProject.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathrynkillebrew on 7/14/14.
 * Polling location or early voting site
 * https://developers.google.com/civic-information/docs/v1/voterinfo
 */
public class PollingLocation implements Parcelable {
    /**
     * Static field used to regenerate object, individually or as arrays
     */
    public static final Parcelable.Creator<PollingLocation> CREATOR = new Parcelable.Creator<PollingLocation>() {
        public PollingLocation createFromParcel(Parcel pc) {
            return new PollingLocation(pc);
        }

        public PollingLocation[] newArray(int size) {
            return new PollingLocation[size];
        }
    };

    public CivicApiAddress address;
    public String id;
    public String name;
    public String startDate;
    public String endDate;
    public String pollingHours;
    public String voterServices; // This field is not populated for polling locations.

    /**
     * Creator from Parcel, reads back fields IN THE ORDER they were written
     */
    public PollingLocation(Parcel parcel) {
        address = parcel.readParcelable(CivicApiAddress.class.getClassLoader());
        id = parcel.readString();
        name = parcel.readString();
        startDate = parcel.readString();
        endDate = parcel.readString();
        pollingHours = parcel.readString();
        voterServices = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(address, 0);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(pollingHours);
        dest.writeString(voterServices);
    }
}
