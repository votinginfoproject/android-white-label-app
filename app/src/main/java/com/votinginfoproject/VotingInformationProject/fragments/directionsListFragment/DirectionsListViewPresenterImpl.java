package com.votinginfoproject.VotingInformationProject.fragments.directionsListFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Leg;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Step;
import com.votinginfoproject.VotingInformationProject.models.api.interactors.DirectionsInteractor;
import com.votinginfoproject.VotingInformationProject.models.api.requests.DirectionsRequest;
import com.votinginfoproject.VotingInformationProject.models.api.responses.DirectionsResponse;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by max on 4/22/16.
 */
public class DirectionsListViewPresenterImpl extends DirectionsListViewPresenter {
    private static final String TAG = DirectionsListViewPresenterImpl.class.getSimpleName();

    private static final String STEPS_KEY = "STEPS";

    private Context mContext;

    private Route mRoute;
    private List<Step> mSteps = new ArrayList<>();

    public DirectionsListViewPresenterImpl(Context context, Route route) {
        mContext = context;
        for (Leg leg : route.legs) {
            mSteps.addAll(leg.steps);
        }
    }

    @Override
    public void onCreate(Bundle savedState) {
        if (savedState != null) {
            getView().refresh();
        }
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        //state.putParcelableArrayList(STEPS_KEY, mSteps);
    }

    @Override
    public void onDestroy() {
        setView(null);
    }

    @Override
    List<Step> getSteps() {
        return mSteps;
    }
}
