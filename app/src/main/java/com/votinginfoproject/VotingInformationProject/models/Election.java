package com.votinginfoproject.VotingInformationProject.models;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kathrynkillebrew on 7/14/14.
 */
public class Election {
    public String id;
    public String name;
    public String electionDay;

    private SimpleDateFormat api_date_format;
    private SimpleDateFormat api_date_display_format;

    private Date currentDay;

    private final String TAG = Election.class.getSimpleName();

    public Election() {
        this(null, null, null);
    }

    public Election(String id, String name, String electionDay) {
        this.id = id;
        this.name = name;
        this.electionDay = electionDay;
        api_date_format = new SimpleDateFormat("yyyy-MM-dd");
        api_date_display_format = new SimpleDateFormat("MMMM d, yyyy");
    }

    /* Helper function to convert date from API string to Date object
     */
    public Date getDayFromString(String str_date) {
        if (str_date == null || str_date.isEmpty()) {
            return null;
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
}
