package com.votinginfoproject.VotingInformationProject.ModelTests;

import com.votinginfoproject.VotingInformationProject.MockVIPAppContext;
import com.votinginfoproject.VotingInformationProject.models.CivicApiAddress;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VIPApp;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew on 8/1/14.
 */
public class VoterInfoTests extends TestCase {

    public void testOtherElectionsNotNull() {
        VoterInfo voterInfo = new VoterInfo();
        assertNotNull(voterInfo.otherElections);
    }

    public void testContestFilter() {
        MockVIPAppContext mockContext = new MockVIPAppContext();
        VIPApp app = mockContext.getVIPApp();
        VoterInfo info = app.getVoterInfo();

        // should have both contests in filtered list with empty party string
        List<Contest> filteredContests = info.getFilteredContests();
        assertNotNull(filteredContests);
        assertEquals(2, filteredContests.size());

        // should have only one contest when filtered by party name
        app.setSelectedParty("Yes please");
        filteredContests = info.getFilteredContests();
        assertNotNull(filteredContests);
        assertEquals(1, filteredContests.size());
        assertEquals("Yes please", info.getContestAt(0).primaryParty);

    }

    public void testGetLocationFromList() {
        MockVIPAppContext mockContext = new MockVIPAppContext();
        VIPApp app = mockContext.getVIPApp();
        VoterInfo info = app.getVoterInfo();

        ArrayList<PollingLocation> earlyLocations = new ArrayList();
        ArrayList<PollingLocation> pollingLocations = new ArrayList();

        PollingLocation loc1 = new PollingLocation();
        loc1.name = "location one";
        loc1.id = "1";
        CivicApiAddress locOneAddr = new CivicApiAddress();
        locOneAddr.locationName = "One";
        locOneAddr.line1 = "123 Foo St";
        locOneAddr.city = "Nowheresville";
        locOneAddr.state = "DE";
        locOneAddr.latitude = 45;
        locOneAddr.longitude = -75;
        loc1.address = locOneAddr;

        pollingLocations.add(loc1);

        // test list of one polling location only
        info.pollingLocations = pollingLocations;
        info.earlyVoteSites = earlyLocations;
        info.setUpLocations();
        assertEquals(1, info.getAllLocations().size());

        // get the location by ID
        PollingLocation foundLocation = info.getLocationForId("1");
        assertNotNull(foundLocation);

        // test multiple locations

        PollingLocation loc2 = new PollingLocation();
        loc2.name = "location two";
        CivicApiAddress locTwoAddr = new CivicApiAddress();
        locTwoAddr.locationName = "Two";
        locTwoAddr.line1 = "123 Bar St";
        locTwoAddr.city = "Nowheresville";
        locTwoAddr.state = "DE";
        loc2.address = locTwoAddr;

        pollingLocations.add(loc2);

        PollingLocation loc3 = new PollingLocation();
        loc3.name = "location three";
        CivicApiAddress locThreeAddr = new CivicApiAddress();
        locThreeAddr.locationName = "Three";
        locThreeAddr.line1 = "123 Baz St";
        locThreeAddr.city = "Somewheresville";
        locThreeAddr.state = "PA";
        loc3.address = locThreeAddr;

        earlyLocations.add(loc3);

        info.pollingLocations = pollingLocations;
        info.earlyVoteSites = earlyLocations;
        info.setUpLocations();

        // should have three total locations now
        assertEquals(3, info.getAllLocations().size());

        // should be able to get location keyed on address
        foundLocation = info.getLocationForId(loc2.address.toGeocodeString());
        assertNotNull(foundLocation);
        assertEquals("location two", foundLocation.name);

        // should be able to get address by key
        CivicApiAddress foundAddress = info.getAddressForId(loc3.address.toGeocodeString());
        assertEquals("Three", foundAddress.locationName);
    }
}
