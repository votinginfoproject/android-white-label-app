package com.votinginfoproject.VotingInformationProject.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.aboutActivity.AboutVIPActivity;
import com.votinginfoproject.VotingInformationProject.models.CoordinatePair;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by max on 4/12/16.
 */

public class AboutVIPFragment extends Fragment {
    private static final String TAG = AboutVIPFragment.class.getSimpleName();

    private static final String ARG_TITLE = "mTitle";
    private static final String ARG_INFO_TEXT = "mInfoText";
    private static final String ARG_SHOWS_INFO_BUTTONS = "mShowsAdditionalInfoButtons";
    private static final String ARG_TRANSITION_POINT = "mTransitionPoint";
    private static final String ARG_IS_LOADING = "mIsLoading";
    private final int mAnimationDurationMillis = 400;
    private AboutVIPActivity myActivity;
    private String mInfoText;
    private String mTitle;
    private CoordinatePair mTransitionPoint;
    private boolean mShowsAdditionalInfoButtons;
    private boolean mIsLoading;
    private TextView mAboutTextView;
    private View mAdditionalInformationView;
    private View mTermsOfUseButton;
    private View mPrivacyPolicyButton;
    private Toolbar mToolbar;
    private View mProgressBarContainer;

    public static AboutVIPFragment newInstance(String titleText, String infoText, boolean isLoading, boolean showsAdditionalInfoButtons, CoordinatePair transitionPoint) {
        AboutVIPFragment fragment = new AboutVIPFragment();
        Bundle args = new Bundle();

        args.putString(ARG_TITLE, titleText);
        args.putParcelable(ARG_TRANSITION_POINT, transitionPoint);
        args.putString(ARG_INFO_TEXT, infoText);
        args.putBoolean(ARG_SHOWS_INFO_BUTTONS, showsAdditionalInfoButtons);
        args.putBoolean(ARG_IS_LOADING, isLoading);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            if (mInfoText == null) {
                mInfoText = getArguments().getString(ARG_INFO_TEXT);
            }

            mTitle = getArguments().getString(ARG_TITLE);
            mIsLoading = getArguments().getBoolean(ARG_IS_LOADING);
            mShowsAdditionalInfoButtons = getArguments().getBoolean(ARG_SHOWS_INFO_BUTTONS);
            mTransitionPoint = getArguments().getParcelable(ARG_TRANSITION_POINT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_vip, container, false);

        myActivity = (AboutVIPActivity) getActivity();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mAboutTextView = (TextView) view.findViewById(R.id.about_app);
        mAdditionalInformationView = view.findViewById(R.id.additional_information);
        mTermsOfUseButton = view.findViewById(R.id.button_terms_of_use);
        mPrivacyPolicyButton = view.findViewById(R.id.button_privacy_policy);
        mProgressBarContainer = view.findViewById(R.id.progress_bar_container);

        setupViewListeners();
        updateView();

        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                performRevealAnimation();
            }
        });

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myActivity.upButtonPressed();
            }
        });

        return view;
    }

    private void updateView() {
        if (mAboutTextView != null) {
            mAboutTextView.setText(getInfoText());
        }

        if (mTitle != null) {
            mToolbar.setTitle(mTitle);
        }

        if (mAdditionalInformationView != null) {
            mAdditionalInformationView.setVisibility(showsAdditionalInfoButtons() ? View.VISIBLE : View.GONE);
        }

        if (mProgressBarContainer != null) {

            if (mIsLoading) {
                mProgressBarContainer.setVisibility(View.VISIBLE);
            } else {
                mProgressBarContainer.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setupViewListeners() {
        mTermsOfUseButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    myActivity.termsOfUseClicked(event);
                }
                return false;
            }
        });

        mPrivacyPolicyButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    myActivity.privacyPolicyClicked(event);
                }
                return false;
            }
        });
    }

    private void performRevealAnimation() {
        if (mTransitionPoint != null) {
            Animator reveal = prepareRevealAnimator();
            reveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    //Required onAnimationStart override
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    myActivity.viewTransitionEnded();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    //Required onAnimationCancel override
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    //Required onAnimationRepeat override
                }
            });
            reveal.start();
        } else {
            myActivity.viewTransitionEnded();
        }
    }

    public Animator prepareRevealAnimator() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return prepareCircularAnimator(true, mTransitionPoint);
        }

        return prepareSlidingAnimator(true);
    }

    public Animator prepareUnrevealAnimator() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return prepareCircularAnimator(false, mTransitionPoint);
        }

        return prepareSlidingAnimator(false);
    }

    @TargetApi(21)
    private Animator prepareCircularAnimator(boolean isRevealing, CoordinatePair transitionPoint) {
        int viewRadius = getEnclosingCircleRadius(getView(), transitionPoint.x, transitionPoint.y);

        int from = isRevealing ? 0 : viewRadius;
        int to = isRevealing ? viewRadius : 0;

        Animator reveal = ViewAnimationUtils.createCircularReveal(getView(), transitionPoint.x, transitionPoint.y, from, to);
        reveal.setInterpolator(getAnimationInterpolator(isRevealing));
        reveal.setDuration(mAnimationDurationMillis);
        return reveal;
    }

    private Animator prepareSlidingAnimator(boolean isRevealing) {
        int left = getView().getLeft();
        int right = getView().getLeft() + getView().getWidth();

        int from = isRevealing ? right : left;
        int to = isRevealing ? left : right;

        ObjectAnimator anim = ObjectAnimator.ofFloat(getView(), "x", from, to);
        anim.setInterpolator(getAnimationInterpolator(isRevealing));
        anim.setDuration(mAnimationDurationMillis);
        return anim;
    }

    private TimeInterpolator getAnimationInterpolator(boolean isRevealing) {
        float animationSmoothing = 2f;
        if (isRevealing) {
            return new DecelerateInterpolator(animationSmoothing);
        }

        return new AccelerateInterpolator(animationSmoothing);
    }

    private int getEnclosingCircleRadius(View v, int cx, int cy) {
        int realCenterX = cx + v.getLeft();
        int realCenterY = cy + v.getTop();
        int distanceTopLeft = (int) Math.hypot(realCenterX - v.getLeft(), realCenterY - v.getTop());
        int distanceTopRight = (int) Math.hypot(v.getRight() - realCenterX, realCenterY - v.getTop());
        int distanceBottomLeft = (int) Math.hypot(realCenterX - v.getLeft(), v.getBottom() - realCenterY);
        int distanceBottomRight = (int) Math.hypot(v.getRight() - realCenterX, v.getBottom() - realCenterY);

        Integer[] distances = new Integer[]{distanceTopLeft, distanceTopRight, distanceBottomLeft,
                distanceBottomRight};

        return Collections.max(Arrays.asList(distances));
    }

    public void setTitle(String newTitle) {
        mTitle = newTitle;
        mToolbar.setTitle(mTitle);
    }

    public String getInfoText() {
        return mInfoText;
    }

    public void setInfoText(String infoText) {
        this.mInfoText = infoText;
        updateView();
    }


    public boolean showsAdditionalInfoButtons() {
        return mShowsAdditionalInfoButtons;
    }

    public void setShowsAdditionalInfoButtons(boolean showsAdditionalInfoButtons) {
        this.mShowsAdditionalInfoButtons = showsAdditionalInfoButtons;
        updateView();
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    public void setLoading(boolean loading) {
        mIsLoading = loading;
        updateView();
    }
}
