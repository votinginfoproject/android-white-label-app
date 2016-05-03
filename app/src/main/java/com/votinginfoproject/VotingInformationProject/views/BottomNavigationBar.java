package com.votinginfoproject.VotingInformationProject.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.votinginfoproject.VotingInformationProject.R;

import java.lang.ref.WeakReference;

/**
 * Created by marcvandehey on 4/5/16.
 */
public class BottomNavigationBar extends LinearLayout implements View.OnClickListener {
    private BottomNavigationButton pollsNavigationButton;
    private BottomNavigationButton ballotNavigationButton;
    private BottomNavigationButton detailsNavigationButton;

    private WeakReference<BottomNavigationBarCallback> mTabBarListener;

    public BottomNavigationBar(Context context) {
        super(context);

        styleUI(context);
    }

    public BottomNavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        styleUI(context);
    }

    public BottomNavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        styleUI(context);
    }

    public void setListener(BottomNavigationBarCallback callback) {
        if (callback != null) {
            mTabBarListener = new WeakReference<>(callback);
        } else if (mTabBarListener != null) {
            mTabBarListener.clear();
            mTabBarListener = null;
        }
    }

    private void styleUI(Context context) {
        inflate(context, R.layout.bottom_navigation_bar, this);

        pollsNavigationButton = (BottomNavigationButton) findViewById(R.id.nav_button_polls);
        pollsNavigationButton.setClickable(true);
        pollsNavigationButton.setOnClickListener(this);
        pollsNavigationButton.setSelected(false);

        ballotNavigationButton = (BottomNavigationButton) findViewById(R.id.nav_button_ballot);
        ballotNavigationButton.setOnClickListener(this);
        ballotNavigationButton.setUnselected(false);

        detailsNavigationButton = (BottomNavigationButton) findViewById(R.id.nav_button_details);
        detailsNavigationButton.setOnClickListener(this);
        detailsNavigationButton.setUnselected(false);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(pollsNavigationButton)) {
            pollsNavigationButton.setSelected();
            ballotNavigationButton.setUnselected();
            detailsNavigationButton.setUnselected();

            mTabBarListener.get().pollsButtonSelected();
        } else if (v.equals(ballotNavigationButton)) {
            pollsNavigationButton.setUnselected();
            ballotNavigationButton.setSelected();
            detailsNavigationButton.setUnselected();

            mTabBarListener.get().ballotButtonSelected();
        } else if (v.equals(detailsNavigationButton)) {
            pollsNavigationButton.setUnselected();
            ballotNavigationButton.setUnselected();
            detailsNavigationButton.setSelected();

            mTabBarListener.get().detailsButtonSelected();
        }
    }

    public interface BottomNavigationBarCallback {
        void pollsButtonSelected();

        void ballotButtonSelected();

        void detailsButtonSelected();
    }
}
