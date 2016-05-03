package com.votinginfoproject.VotingInformationProject.activities.reportErrorActivity;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;

/**
 * Created by max on 5/3/16.
 */
public abstract class ReportErrorPresenter extends BasePresenter<ReportErrorView> {
    abstract void webViewFinishedLoading();
}
