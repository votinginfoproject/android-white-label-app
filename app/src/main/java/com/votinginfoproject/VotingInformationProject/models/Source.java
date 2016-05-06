package com.votinginfoproject.VotingInformationProject.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathrynkillebrew on 7/14/14.
 */

public class Source implements Parcelable {
    public static final Parcelable.Creator<Source> CREATOR = new Parcelable.Creator<Source>() {
        public Source createFromParcel(Parcel pc) {
            return new Source(pc);
        }

        public Source[] newArray(int size) {
            return new Source[size];
        }
    };

    public final String name;
    public final Boolean official;

    public Source(Parcel parcel) {
        name = parcel.readString();
        official = (parcel.readInt() != 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(official ? 1 : 0);
    }
}
