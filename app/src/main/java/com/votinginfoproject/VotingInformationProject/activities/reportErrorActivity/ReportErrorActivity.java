package com.votinginfoproject.VotingInformationProject.activities.reportErrorActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;

/**
 * Created by max on 5/3/16.
 */
public class ReportErrorActivity extends BaseActivity<ReportErrorPresenter> implements ReportErrorView {
    public static final String TAG = ReportErrorActivity.class.getSimpleName();
    public static final String ARG_PRESENTER = "presenter";

    private WebView mWebView;
    private View mLoadingView;
    private ProgressBar mProgressBar;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report_error);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey(ARG_PRESENTER) && getPresenter() == null) {
                ReportErrorPresenter presenter = extras.getParcelable(ARG_PRESENTER);

                setPresenter(presenter);
            }
        }

        getPresenter().onCreate(savedInstanceState);

        mWebView = (WebView) findViewById(R.id.web_view);

        //Careful, this opens app to XSS attacks. Only use on trusted websites
        mWebView.getSettings().setJavaScriptEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.feedback_link);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLoadingView = findViewById(R.id.loading_view);

        mProgressBar = (ProgressBar) mLoadingView.findViewById(R.id.progress_bar);

        getPresenter().setView(this);
    }

    @Override
    public void postUrl(final String postUrl, byte[] postData) {
        mWebView.setWebViewClient(new ReportErrorWebViewClient(postUrl));
        mWebView.postUrl(postUrl, postData);
    }

    @Override
    public void toggleLoading(final boolean loading) {
        float alpha = loading ? 1 : 0;

        if (loading) {
            mLoadingView.setVisibility(View.VISIBLE);

            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.animate();
        }

        mLoadingView.animate()
                .alpha(alpha)
                .setDuration(getResources().getInteger(R.integer.report_error_loading_transition_duration))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        if (!loading) {
                            mLoadingView.setVisibility(View.GONE);

                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private class ReportErrorWebViewClient extends WebViewClient {
        private String mReportErrorUrl;

        private boolean finished = false;

        public ReportErrorWebViewClient(String reportErrorUrl) {
            super();

            mReportErrorUrl = reportErrorUrl;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if (url.equals(mReportErrorUrl)) {
                finished = false;
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);

            if (mWebView.getProgress() == 100 && !finished) {
                finished = true;

                getPresenter().webViewFinishedLoading();
            }
        }
    }
}
