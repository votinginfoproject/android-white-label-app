package com.votinginfoproject.VotingInformationProject.activities.aboutActivity;

/**
 * Created by max on 4/8/16.
 */

public interface AboutVIPView {

    void setTitle(String title);

    void setInformationText(String informationText);

    void navigateToAboutVIPView(int infoTitleKey, int infoTextKey, float transitionPointX, float transitionPointY);

    void navigateToAboutVIPLicenseVIew(int infoTitleKey, float transitionPointX, float transitionPointY);
}
