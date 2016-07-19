package com.votinginfoproject.VotingInformationProject.activities.aboutActivity;

import android.animation.Animator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;
import com.votinginfoproject.VotingInformationProject.fragments.AboutVIPFragment;
import com.votinginfoproject.VotingInformationProject.models.CoordinatePair;

public class AboutVIPActivity extends BaseActivity<AboutVIPPresenter> implements AboutVIPView {
    private final String TAG = AboutVIPActivity.class.getSimpleName();

    private View mFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_vip);

        setPresenter(new AboutVIPPresenterImpl(getApplicationContext()));

        mFragmentContainer = findViewById(R.id.fragment_container);
    }

    @Override
    public void onBackPressed() {
        getPresenter().onBackPressed();
    }

    @Override
    public void setTitle(String title) {
        if (getTopFragment() != null) {
            getTopFragment().setTitle(title);
        }
    }

    @Override
    public void setInformationText(String informationText) {
        if (getTopFragment() != null) {
            getTopFragment().setInfoText(informationText);
        }
    }

    @Override
    public void setLoading(boolean loading) {
        if (getTopFragment() != null) {
            getTopFragment().setLoading(loading);
        }
    }

    @Override
    public void navigateToAboutView(String title, String infoText, boolean isLoading, boolean showsAdditionalInfoButtons, CoordinatePair transitionPoint) {
        showNewAboutScreen(title, infoText, isLoading, showsAdditionalInfoButtons, transitionPoint);
    }

    @Override
    public void navigateToPreviousView() {
        if (getFragmentCount() > 1) {
            AboutVIPFragment toRemove = getTopFragment();
            navigateToPreviousFragment(toRemove);
        } else {
            finish();
        }
    }

    private int getFragmentCount() {
        FragmentManager manager = getFragmentManager();
        return manager.getBackStackEntryCount();
    }

    @Nullable
    private AboutVIPFragment getTopFragment() {
        FragmentManager manager = getFragmentManager();
        int entryCount = manager.getBackStackEntryCount();

        if (entryCount > 0) {
            FragmentManager.BackStackEntry entry = manager.getBackStackEntryAt(entryCount - 1);
            Fragment topFragment = manager.findFragmentByTag(entry.getName());

            if (topFragment instanceof AboutVIPFragment) {
                return (AboutVIPFragment) topFragment;
            }
        }

        return null;
    }

    public void showNewAboutScreen(String title, String infoText, boolean isLoading, boolean showsAdditionalInfoButtons, CoordinatePair transitionPoint) {
        AboutVIPFragment newFragment = AboutVIPFragment.newInstance(title, infoText, isLoading, showsAdditionalInfoButtons, transitionPoint);

        String fragmentTag = newFragment.hashCode() + "";

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(mFragmentContainer.getId(), newFragment, fragmentTag);
        transaction.addToBackStack(fragmentTag);
        transaction.commit();
    }

    private void navigateToPreviousFragment(final AboutVIPFragment fragmentToRemove) {
        Animator unreveal = fragmentToRemove.prepareUnrevealAnimator();

        unreveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //Required onAnimationStart override
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getFragmentManager().popBackStackImmediate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                getFragmentManager().popBackStackImmediate();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //Required onAnimationRepeat override
            }
        });
        unreveal.start();
    }

    public void termsOfUseClicked(MotionEvent event) {
        getPresenter().termsOfUseClicked(event);
    }

    public void privacyPolicyClicked(MotionEvent event) {
        getPresenter().privacyPolicyClicked(event);
    }

    public void viewTransitionEnded() {
        getPresenter().viewTransitionEnded();
    }

    public void upButtonPressed() {
        onBackPressed();
    }
}
