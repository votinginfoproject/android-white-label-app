package com.votinginfoproject.VotingInformationProject;

import com.votinginfoproject.VotingInformationProject.models.Candidate;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.District;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.Source;
import com.votinginfoproject.VotingInformationProject.models.State;
import com.votinginfoproject.VotingInformationProject.models.VIPApp;
import com.votinginfoproject.VotingInformationProject.models.VIPAppContext;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.util.ArrayList;

/**
 * Created by kathrynkillebrew on 7/22/14.
 */
public class MockVIPAppContext extends VIPAppContext {

    private VIPApp app;

    public MockVIPAppContext() {
        app = new VIPApp();

        VoterInfo testInfo = new VoterInfo();
        Election testElection = new Election();
        Contest testContestOne = new Contest();
        Contest testContestTwo = new Contest();
        Candidate testCandidateOne = new Candidate();
        Candidate testCandidateTwo = new Candidate();
        Source testSource = new Source();
        District testDistrict = new District();
        State testState = new State();

        testElection.name = "Test Election";
        testElection.electionDay = "2014-01-01";
        testInfo.election = testElection;

        testContestOne.type = "general";
        testContestOne.office = "Benevolent Dictator";
        testContestOne.primaryParty = "Yes please";

        testContestTwo.type = "general";
        testContestTwo.office = "Benevolent Dictator";
        testContestTwo.primaryParty = "LAN";

        testCandidateOne.name = "Test Candidate One";
        testCandidateOne.party = "Yes please";

        testCandidateTwo.name = "Test Candidate Two";
        testCandidateTwo.party = "LAN";

        testContestOne.candidates = new ArrayList<Candidate>(1);
        testContestOne.candidates.add(testCandidateOne);

        testContestTwo.candidates = new ArrayList<Candidate>(1);
        testContestTwo.candidates.add(testCandidateTwo);

        testContestOne.sources = new ArrayList<Source>(1);
        testContestOne.sources.add(testSource);

        testContestTwo.sources = new ArrayList<Source>(1);
        testContestTwo.sources.add(testSource);

        testInfo.contests = new ArrayList<Contest>(2);
        testInfo.contests.add(testContestOne);
        testInfo.contests.add(testContestTwo);

        app.setVoterInfo(testInfo, "");
    }

    public VIPApp getVIPApp() {
        return app;
    }

    public void setVIPApp(VIPApp vip_app) {
        app = vip_app;
    }

}
