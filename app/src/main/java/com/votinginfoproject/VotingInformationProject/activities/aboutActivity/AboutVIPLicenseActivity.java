package com.votinginfoproject.VotingInformationProject.activities.aboutActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;
import com.votinginfoproject.VotingInformationProject.constants.ExtraConstants;

/**
 * Created by max on 4/11/16.
 */
public class AboutVIPLicenseActivity extends BaseActivity<AboutVIPPresenter> implements AboutVIPView {
    private  final String TAG = AboutVIPLicenseActivity.class.getSimpleName();

    private TextView mAboutTextView;

    private View mTermsOfUseButton;
    private View mPrivacyPolicyButton;
    private View mLegalNoticesbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_vip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getPresenter() == null) {
            Bundle extras = getIntent().getExtras();
            int titleKey = R.string.about_app_title;

            if (extras != null) {
                titleKey = extras.getInt(ExtraConstants.TITLE_KEY, titleKey);
            }

            setPresenter(new AboutVIPLicensePresenterImpl(getApplicationContext(),
                    titleKey));
        }

        mAboutTextView = (TextView) findViewById(R.id.about_app);
        mTermsOfUseButton = findViewById(R.id.button_terms_of_use);
        mPrivacyPolicyButton = findViewById(R.id.button_privacy_policy);
        mLegalNoticesbutton = findViewById(R.id.button_legal_notices);

        setupViewListeners();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void setupViewListeners() {
        mTermsOfUseButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getPresenter().termsOfUseClicked(event);
                }
                return false;
            }
        });

        mPrivacyPolicyButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getPresenter().privacyPolicyClicked(event);
                }
                return false;
            }
        });

        mLegalNoticesbutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getPresenter().legalNoticesClicked(event);
                }
                return false;
            }
        });
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
    }

    @Override
    public void setInformationText(String informationText) {
        mAboutTextView.setText(informationText);
    }

    @Override
    public void navigateToAboutVIPView(int infoTitleKey, int infoTextKey, float transitionPointX, float transitionPointY) {
        Intent intent = new Intent(this, AboutVIPActivity.class);

        intent.putExtra(ExtraConstants.TITLE_KEY, infoTitleKey);
        intent.putExtra(ExtraConstants.DESCRIPTION_KEY, infoTextKey);

        startActivity(intent);
    }

    @Override
    public void navigateToAboutVIPLicenseVIew(int infoTitleKey, float transitionPointX, float transitionPointY) {
        Intent intent = new Intent(this, AboutVIPLicenseActivity.class);

        intent.putExtra(ExtraConstants.TITLE_KEY, infoTitleKey);

        startActivity(intent);
    }
}
