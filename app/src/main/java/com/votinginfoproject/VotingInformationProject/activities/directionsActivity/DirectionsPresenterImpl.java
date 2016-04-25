package com.votinginfoproject.VotingInformationProject.activities.directionsActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.constants.TransitModes;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;
import com.votinginfoproject.VotingInformationProject.models.api.interactors.DirectionsInteractor;
import com.votinginfoproject.VotingInformationProject.models.api.requests.DirectionsRequest;
import com.votinginfoproject.VotingInformationProject.models.api.responses.DirectionsResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by max on 4/25/16.
 */
public class DirectionsPresenterImpl extends DirectionsPresenter implements DirectionsInteractor.DirectionsCallback {
    private String[] mAllTransitModes = TransitModes.ALL;

    private Context mContext;
    private List<String> mLoadingTransitModes = new ArrayList<>();
    private List<DirectionsInteractor> mDirectionsInteractors = new ArrayList<>();
    private String mOriginCoordinates;
    private String mDestinationCoordinates;

    private HashMap<String, Route> transitModesToRoutes = new HashMap<>();

    public DirectionsPresenterImpl(Context context, String originCoordinates, String destinationCoordinates) {
        mContext = context;
        mOriginCoordinates = originCoordinates;
        mDestinationCoordinates = destinationCoordinates;

        enqueueRequests();
    }

    @Override
    public void onCreate(Bundle savedState) {

    }

    @Override
    public void onSaveState(@NonNull Bundle state) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public String[] getTransitModes() {
        List<String> validTransitModes = new ArrayList<>();

        if (!isLoading()) {
            for (String transitMode : mAllTransitModes) {
                Route route = getRouteForTransitMode(transitMode);

                if (route != null && route.legs.size() > 0) {
                    validTransitModes.add(transitMode);
                }
            }
        }

        String[] toReturn = new String[validTransitModes.size()];
        return validTransitModes.toArray(toReturn);
    }

    @Override
    public boolean isLoading() {
        return mLoadingTransitModes.size() != 0;
    }

    @Override
    public Route getRouteForTransitMode(String transitMode) {
        return transitModesToRoutes.get(transitMode);
    }

    @Override
    public void tabSelectedAtIndex(int index) {
        getView().navigateToDirectionsListAtIndex(index);
    }

    @Override
    public void swipedToDirectionsListAtIndex(int index) {
        getView().selectTabAtIndex(index);
    }

    @Override
    public String getOriginCoordinates() {
        return mOriginCoordinates;
    }

    @Override
    public String getDestinationCoordinates() {
        return mDestinationCoordinates;
    }

    @Override
    public void directionsResponse(DirectionsResponse response) {
        String transitMode = response.mode;

        mLoadingTransitModes.remove(transitMode);

        if (response.routes.size() > 0) {
            transitModesToRoutes.put(transitMode, response.routes.get(0));
        } else {
            transitModesToRoutes.put(transitMode, null);
        }
        getView().refresh();
    }

    @Override
    public int getTabImageForTransitMode(String transitMode) {
        switch (transitMode) {
            case TransitModes.DRIVING:
                return R.drawable.ic_directions_car;
            case TransitModes.BICYCLING:
                return R.drawable.ic_directions_bike;
            case TransitModes.TRANSIT:
                return R.drawable.ic_directions_bus;
            case TransitModes.WALKING:
                return R.drawable.ic_directions_walk;
        }
        return 0;
    }

    private void enqueueRequests() {
        mDirectionsInteractors.clear();
        mLoadingTransitModes.clear();

        String directionsKey = mContext.getString(R.string.google_api_browser_key);

        for (String transitMode : mAllTransitModes) {
            mLoadingTransitModes.add(transitMode);

            DirectionsRequest request = new DirectionsRequest(directionsKey, transitMode, getOriginCoordinates(), getDestinationCoordinates());

            DirectionsInteractor interactor = new DirectionsInteractor();
            mDirectionsInteractors.add(interactor);
            interactor.enqueueRequest(request, this);
        }
    }
}
