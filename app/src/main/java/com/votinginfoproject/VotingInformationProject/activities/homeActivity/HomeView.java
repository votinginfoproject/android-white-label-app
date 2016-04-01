package com.votinginfoproject.VotingInformationProject.activities.homeActivity;

import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.util.ArrayList;

/**
 * Created by marcvandehey on 3/31/16.
 */
public interface HomeView {
    public static final int PICK_CONTACT_REQUEST = 1;

    void navigateToContactsActivity();

    void navigateToAboutActivity();

    void navigateToVIPResultsActivity(VoterInfo voterInfo);

    void showElectionPicker();

    void setElectionText(String electionText);

    void hideElectionPicker();

    void displayElectionPickerWithItems(ArrayList<String> elections, int selected);

    void showPartyPicker();

    void setPartyText(String partyText);

    void displayPartyPickerWithItems(ArrayList<String> parties, int selected);

    void hidePartyPicker();

    void showGoButton();

    void hideGoButton();

    void overrideSearchAddress(String searchAddress);

    void showMessage(String message);

    void hideStatusView();
}
