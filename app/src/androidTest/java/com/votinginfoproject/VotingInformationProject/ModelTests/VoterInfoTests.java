package com.votinginfoproject.VotingInformationProject.ModelTests;

import com.votinginfoproject.VotingInformationProject.MockVIPAppContext;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.VIPApp;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import junit.framework.TestCase;

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
}
