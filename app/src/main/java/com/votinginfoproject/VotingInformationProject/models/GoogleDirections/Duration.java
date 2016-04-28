package com.votinginfoproject.VotingInformationProject.models.GoogleDirections;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathrynkillebrew on 7/31/14.
 */
public class Duration implements Parcelable {
    public static final Creator<Duration> CREATOR = new Creator<Duration>() {
        @Override
        public Duration createFromParcel(Parcel source) {
            return new Duration(source);
        }

        @Override
        public Duration[] newArray(int size) {
            return new Duration[size];
        }
    };

    public int value;
    public String text;

    private Duration(Parcel parcel) {
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
