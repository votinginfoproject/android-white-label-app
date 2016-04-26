package com.votinginfoproject.VotingInformationProject.activities.directionsActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.constants.TransitModes;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;
import com.votinginfoproject.VotingInformationProject.models.TabData;
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
    private boolean mIsPresentingMap;

    private HashMap<String, Route> transitModesToRoutes = new HashMap<>();

    public DirectionsPresenterImpl(Context context, String originCoordinates, String destinationCoordinates) {
        mContext = context;
        mOriginCoordinates = originCoordinates;
        mDestinationCoordinates = destinationCoordinates;

        enqueueRequests();
    }

    @Override
    public void onCreate(Bundle savedState) {
        refreshView();
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
        refreshView();
    }

    @Override
    public void mapButtonPressed() {
        mIsPresentingMap = !mIsPresentingMap;
        getView().toggleMapDisplaying(mIsPresentingMap);
    }

    private void refreshView() {
        getView().refreshDataView();

        TabData[] tabs = getTabDataForTransitModes(getTransitModes());
        getView().setTabs(tabs);
    }

    public TabData[] getTabDataForTransitModes(String[] transitModes) {
        List<TabData> tabDataList = new ArrayList<>();

        for (String transitMode : transitModes) {
            tabDataList.add(getTabDataForTransitMode(transitMode));
        }

        TabData[] toReturn = new TabData[tabDataList.size()];
        return tabDataList.toArray(toReturn);
    }

    public TabData getTabDataForTransitMode(String transitMode) {
        switch (transitMode) {
            case TransitModes.DRIVING:
                return new TabData(R.drawable.ic_directions_car, R.string.directions_label_drive_button);
            case TransitModes.BICYCLING:
                return new TabData(R.drawable.ic_directions_bike, R.string.directions_label_bike_button);
            case TransitModes.TRANSIT:
                return new TabData(R.drawable.ic_directions_bus, R.string.directions_label_transit_button);
            case TransitModes.WALKING:
                return new TabData(R.drawable.ic_directions_walk, R.string.directions_label_walk_button);
        }

        return null;
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
