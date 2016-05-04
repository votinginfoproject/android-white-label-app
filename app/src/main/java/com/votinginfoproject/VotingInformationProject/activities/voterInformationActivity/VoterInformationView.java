package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;

import android.app.Fragment;
import android.content.Context;

import com.votinginfoproject.VotingInformationProject.activities.reportErrorActivity.ReportErrorPresenter;

/**
 * Created by marcvandehey on 4/6/16.
 */
public interface VoterInformationView {

    void presentParentLevelFragment(Fragment parentLevel);

    void presentChildLevelFragment(Fragment childLevelFragment);

    void navigateBack();

    void navigateToReportErrorActivity(ReportErrorPresenter presenter);

    void scrollCurrentFragmentToTop();

    Context getContext();
}
