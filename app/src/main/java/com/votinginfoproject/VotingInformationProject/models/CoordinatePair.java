package com.votinginfoproject.VotingInformationProject.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by max on 4/11/16.
 */
public class CoordinatePair implements Parcelable {
    public final int x;
    public final int y;

    public CoordinatePair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private CoordinatePair(Parcel parcel) {
        x = parcel.readInt();
        y = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(x);
        dest.writeInt(y);
    }

    public static final Parcelable.Creator<CoordinatePair> CREATOR
            = new Parcelable.Creator<CoordinatePair>() {

        @Override
        public CoordinatePair createFromParcel(Parcel in) {
            return new CoordinatePair(in);
        }

        @Override
        public CoordinatePair[] newArray(int size) {
            return new CoordinatePair[size];
        }
    };
}
