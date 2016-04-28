package com.votinginfoproject.VotingInformationProject.models.GoogleDirections;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Routes with no waypoints will consist of a single leg.
 *
 * Created by kathrynkillebrew on 7/31/14.
 */
public class Leg implements Parcelable {
    public static final Creator<Leg> CREATOR = new Creator<Leg>() {
        @Override
        public Leg createFromParcel(Parcel source) {
            return new Leg(source);
        }

        @Override
        public Leg[] newArray(int size) {
            return new Leg[size];
        }
    };

    public static final Creator<Integer> INTEGER_CREATOR = new Creator<Integer>() {
        @Override
        public Integer createFromParcel(Parcel source) {
            return source.readInt();
        }

        @Override
        public Integer[] newArray(int size) {
            return new Integer[size];
        }
    };

    public List<Step> steps;
    public List<Integer> via_waypoint;
    public Distance distance;
    public Duration duration;
    public String start_address;
    public String end_address;
    public Location start_location;
    public Location end_location;

    private Leg(Parcel parcel) {
        steps = new ArrayList<>();
        parcel.readTypedList(steps, Step.CREATOR);

        via_waypoint = new ArrayList<>();
        parcel.readList(via_waypoint, Integer.class.getClassLoader());

        distance = parcel.readParcelable(Distance.class.getClassLoader());
        duration = parcel.readParcelable(Duration.class.getClassLoader());
        start_address = parcel.readString();
        end_address = parcel.readString();
        start_location = parcel.readParcelable(Location.class.getClassLoader());
        end_location = parcel.readParcelable(Location.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(steps);
        dest.writeList(via_waypoint);
        dest.writeParcelable(distance, 0);
        dest.writeParcelable(duration, 0);
        dest.writeString(start_address);
        dest.writeString(end_address);
        dest.writeParcelable(start_location, 0);
        dest.writeParcelable(end_location, 0);
    }
}
