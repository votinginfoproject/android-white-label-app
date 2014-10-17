package com.votinginfoproject.VotingInformationProject.ModelTests;

import android.test.AndroidTestCase;

import com.votinginfoproject.VotingInformationProject.models.SocialMediaChannel;

/**
 * Created by andrew on 7/31/14.
 */
public class SocialMediaChannelTests extends AndroidTestCase {

    public void testConstructorCreatesNullPropertiesForInvalidTypes() {
        SocialMediaChannel channel = new SocialMediaChannel("testOther", "Other");
        assertNull(channel.id);
        assertNull(channel.type);
    }
    public void testGetUriReturnsNullForInvalidId() {
        SocialMediaChannel channel = new SocialMediaChannel("testOther", "Other");
        assertNull(channel.getUri());
    }

    public void testGetCleanType() {
        // getCleanType should return lowercase, whitespace trimmed string
        String cleanType = "facebook";
        SocialMediaChannel channel = new SocialMediaChannel("testFacebook", "Facebook");
        assertEquals(cleanType, channel.getCleanType());
    }


}
