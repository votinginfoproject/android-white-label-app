package com.votinginfoproject.VotingInformationProject.fragments.directionsListFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Leg;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Step;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by max on 4/22/16.
 */
public class DirectionsListViewPresenterImpl extends DirectionsListViewPresenter {
    private static final String TAG = DirectionsListViewPresenterImpl.class.getSimpleName();

    private ArrayList<Step> mSteps = new ArrayList<>();

    public DirectionsListViewPresenterImpl(Route route) {
        for (Leg leg : route.legs) {
            mSteps.addAll(leg.steps);
        }
    }

    @Override
    public void onCreate(Bundle savedState) {
        if (getView() != null) {
            getView().refreshViewData();
        }
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        //Required empty override method
    }

    @Override
    public void onDestroy() {
        setView(null);
    }

    @Override
    public List<Step> getSteps() {
        return mSteps;
    }
}
