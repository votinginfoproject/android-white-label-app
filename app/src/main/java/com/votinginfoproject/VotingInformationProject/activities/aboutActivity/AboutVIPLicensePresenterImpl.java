package com.votinginfoproject.VotingInformationProject.activities.aboutActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.MotionEvent;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.CoordinatePair;
import com.votinginfoproject.VotingInformationProject.models.api.interactors.BaseInteractor;
import com.votinginfoproject.VotingInformationProject.models.api.interactors.OpenSourceLicenseInteractor;
import com.votinginfoproject.VotingInformationProject.models.api.requests.RequestType;

/**
 * Created by max on 4/11/16.
 */
public class AboutVIPLicensePresenterImpl extends AboutVIPPresenter implements OpenSourceLicenseInteractor.OpenSourceLicenceCallback {
    private static final String TAG = AboutVIPLicensePresenterImpl.class.getSimpleName();

    private static final String INFO_TITLE_KEY = "INFO_TITLE";
    private static final String INFO_TEXT_KEY = "INFO_TEXT";

    protected Integer mTitleStringID;
    protected String mTitle;

    protected String mInfoText;

    private OpenSourceLicenseInteractor mLicenseInteractor;
    private CoordinatePair mTransitionPoint;

    public AboutVIPLicensePresenterImpl(Context context, @StringRes int titleStringID, CoordinatePair transitionPoint) {
        mTitleStringID = titleStringID;
        mTitle = context.getString(mTitleStringID);
        mTransitionPoint = transitionPoint;

        kickoffLoading(context);
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
        updateView();

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

    private void kickoffLoading(Context context) {
        mLicenseInteractor = new OpenSourceLicenseInteractor(context);

        //No request needed
        mLicenseInteractor.enqueueRequest(new RequestType() {
            @Override
            public String buildQueryString() {
                return null;
            }
        }, this);
    }

    private void updateView() {
        getView().setTitle(mTitle);
        getView().setInformationText(mInfoText);
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

    // Interactor Callback

    @Override
    public void openSourceLicenseResponse(String licenseInfo) {
        mInfoText = licenseInfo;
        updateView();
    }
}
