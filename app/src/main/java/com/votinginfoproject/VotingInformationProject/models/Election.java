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

    public String getFormattedDate() {
        if (electionDay == null || electionDay.isEmpty()) {
            return "";
        }
        SimpleDateFormat election_date_api_format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat election_date_display_format = new SimpleDateFormat("MMMM d, yyyy");
        try {
            Date election_date = election_date_api_format.parse(electionDay);
            return election_date_display_format.format(election_date);
        } catch (ParseException e) {
            Log.e("ElectionModel", "Failed to parse election date " + electionDay);
            return electionDay;
        }
    }
}
