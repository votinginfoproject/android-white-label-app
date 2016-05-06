package com.votinginfoproject.VotingInformationProject.activities.homeActivity;

import android.support.annotation.StringRes;

import com.votinginfoproject.VotingInformationProject.models.VoterInfoResponse;

import java.util.ArrayList;

/**
 * Created by marcvandehey on 3/31/16.
 */
public interface HomeView {
    void navigateToAboutActivity();

    void navigateToVoterInformationActivity(VoterInfoResponse voterInfoResponse, String partyFilter);

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

    void showMessage(@StringRes int message);

    void hideStatusView();
}
