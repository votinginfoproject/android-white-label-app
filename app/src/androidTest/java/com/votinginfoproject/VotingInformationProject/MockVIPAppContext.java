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
        Contest testContest = new Contest();
        Candidate testCandidate = new Candidate();
        Source testSource = new Source();
        District testDistrict = new District();
        State testState = new State();

        testElection.name = "Test Election";
        testElection.electionDay = "2014-01-01";
        testInfo.election = testElection;

        testContest.type = "general";
        testContest.office = "Benevolent Dictator";

        testCandidate.name = "Test Candidate";
        testCandidate.party = "Yes please";

        testContest.candidates = new ArrayList<Candidate>(1);
        testContest.candidates.add(testCandidate);

        testContest.sources = new ArrayList<Source>(1);
        testContest.sources.add(testSource);

        testInfo.contests = new ArrayList<Contest>(1);
        testInfo.contests.add(testContest);

        app.setVoterInfo(testInfo);
    }

    public VIPApp getVIPApp() {
        return app;
    }

    public void setVIPApp(VIPApp vip_app) {
        app = vip_app;
    }

}
