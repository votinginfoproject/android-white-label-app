package com.votinginfoproject.VotingInformationProject.activities.reportErrorActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;

/**
 * Created by max on 5/3/16.
 */
public class ReportErrorActivity extends BaseActivity<ReportErrorPresenter> implements ReportErrorView {
    public static final String TAG = ReportErrorActivity.class.getSimpleName();

    public static final String ARG_PRESENTER = "presenter";

    private Toolbar mToolbar;
    private WebView mWebview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report_error);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey(ARG_PRESENTER) && getPresenter() == null) {
                ReportErrorPresenter presenter = (ReportErrorPresenter) extras.getParcelable(ARG_PRESENTER);

                setPresenter(presenter);
            }
        }

        mWebview = (WebView) findViewById(R.id.web_view);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        getPresenter().setView(this);
    }
}
