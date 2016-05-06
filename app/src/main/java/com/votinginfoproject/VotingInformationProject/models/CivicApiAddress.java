package com.votinginfoproject.VotingInformationProject.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kathrynkillebrew on 7/14/14.
 */
public class CivicApiAddress implements Parcelable {
    /**
     * Static field used to regenerate object, individually or as arrays
     */
    public static final Parcelable.Creator<CivicApiAddress> CREATOR = new Parcelable.Creator<CivicApiAddress>() {
        public CivicApiAddress createFromParcel(Parcel pc) {
            return new CivicApiAddress(pc);
        }

        public CivicApiAddress[] newArray(int size) {
            return new CivicApiAddress[size];
        }
    };

    public final String locationName;
    public final String line1;
    public final String line2;
    public final String line3;
    public final String city;
    public final String state;
    public final String zip;
    // the co-ordinates are not in the API response; the app will set them when geocoded
    public double latitude;
    public double longitude;
    public double distance;

    public CivicApiAddress(Parcel parcel) {
        locationName = parcel.readString();
        line1 = parcel.readString();
        line2 = parcel.readString();
        line3 = parcel.readString();
        city = parcel.readString();
        state = parcel.readString();
        zip = parcel.readString();
        latitude = parcel.readDouble();
        longitude = parcel.readDouble();
        distance = parcel.readDouble();
    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (locationName != null && !locationName.isEmpty()) {
            builder.append(locationName);
            builder.append("\n");
        }

        if (line1 != null && !line1.isEmpty()) {
            builder.append(line1);
            builder.append("\n");
        }

        if (line2 != null && !line2.isEmpty()) {
            builder.append(line2);
            builder.append("\n");
        }

        if (line3 != null && !line3.isEmpty()) {
            builder.append(line3);
            builder.append("\n");
        }

        if (city != null && !city.isEmpty()) {
            builder.append(city);
            builder.append(", ");
        }

        if (state != null && !state.isEmpty()) {
            builder.append(state);
            builder.append(" ");
        } else if (city != null && !city.isEmpty()) {
            // remove comma after city if there's no state
            builder.deleteCharAt(builder.length() - 2);
        }

        if (zip != null && !zip.isEmpty()) {
            builder.append(zip);
        }

        return builder.toString();
    }

    /**
     * Omits the location name, and returns address as a single line.
     *
     * @return string representation of address suitable for sending to geocoder
     */
    public String toGeocodeString() {
        StringBuilder builder = new StringBuilder();

        if (line1 != null && !line1.isEmpty()) {
            builder.append(line1);
            builder.append(", ");
        }

        if (city != null && !city.isEmpty()) {
            builder.append(city);
            builder.append(", ");
        }

        if (state != null && !state.isEmpty()) {
            builder.append(state);
            builder.append(" ");
        } else if (city != null && !city.isEmpty()) {
            // remove comma after city if there's no state
            builder.deleteCharAt(builder.length() - 2);
        }

        if (zip != null && !zip.isEmpty()) {
            builder.append(zip);
        }

        return builder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(locationName);
        dest.writeString(line1);
        dest.writeString(line2);
        dest.writeString(line3);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(zip);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(distance);
    }
}
