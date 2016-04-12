package com.votinginfoproject.VotingInformationProject.activities.aboutActivity;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;
import com.votinginfoproject.VotingInformationProject.constants.ExtraConstants;
import com.votinginfoproject.VotingInformationProject.models.CoordinatePair;

import org.w3c.dom.Text;


public class AboutVIPActivity extends BaseActivity<AboutVIPPresenter> implements AboutVIPView {
    private  final String TAG = AboutVIPActivity.class.getSimpleName();

    private TextView mAboutTextView;

    private View mAdditionalInformationView;

    private View mTermsOfUseButton;
    private View mPrivacyPolicyButton;
    private View mLegalNoticesbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_vip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int baseTitleKey = R.string.about_app_title;
        int titleKey = baseTitleKey;
        int descriptionKey = R.string.about_app_description;
        CoordinatePair transitionPoint = null;

        if (getPresenter() == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                titleKey = extras.getInt(ExtraConstants.TITLE_KEY, titleKey);
                descriptionKey = extras.getInt(ExtraConstants.DESCRIPTION_KEY, descriptionKey);
                transitionPoint = extras.getParcelable(ExtraConstants.TRANSITION_START_KEY);
            }
        }

        setPresenter(new AboutVIPPresenterImpl(getApplicationContext(),
                titleKey,
                descriptionKey,
                transitionPoint));

        if (transitionPoint != null) {
            findViewById(android.R.id.content).setVisibility(View.INVISIBLE);
        }

        mAboutTextView = (TextView) findViewById(R.id.about_app);
        mAdditionalInformationView = findViewById(R.id.additional_information);
        mTermsOfUseButton = findViewById(R.id.button_terms_of_use);
        mPrivacyPolicyButton = findViewById(R.id.button_privacy_policy);
        mLegalNoticesbutton = findViewById(R.id.button_legal_notices);

        //We only show additional information when on the base AboutVIP screen
        if(titleKey == baseTitleKey) {
            mAdditionalInformationView.setVisibility(View.VISIBLE);
        }

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

    @TargetApi(21)
    public void performCircularReveal(final CoordinatePair transitionPoint) {
        final View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);

                    int cx = transitionPoint.x;
                    int cy = transitionPoint.y;

                    Log.e(TAG, v.getHeight() + " (" + cx + ", " + cy + ")");

                    int radius = (int) Math.hypot(right, bottom);

                    Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
                    reveal.setInterpolator(new DecelerateInterpolator(2f));
                    reveal.setDuration(1000);
                    rootView.setVisibility(View.VISIBLE);
                    reveal.start();
                }
            });
        }
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
    public void navigateToAboutVIPView(int infoTitleKey, int infoTextKey, CoordinatePair transitionPoint) {
        Intent intent = new Intent(this, AboutVIPActivity.class);
        intent.putExtra(ExtraConstants.TITLE_KEY, infoTitleKey);
        intent.putExtra(ExtraConstants.DESCRIPTION_KEY, infoTextKey);
        intent.putExtra(ExtraConstants.TRANSITION_START_KEY, transitionPoint);

        startActivity(intent);
    }

    @Override
    public void navigateToAboutVIPLicenseView(int infoTitleKey, CoordinatePair transitionPoint) {
        Intent intent = new Intent(this, AboutVIPLicenseActivity.class);

        intent.putExtra(ExtraConstants.TITLE_KEY, infoTitleKey);
        intent.putExtra(ExtraConstants.TRANSITION_START_KEY, transitionPoint);

        startActivity(intent);
    }
}
