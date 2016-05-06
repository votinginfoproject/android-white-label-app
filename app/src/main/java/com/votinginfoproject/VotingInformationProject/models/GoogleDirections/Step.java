package com.votinginfoproject.VotingInformationProject.models.GoogleDirections;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathrynkillebrew on 7/31/14.
 */
public class Step implements Parcelable {
    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel source) {
            return new Step(source);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    public final String travel_mode;
    public final Location start_location;
    public final Location end_location;
    public final Polyline polyline;
    public final Duration duration;
    public final String html_instructions;
    public final String maneuver;
    public final Distance distance;

    public Step(Parcel parcel) {
        travel_mode = parcel.readString();
        start_location = parcel.readParcelable(Location.class.getClassLoader());
        end_location = parcel.readParcelable(Location.class.getClassLoader());
        polyline = parcel.readParcelable(Polyline.class.getClassLoader());
        duration = parcel.readParcelable(Duration.class.getClassLoader());
        html_instructions = parcel.readString();
        maneuver = parcel.readString();
        distance = parcel.readParcelable(Distance.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(travel_mode);
        dest.writeParcelable(start_location, 0);
        dest.writeParcelable(end_location, 0);
        dest.writeParcelable(polyline, 0);
        dest.writeParcelable(duration, 0);
        dest.writeString(html_instructions);
        dest.writeString(maneuver);
        dest.writeParcelable(distance, 0);
    }
}
