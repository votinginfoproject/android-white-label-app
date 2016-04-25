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
public class DirectionsListViewPresenterImpl extends DirectionsListViewPresenter implements DirectionsInteractor.DirectionsCallback {
    private static final String TAG = DirectionsListViewPresenterImpl.class.getSimpleName();

    private static final String STEPS_KEY = "STEPS";

    private Context mContext;

    private DirectionsInteractor mDirectionsInteractor;

    private String mTransitMode;
    private String mOriginCoordinates;
    private String mDestinationCoordinates;

    private ArrayList<Step> mSteps = new ArrayList<>();

    public DirectionsListViewPresenterImpl(Context context, String transitMode, String originCoordinates, String destinationCoordinates) {
        mContext = context;

        mTransitMode = transitMode;
        mOriginCoordinates = originCoordinates;
        mDestinationCoordinates = destinationCoordinates;

        mDirectionsInteractor = new DirectionsInteractor();

        requestDirections();
    }

    @Override
    public void onCreate(Bundle savedState) {
        Log.e(TAG, "On create!!");
        if (savedState != null) {
            mSteps = savedState.getParcelableArrayList(STEPS_KEY);
            getView().refresh();
        }
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        state.putParcelableArrayList(STEPS_KEY, mSteps);
    }

    @Override
    public void onDestroy() {
        setView(null);
    }

    @Override
    List<Step> getSteps() {
        return mSteps;
    }

    private void requestDirections() {
        String directionsKey = mContext.getString(R.string.google_api_browser_key);
        DirectionsRequest request = new DirectionsRequest(directionsKey, mTransitMode, mOriginCoordinates, mDestinationCoordinates);

        mDirectionsInteractor.enqueueRequest(request, this);
    }

    @Override
    public void directionsResponse(DirectionsResponse response) {
        mSteps.clear();

        List<Route> routes = response.routes;

        if (routes.size() > 0) {
            Route route = routes.get(0);
            for(Leg leg : route.legs) {
                mSteps.addAll(leg.steps);
            }
        }

        getView().refresh();
    }
}
