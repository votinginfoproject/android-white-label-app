package com.votinginfoproject.VotingInformationProject.activities.bottomBarActivity;

/**
 * Created by marcvandehey on 4/5/16.
 */
public interface BottomBarPresenter {
    void backNavigationBarButtonClicked();

    void ballotButtonClicked();

    void detailsButtonClicked();

    void pollintSitesButtonClicked();

    void mapButtonClicked();

    void overflowButtonClicked();

    void mapFilterButtonClicked(int filterItem);
}
