package com.votinginfoproject.VotingInformationProject.activities.aboutActivity;

import com.votinginfoproject.VotingInformationProject.models.CoordinatePair;

/**
 * Created by max on 4/8/16.
 */

public interface AboutVIPView {

    void setTitle(String title);

    void setInformationText(String informationText);

    void navigateToAboutView(String title, String infoText, boolean showsAdditionalInfoButtons, CoordinatePair transitionPoint);

    void navigateToPreviousView();
}
