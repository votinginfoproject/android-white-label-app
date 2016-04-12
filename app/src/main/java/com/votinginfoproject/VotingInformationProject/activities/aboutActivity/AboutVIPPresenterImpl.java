package com.votinginfoproject.VotingInformationProject.activities.aboutActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.MotionEvent;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.CoordinatePair;
import com.votinginfoproject.VotingInformationProject.models.api.interactors.OpenSourceLicenseInteractor;

/**
 * Created by max on 4/8/16.
 */
public class AboutVIPPresenterImpl extends AboutVIPPresenter {

    private static final String TAG = AboutVIPPresenterImpl.class.getSimpleName();

    private static final String INFO_TITLE_KEY = "INFO_TITLE";
    private static final String INFO_TEXT_KEY = "INFO_TEXT";

    protected Integer mTitleStringID;
    protected String mTitle;

    protected Integer mInfoTextStringID;
    protected String mInfoText;

    private CoordinatePair mTransitionPoint;

    public AboutVIPPresenterImpl(Context context, @StringRes int titleStringID, @StringRes int infoTextStringID, CoordinatePair transitionPoint) {
        mTitleStringID = titleStringID;
        mTitle = context.getString(mTitleStringID);

        mInfoTextStringID = infoTextStringID;
        mInfoText = context.getString(mInfoTextStringID);

        mTransitionPoint = transitionPoint;
    }

    @Override
    public void onCreate(Bundle savedState) {
        if (savedState != null) {
            mTitle = savedState.getString(INFO_TITLE_KEY);
            mInfoText = savedState.getString(INFO_TEXT_KEY);
        }


    }

    @Override
    public void onAttachView(AboutVIPView aboutVIPView) {
        super.onAttachView(aboutVIPView);

        aboutVIPView.setTitle(mTitle);
        aboutVIPView.setInformationText(mInfoText);

        if (mTransitionPoint != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getView().performCircularReveal(mTransitionPoint);
        }
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        state.putString(INFO_TITLE_KEY, mTitle);
        state.putString(INFO_TEXT_KEY, mInfoText);
    }

    @Override
    public void onDestroy() {
        setView(null);
    }

    @Override
    void termsOfUseClicked(MotionEvent event) {
        CoordinatePair transitionPoint = new CoordinatePair((int) event.getRawX(), (int) event.getRawY());
        getView().navigateToAboutVIPView(R.string.about_title_terms,
                R.string.about_terms_description,
                transitionPoint);
    }

    @Override
    void privacyPolicyClicked(MotionEvent event) {
        CoordinatePair transitionPoint = new CoordinatePair((int) event.getRawX(), (int) event.getRawY());
        getView().navigateToAboutVIPView(R.string.about_title_privacy,
                R.string.about_privacy_description,
                transitionPoint);
    }

    @Override
    void legalNoticesClicked(MotionEvent event) {
        CoordinatePair transitionPoint = new CoordinatePair((int) event.getRawX(), (int) event.getRawY());
        getView().navigateToAboutVIPLicenseView(R.string.about_title_legal_notices,
                transitionPoint);
    }
}
