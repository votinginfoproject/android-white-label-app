package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.candidateInformationFragment;

import android.content.Context;

/**
 * Created by marcvandehey on 5/2/16.
 */
public interface CandidateInformationView {
    Context getContext();

    void navigateToUrl(String url);

    void navigateToPhone(String phoneNumber);

    void navigateToEmail(String email);

    void reportClicked();
}
