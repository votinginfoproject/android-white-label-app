package com.votinginfoproject.VotingInformationProject.ModelTests;

import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import junit.framework.TestCase;

/**
 * Created by andrew on 8/1/14.
 */
public class VoterInfoTests extends TestCase {

    public void testOtherElectionsNotNull() {
        VoterInfo voterInfo = new VoterInfo();
        assertNotNull(voterInfo.otherElections);
    }
}
