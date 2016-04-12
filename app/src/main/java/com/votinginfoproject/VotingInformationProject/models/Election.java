package com.votinginfoproject.VotingInformationProject.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kathrynkillebrew on 7/14/14.
 */
public class Election implements Parcelable {
    /**
     * Static field used to regenerate object, individually or as arrays
     */
    public static final Parcelable.Creator<Election> CREATOR = new Parcelable.Creator<Election>() {
        public Election createFromParcel(Parcel pc) {
            return new Election(pc);
        }

        public Election[] newArray(int size) {
            return new Election[size];
        }
    };
    private final String TAG = Election.class.getSimpleName();
    private String id;
    private String name;
    private String electionDay;

    //Skip Gson Serialization for these fields
    private transient SimpleDateFormat api_date_format = null;
    private transient SimpleDateFormat api_date_display_format = null;
    private transient Date currentDay;

    public Election() {
        this(null, null, null);
    }

    public Election(String id, String name, String electionDay) {
        this.id = id;
        this.name = name;
        this.electionDay = electionDay;
    }

    public Election(Parcel parcel) {
        this.id = parcel.readString();
        this.name = parcel.readString();
        this.electionDay = parcel.readString();
    }

    /*
     * Helper function to convert date from API string to Date object
     */
    public Date getDayFromString(String str_date) {
        if (str_date == null || str_date.isEmpty()) {
            return null;
        }

        if (api_date_format == null) {
            api_date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        }

        try {
            return api_date_format.parse(str_date);
        } catch (ParseException e) {
            Log.e(TAG, "Failed to parse API date string " + str_date);
            return null;
        }
    }

    /* Helper function to get current date object, for comparison
     */
    public Date getCurrentDay() {
        if (currentDay == null) {
            // set time for current date object to midnight, to compare only date part
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            currentDay = calendar.getTime();
        }
        Log.d(TAG, "Current date is " + currentDay.toString());

        return currentDay;
    }

    /*
     * @return True if election day is earlier than today.
     */
    public boolean isElectionOver() {
        Date election_day = getDayFromString(electionDay);
        Log.d(TAG, "Election date is " + election_day.toString());

        return getCurrentDay().after(election_day);
    }

    public String getFormattedDate() {
        if (electionDay == null || electionDay.isEmpty()) {
            return "";
        }

        if (api_date_format == null) {
            api_date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        }

        if (api_date_display_format == null) {
            api_date_display_format = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        }

        try {
            Date election_date = api_date_format.parse(electionDay);

            return api_date_display_format.format(election_date);
        } catch (ParseException e) {
            Log.e(TAG, "Failed to parse election date " + electionDay);

            return electionDay;
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Getter for name
     *
     * @return name or empty string if name null
     */
    public String getName() {
        return (name != null) ? name : "";
    }

    /**
     * Getter for id
     *
     * @return id or empty string if null
     */
    public String getId() {
        return (id != null) ? id : "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(electionDay);
    }
}
