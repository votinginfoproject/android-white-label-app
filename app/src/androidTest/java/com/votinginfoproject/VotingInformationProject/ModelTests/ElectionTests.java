package com.votinginfoproject.VotingInformationProject.ModelTests;

import com.votinginfoproject.VotingInformationProject.models.Election;

import junit.framework.TestCase;

/**
 * Created by andrew on 8/1/14.
 */
public class ElectionTests extends TestCase {

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
}
