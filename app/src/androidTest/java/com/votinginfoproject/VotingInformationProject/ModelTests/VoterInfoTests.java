package com.votinginfoproject.VotingInformationProject.ModelTests;

import android.test.AndroidTestCase;

import com.google.android.gms.maps.model.LatLng;
import com.votinginfoproject.VotingInformationProject.models.CivicApiAddress;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VoterInfoResponse;
import com.votinginfoproject.VotingInformationProject.models.singletons.VoterInformation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by andrew on 8/1/14.
 */
public class VoterInfoTests extends AndroidTestCase {

    public void testOtherElectionsNotNull() {
        VoterInfoResponse voterInfoResponse = new VoterInfoResponse();
        assertNotNull(voterInfoResponse.otherElections);
    }

    public void testGetLocationFromList() {
        VoterInfoResponse info = VoterInformation.getVoterInfo();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat api_date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        Date tomorrow = calendar.getTime();


        ArrayList<PollingLocation> earlyLocations = new ArrayList<>();
        ArrayList<PollingLocation> pollingLocations = new ArrayList<>();
        ArrayList<PollingLocation> dropOffLocations = new ArrayList<>();

        PollingLocation loc1 = new PollingLocation();
        loc1.name = "location one";
        loc1.id = "1";
        CivicApiAddress locOneAddress = new CivicApiAddress();
        locOneAddress.locationName = "One";
        locOneAddress.line1 = "123 Foo St";
        locOneAddress.city = "Nowheresville";
        locOneAddress.state = "DE";
        locOneAddress.latitude = 45;
        locOneAddress.longitude = -75;
        loc1.address = locOneAddress;

        pollingLocations.add(loc1);

        // AboutVIPActivity list of one polling location only
        info.pollingLocations = pollingLocations;
        info.earlyVoteSites = earlyLocations;
        info.setUpLocations();
        assertEquals(1, info.getAllLocations().size());

        // get the location by ID
        PollingLocation foundLocation = info.getLocationForId("1");
        assertNotNull(foundLocation);

        // AboutVIPActivity multiple locations

        PollingLocation loc2 = new PollingLocation();
        loc2.name = "location two";
        CivicApiAddress locTwoAddress = new CivicApiAddress();
        locTwoAddress.locationName = "Two";
        locTwoAddress.line1 = "123 Bar St";
        locTwoAddress.city = "Nowheresville";
        locTwoAddress.state = "DE";
        loc2.address = locTwoAddress;

        pollingLocations.add(loc2);

        PollingLocation loc3 = new PollingLocation();
        loc3.name = "location three";
        CivicApiAddress locThreeAddress = new CivicApiAddress();
        locThreeAddress.locationName = "Three";
        locThreeAddress.line1 = "123 Baz St";
        locThreeAddress.city = "Somewheresville";
        locThreeAddress.state = "PA";
        loc3.address = locThreeAddress;

        // location 3 is available today only (should use it)
        loc3.startDate = api_date_format.format(today);
        loc3.endDate = loc3.startDate;

        earlyLocations.add(loc3);

        // AboutVIPActivity date filter for early vote sites

        // location 4 already closed (should not use it)
        PollingLocation loc4 = new PollingLocation();
        loc4.id = "4";
        loc4.name = "location four";
        loc4.address = locThreeAddress;
        loc4.startDate = api_date_format.format(yesterday);
        loc4.endDate = loc4.startDate;

        earlyLocations.add(loc4);

        // location 5 hasn't opened yet--still use it anyway
        PollingLocation loc5 = new PollingLocation();
        loc5.id = "5";
        loc5.name = "location five";
        loc5.address = locThreeAddress;

        loc5.startDate = api_date_format.format(tomorrow);
        loc5.endDate = loc5.startDate;

        earlyLocations.add(loc5);

        // location 6 is a drop-off box that hasn't closed yet
        PollingLocation loc6 = new PollingLocation();
        loc6.id = "6";
        loc6.name = "location six";
        loc6.address = locThreeAddress;

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

        // should have two polling locations
        assertEquals(info.getPollingLocations().size(), 2);

        // should limit drop-box locations to first 50
        for (int i = 0; i <= 60; i++) {
            loc6.id = Integer.toString(i + 10);
            dropOffLocations.add(loc6);
        }

        info.dropOffLocations = dropOffLocations;
        info.setUpLocations();
        assertEquals(info.getOpenDropOffLocations().size(), 50);
    }

    public void testGetAdministrativeBodies() {
        VoterInfoResponse info = VoterInformation.getVoterInfo();
        info.setUpLocations();

        // should be able to get admin body address
        CivicApiAddress gotAddress = info.getAdminAddress(ElectionAdministrationBody.AdminBody.STATE);
        assertEquals("AboutVIPActivity location", gotAddress.locationName);

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
