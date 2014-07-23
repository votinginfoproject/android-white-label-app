package com.votinginfoproject.VotingInformationProject.models;

/**
 * Created by kathrynkillebrew on 7/14/14.
 */
public class Address {
    public String locationName;
    public String line1;
    public String line2;
    public String line3;
    public String city;
    public String state;
    public String zip;

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
        }

        if (zip != null && !zip.isEmpty()) {
            builder.append(zip);
        }

        return builder.toString();
    }
}
