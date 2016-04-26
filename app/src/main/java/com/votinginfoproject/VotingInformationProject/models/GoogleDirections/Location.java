package com.votinginfoproject.VotingInformationProject.models.GoogleDirections;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathrynkillebrew on 7/31/14.
 */
public class Location implements Parcelable {
    /**
     * Static field used to regenerate object, individually or as arrays
     */
    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel pc) {
            return new Location(pc);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public float lat;
    public float lng;

    public Location() {
        lat = 0.f;
        lng = 0.f;
    }

    public Location(Parcel parcel) {
        this.lat = parcel.readFloat();
        this.lng = parcel.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(lat);
        dest.writeFloat(lng);
    }

    public String getGoogleAPIRepresentation() {
        return lat + "," + lng;
    }

    public String getDescription() {
        return "Location - latitude: " + lat + " longitude: " + lng;
    }
}
