package com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment;

/**
 * Created by max on 4/15/16.
 */
public interface ElectionDetailsView {
    void navigateToURL(String urlString);

    void showNoContentView();

    void navigateToErrorView();

    void navigateToDirectionsView(String address);
}
