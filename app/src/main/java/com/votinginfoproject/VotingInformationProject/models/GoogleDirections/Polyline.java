package com.votinginfoproject.VotingInformationProject.models.GoogleDirections;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathrynkillebrew on 7/31/14.
 */
public class Polyline implements Parcelable {
    public String points;

    public static final Creator<Polyline> CREATOR = new Creator<Polyline>() {
        @Override
        public Polyline createFromParcel(Parcel source) {
            return new Polyline(source);
        }

        @Override
        public Polyline[] newArray(int size) {
            return new Polyline[size];
        }
    };

    public Polyline(Parcel parcel) {
        points = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(points);
    }
}
