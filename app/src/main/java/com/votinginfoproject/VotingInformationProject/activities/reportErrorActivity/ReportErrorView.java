package com.votinginfoproject.VotingInformationProject.activities.reportErrorActivity;

/**
 * Created by max on 5/3/16.
 */
public interface ReportErrorView {
    void toggleLoading(boolean loading);

    void postUrl(String url, byte[] postData);
}
