package com.votinginfoproject.VotingInformationProject.ModelTests;

import android.test.AndroidTestCase;

import com.votinginfoproject.VotingInformationProject.models.Candidate;
import com.votinginfoproject.VotingInformationProject.models.SocialMediaChannel;

/**
 * Created by andrew on 7/31/14.
 */
public class CandidateTests extends AndroidTestCase {

    public void testConstructorCreatesNonNullArrayListForChannels() {
        Candidate candidate = new Candidate();
        assertNotNull(candidate.channels);
    }

    public void testFindChannelForType() {
        Candidate candidate = new Candidate();
        candidate.channels.add(new SocialMediaChannel("testFacebook", "Facebook"));
        candidate.channels.add(new SocialMediaChannel("testYouTube", "YouTube"));

        SocialMediaChannel channel = candidate.findChannelForType("YouTube");
        assertEquals(channel.type, "YouTube");

        // Should allow for searches using cleanType
        String secondSearch = "Facebook";
        String cleanType = SocialMediaChannel.getCleanType(secondSearch);
        channel = candidate.findChannelForType(cleanType);
        assertEquals(channel.type, secondSearch);
    }
}
