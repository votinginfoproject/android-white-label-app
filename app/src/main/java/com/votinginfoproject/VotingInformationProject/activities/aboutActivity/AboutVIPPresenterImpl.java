package com.votinginfoproject.VotingInformationProject.activities.aboutActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.MotionEvent;

import com.google.android.gms.common.GoogleApiAvailability;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.CoordinatePair;

/**
 * Created by max on 4/8/16.
 */
public class AboutVIPPresenterImpl extends AboutVIPPresenter {
    private static final String TAG = AboutVIPPresenterImpl.class.getSimpleName();

    private final int ABOUT_APP_TITLE_KEY = R.string.about_app_title;
    private final int ABOUT_APP_DESCRIPTION_KEY = R.string.about_app_description;

    private final int TERMS_TITLE_KEY = R.string.about_title_terms;
    private final int TERMS_DESCRIPTION_KEY = R.string.about_terms_description;

    private final int PRIVACY_TITLE_KEY = R.string.about_title_privacy;
    private final int PRIVACY_DESCRIPTION_KEY = R.string.about_privacy_description;

    private final int LEGAL_NOTICES_TITLE_KEY = R.string.about_title_legal_notices;
    private final int LEGAL_NOTICES_LOADING_KEY = R.string.about_loading_legal_notices;

    private Context mContext;

    private boolean mHasAttachedView = false;
    private boolean mPresentingLegalNotices = false;

    public AboutVIPPresenterImpl(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedState) {
        //Required onCreate override
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        //Required onSaveState override
    }

    @Override
    public void onDestroy() {
        setView(null);
    }

    @Override
    public void onAttachView(AboutVIPView aboutVIPView) {
        super.onAttachView(aboutVIPView);
        if (!mHasAttachedView) {
            mHasAttachedView = true;
            navigateToInitialAboutView();
        }
    }

    @Override
    void termsOfUseClicked(MotionEvent event) {
        navigateToNewAboutView(TERMS_TITLE_KEY,
                TERMS_DESCRIPTION_KEY,
                new CoordinatePair((int) event.getRawX(), (int) event.getRawY()));
    }

    @Override
    void privacyPolicyClicked(MotionEvent event) {
        navigateToNewAboutView(PRIVACY_TITLE_KEY,
                PRIVACY_DESCRIPTION_KEY,
                new CoordinatePair((int) event.getRawX(), (int) event.getRawY()));
    }

    @Override
    void legalNoticesClicked(MotionEvent event) {
        mPresentingLegalNotices = true;

        navigateToNewAboutView(LEGAL_NOTICES_TITLE_KEY,
                LEGAL_NOTICES_LOADING_KEY,
                new CoordinatePair((int) event.getRawX(), (int) event.getRawY()));
    }

    @Override
    void onBackPressed() {
        mPresentingLegalNotices = false;
        getView().navigateToPreviousView();
    }

    @Override
    void viewTransitionEnded() {
        if(mPresentingLegalNotices) {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            getView().setInformationText(apiAvailability.getOpenSourceSoftwareLicenseInfo(mContext));
        }
    }

    private void navigateToNewAboutView(@StringRes int titleTextKey, @StringRes int descriptionTextKey, CoordinatePair transitionPoint) {
        navigateToNewAboutView(
                mContext.getString(titleTextKey),
                mContext.getString(descriptionTextKey),
                transitionPoint);
    }

    private void navigateToInitialAboutView() {
        String defaultTitle = mContext.getString(ABOUT_APP_TITLE_KEY);
        String defaultInfoText = mContext.getString(ABOUT_APP_DESCRIPTION_KEY);

        getView().navigateToAboutView(defaultTitle, defaultInfoText, true, null);
    }

    private void navigateToNewAboutView(String titleText, String infoText, CoordinatePair transitionPoint) {
        getView().navigateToAboutView(titleText, infoText, false, transitionPoint);
    }
}
