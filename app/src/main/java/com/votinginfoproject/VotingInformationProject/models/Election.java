package com.votinginfoproject.VotingInformationProject.models;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kathrynkillebrew on 7/14/14.
 */
public class Election {
    public String id;
    public String name;
    public String electionDay;

    private SimpleDateFormat election_date_api_format = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat election_date_display_format = new SimpleDateFormat("MMMM d, yyyy");

    public Election() {
        this(null, null, null);
    }

    public Election(String id, String name, String electionDay) {
        this.id = id;
        this.name = name;
        this.electionDay = electionDay;
        election_date_api_format = new SimpleDateFormat("yyyy-MM-dd");
        election_date_display_format = new SimpleDateFormat("MMMM d, yyyy");
    }

    public String getFormattedDate() {
        if (electionDay == null || electionDay.isEmpty()) {
            return "";
        }
        try {
            Date election_date = election_date_api_format.parse(electionDay);
            return election_date_display_format.format(election_date);
        } catch (ParseException e) {
            Log.e("ElectionModel", "Failed to parse election date " + electionDay);
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
