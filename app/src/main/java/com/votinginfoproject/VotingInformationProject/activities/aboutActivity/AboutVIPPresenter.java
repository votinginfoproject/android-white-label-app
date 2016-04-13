package com.votinginfoproject.VotingInformationProject.activities.aboutActivity;

import android.view.MotionEvent;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;

/**
 * Created by max on 4/8/16.
 */
public abstract class AboutVIPPresenter extends BasePresenter<AboutVIPView> {

    abstract void viewTransitionEnded();

    abstract void termsOfUseClicked(MotionEvent event);

    abstract void privacyPolicyClicked(MotionEvent event);

    abstract void legalNoticesClicked(MotionEvent event);

    abstract void onBackPressed();
}
