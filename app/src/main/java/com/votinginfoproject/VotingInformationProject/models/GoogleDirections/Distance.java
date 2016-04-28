package com.votinginfoproject.VotingInformationProject.models.GoogleDirections;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathrynkillebrew on 7/31/14.
 */
public class Distance implements Parcelable {
    public static final Creator<Distance> CREATOR = new Creator<Distance>() {
        @Override
        public Distance createFromParcel(Parcel source) {
            return new Distance(source);
        }

        @Override
        public Distance[] newArray(int size) {
            return new Distance[0];
        }
    };

    public int value;
    public String text;

    private Distance(Parcel parcel) {
        value = parcel.readInt();
        text = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(value);
        dest.writeString(text);
    }
}
