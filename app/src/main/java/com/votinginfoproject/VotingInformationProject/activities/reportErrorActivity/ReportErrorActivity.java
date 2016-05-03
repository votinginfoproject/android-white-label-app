package com.votinginfoproject.VotingInformationProject.activities.reportErrorActivity;

import android.os.Bundle;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;

/**
 * Created by max on 5/3/16.
 */
public class ReportErrorActivity extends BaseActivity<ReportErrorPresenter> implements ReportErrorView {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report_error);
    }
}
