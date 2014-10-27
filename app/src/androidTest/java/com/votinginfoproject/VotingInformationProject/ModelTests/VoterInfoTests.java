package com.votinginfoproject.VotingInformationProject.ModelTests;

import android.test.AndroidTestCase;

import com.google.android.gms.maps.model.LatLng;
import com.votinginfoproject.VotingInformationProject.MockVIPAppContext;
import com.votinginfoproject.VotingInformationProject.models.CivicApiAddress;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VIPApp;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by andrew on 8/1/14.
 */
public class VoterInfoTests extends AndroidTestCase {

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

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat api_date_format = new SimpleDateFormat("yyyy-MM-dd");
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        Date tomorrow = calendar.getTime();


        ArrayList<PollingLocation> earlyLocations = new ArrayList();
        ArrayList<PollingLocation> pollingLocations = new ArrayList();
        ArrayList<PollingLocation> dropOffLocations = new ArrayList();

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

        // location 3 is available today only (should use it)
        loc3.startDate = api_date_format.format(today);
        loc3.endDate = loc3.startDate;

        earlyLocations.add(loc3);

        // test date filter for early vote sites

        // location 4 already closed (should not use it)
        PollingLocation loc4 = new PollingLocation();
        loc4.id = "4";
        loc4.name = "location four";
        loc4.address = locThreeAddr;
        loc4.startDate = api_date_format.format(yesterday);
        loc4.endDate = loc4.startDate;

        earlyLocations.add(loc4);

        // location 5 hasn't opened yet--still use it anyway
        PollingLocation loc5 = new PollingLocation();
        loc5.id = "5";
        loc5.name = "location five";
        loc5.address = locThreeAddr;

        loc5.startDate = api_date_format.format(tomorrow);
        loc5.endDate = loc5.startDate;

        earlyLocations.add(loc5);

        // location 6 is a drop-off box that hasn't closed yet
        PollingLocation loc6 = new PollingLocation();
        loc6.id = "6";
        loc6.name = "location six";
        loc6.address = locThreeAddr;

        loc6.startDate = api_date_format.format(today);
        loc6.endDate = loc6.startDate;

        dropOffLocations.add(loc6);

        info.pollingLocations = pollingLocations;
        info.earlyVoteSites = earlyLocations;
        info.dropOffLocations = dropOffLocations;
        info.setUpLocations();

        // should have five total locations now
        assertEquals(5, info.getAllLocations().size());

        // should be able to get location keyed on address
        foundLocation = info.getLocationForId(loc2.address.toGeocodeString());
        assertNotNull(foundLocation);
        assertEquals("location two", foundLocation.name);

        // should be able to get address by key for currently open early voting site
        CivicApiAddress foundAddress = info.getAddressForId(loc3.address.toGeocodeString());
        assertEquals("Three", foundAddress.locationName);

        // should be able to get early vote site not open yet by ID
        foundLocation = info.getLocationForId(loc5.id);
        assertEquals("location five", foundLocation.name);

        // should be able to get description for location by key
        assertEquals("location one", info.getDescriptionForId(loc1.id));

        // should not have early vote site that isn't currently open
        assertNull(info.getLocationForId("4"));

        // should have a drop-box location
        foundLocation = info.getLocationForId(loc6.id);
        assertEquals("location six", foundLocation.name);
    }

    public void testGetAdministrativeBodies() {
        MockVIPAppContext mockContext = new MockVIPAppContext();
        VIPApp app = mockContext.getVIPApp();
        VoterInfo info = app.getVoterInfo();
        info.setUpLocations();

        // should be able to get admin body address
        CivicApiAddress gotAddress = info.getAdminAddress(ElectionAdministrationBody.AdminBody.STATE);
        assertEquals("test location", gotAddress.locationName);

        // should get null for admin body without physical address
        gotAddress = info.getAdminAddress(ElectionAdministrationBody.AdminBody.LOCAL);
        assertNull(gotAddress);

        // should be able to get description for admin body
        assertEquals("State Admin", info.getDescriptionForId(ElectionAdministrationBody.AdminBody.STATE));

        // should be able to get location for admin body
        LatLng stateLoc = info.getAdminBodyLatLng(ElectionAdministrationBody.AdminBody.STATE);
        assertEquals(40.1, stateLoc.latitude);
    }
}
