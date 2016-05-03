package com.votinginfoproject.VotingInformationProject.activities.reportErrorActivity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;

/**
 * Created by max on 5/3/16.
 */
public class ReportErrorActivity extends BaseActivity<ReportErrorPresenter> implements ReportErrorView {
    public static final String TAG = ReportErrorActivity.class.getSimpleName();

    public static final String ARG_PRESENTER = "presenter";

    private Toolbar mToolbar;

    private WebView mWebView;

    private View mLoadingView;

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

        mWebView = (WebView) findViewById(R.id.web_view);

        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            private boolean finished = false;

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);

                if(mWebView.getProgress()==100 && !finished){
                    finished = true;

                    getPresenter().webViewFinishedLoading();
                }
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.feedback_link);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLoadingView = findViewById(R.id.loading_view);

        getPresenter().setView(this);
    }

    @Override
    public void postUrl(String url, byte[] postData) {
        mWebView.postUrl(url, postData);
    }

    @Override
    public void toggleLoading(boolean loading) {
        mLoadingView.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
