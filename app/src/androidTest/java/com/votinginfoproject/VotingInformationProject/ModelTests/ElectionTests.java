package com.votinginfoproject.VotingInformationProject.ModelTests;

import android.test.AndroidTestCase;

import com.votinginfoproject.VotingInformationProject.models.Election;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by andrew on 8/1/14.
 */
public class ElectionTests extends AndroidTestCase {

    public void testEmptyContstructorCreatesNullProperties() {
        Election election = new Election();
        assertNull(election.id);
        assertNull(election.name);
        assertNull(election.electionDay);
    }

    public void testGetIdReturnsEmptyStringForNullValue() {
        Election election = new Election();
        election.id = null;
        assertEquals(election.getId(), "");
    }

    public void testGetNameReturnsEmptyStringForNullValue() {
        Election election = new Election();
        election.name = null;
        assertEquals(election.getName(), "");
    }

    public void testDateCheckForPassedElection() {
        Election election = new Election();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat election_date_api_format = new SimpleDateFormat("yyyy-MM-dd");
        Date electionDay = calendar.getTime();

        // set election day to today
        election.electionDay = election_date_api_format.format(electionDay);

        // if election day is today, it should still be current
        assertFalse(election.isElectionOver());

        // set election to yesterday
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        electionDay = calendar.getTime();
        election.electionDay = election_date_api_format.format(electionDay);

        // if election was yesterday, it should be over
        assertTrue(election.isElectionOver());

        // set election to tomorrow
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        electionDay = calendar.getTime();
        election.electionDay = election_date_api_format.format(electionDay);

        // if election is tomorrow, it should still be current
        assertFalse(election.isElectionOver());
    }
}
