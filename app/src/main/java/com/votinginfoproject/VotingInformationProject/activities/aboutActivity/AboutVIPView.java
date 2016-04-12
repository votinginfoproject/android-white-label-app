package com.votinginfoproject.VotingInformationProject.activities.aboutActivity;

import com.votinginfoproject.VotingInformationProject.models.CoordinatePair;

/**
 * Created by max on 4/8/16.
 */

public interface AboutVIPView {

    void setTitle(String title);

    void setInformationText(String informationText);

    void performCircularReveal(CoordinatePair transitionPoint);

    void navigateToAboutVIPView(int infoTitleKey, int infoTextKey, CoordinatePair transitionPoint);

    void navigateToAboutVIPLicenseView(int infoTitleKey, CoordinatePair transitionPoint);
}
