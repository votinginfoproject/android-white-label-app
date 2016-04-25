package com.votinginfoproject.VotingInformationProject.models.GoogleDirections;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathrynkillebrew on 7/31/14.
 */
public class Step implements Parcelable {
    private static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    public String travel_mode;
    public Location start_location;
    public Location end_location;
    public Polyline polyline;
    public Duration duration;
    public String html_instructions;
    public String maneuver;
    public Distance distance;

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
        dest.writeParcelable(start_location, flags);
        dest.writeParcelable(end_location, flags);
        dest.writeParcelable(polyline, flags);
        dest.writeParcelable(duration, flags);
        dest.writeString(html_instructions);
        dest.writeString(maneuver);
        dest.writeParcelable(distance, flags);
    }
}
