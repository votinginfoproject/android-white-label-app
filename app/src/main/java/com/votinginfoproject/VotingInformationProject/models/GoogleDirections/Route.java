package com.votinginfoproject.VotingInformationProject.models.GoogleDirections;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kathrynkillebrew on 7/31/14.
 */
public class Route implements Parcelable {
    private static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel source) {
            return new Route(source);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    public String summary;
    public Bounds bounds;
    public String copyrights;
    public Polyline overview_polyline;
    public List<String> warnings;
    public List<Integer> waypoint_order;
    public List<Leg> legs;

    private Route(Parcel parcel) {
        summary = parcel.readString();
        bounds = parcel.readParcelable(Bounds.class.getClassLoader());
        copyrights = parcel.readString();
        overview_polyline = parcel.readParcelable(Polyline.class.getClassLoader());

        warnings = new ArrayList<>();
        parcel.readStringList(warnings);

        waypoint_order = new ArrayList<>();
        parcel.readList(waypoint_order, Integer.class.getClassLoader());

        legs = new ArrayList<>();
        parcel.readList(legs, Leg.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(summary);
        dest.writeParcelable(bounds, flags);
        dest.writeString(copyrights);
        dest.writeParcelable(overview_polyline, flags);
        dest.writeList(warnings);
        dest.writeList(waypoint_order);
        dest.writeList(legs);
    }
}
