package com.votinginfoproject.VotingInformationProject.activities.aboutActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;
import com.votinginfoproject.VotingInformationProject.constants.ExtraConstants;

import org.w3c.dom.Text;


public class AboutVIPActivity extends BaseActivity<AboutVIPPresenter> implements AboutVIPView {
    private  final String TAG = AboutVIPActivity.class.getSimpleName();

    private TextView mAboutTextView;

    private View mTermsOfUseButton;
    private View mPrivacyPolicyButton;
    private View mLegalNoticesbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate!");
        setContentView(R.layout.activity_about_vip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getPresenter() == null) {
            Bundle extras = getIntent().getExtras();
            int titleKey = R.string.about_app_title;
            int descriptionKey = R.string.about_app_description;
            if (extras != null) {
                Log.e(TAG, "Got extras!!");
                titleKey = extras.getInt(ExtraConstants.TITLE_KEY, titleKey);
                descriptionKey = extras.getInt(ExtraConstants.DESCRIPTION_KEY, descriptionKey);
            }
            setPresenter(new AboutVIPPresenterImpl(getApplicationContext(),
                    titleKey,
                    descriptionKey));
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
        Log.e(TAG, "Going to next view!");

        Intent intent = new Intent(this, AboutVIPActivity.class);

        intent.putExtra(ExtraConstants.TITLE_KEY, infoTitleKey);
        intent.putExtra(ExtraConstants.DESCRIPTION_KEY, infoTextKey);

        startActivity(intent);
    }
}
