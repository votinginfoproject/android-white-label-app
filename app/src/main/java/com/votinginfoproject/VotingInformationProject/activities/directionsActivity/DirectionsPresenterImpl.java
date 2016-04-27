package com.votinginfoproject.VotingInformationProject.activities.directionsActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.constants.TransitModes;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Location;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.TabData;
import com.votinginfoproject.VotingInformationProject.models.api.interactors.DirectionsInteractor;
import com.votinginfoproject.VotingInformationProject.models.api.requests.DirectionsRequest;
import com.votinginfoproject.VotingInformationProject.models.api.responses.DirectionsResponse;
import com.votinginfoproject.VotingInformationProject.models.singletons.VoterInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by max on 4/25/16.
 */
public class DirectionsPresenterImpl extends DirectionsPresenter implements DirectionsInteractor.DirectionsCallback {
    private static final String TAG = DirectionsPresenterImpl.class.getSimpleName();

    private Context mContext;

    private PollingLocation mPollingLocation;

    private String[] mAllTransitModes = TransitModes.ALL;
    private HashMap<String, Route> transitModesToRoutes = new HashMap<>();
    private List<String> mLoadingTransitModes = new ArrayList<>();

    private int mIndexOfPresentedRoute;
    private boolean mIsPresentingMap;

    public DirectionsPresenterImpl(Context context, PollingLocation pollingLocation) {
        mContext = context;
        mPollingLocation = pollingLocation;

        enqueueRequests();
    }

    @Override
    public void onCreate(Bundle savedState) {
        refreshViewData();
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        //Required empty override method
    }

    @Override
    public void onDestroy() {
        //Required empty override method
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
        updatePresentingRouteIndex(index);
        getView().navigateToDirectionsListAtIndex(mIndexOfPresentedRoute);
    }

    @Override
    public void swipedToDirectionsListAtIndex(int index) {
        updatePresentingRouteIndex(index);
        getView().selectTabAtIndex(mIndexOfPresentedRoute);
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

        refreshViewData();

        getView().toggleLoading(isLoading());

        //If we're done loading, go ahead ahead and tell the view to update its map
        if (!isLoading()) {
            updateViewMap();
        }
    }

    @Override
    public void mapButtonPressed() {
        mIsPresentingMap = !mIsPresentingMap;
        getView().toggleMapDisplaying(mIsPresentingMap);
    }

    @Override
    public void externalMapButtonPressed() {
        getView().navigateToExternalMap(mPollingLocation.address.toGeocodeString());
    }

    private void refreshViewData() {
        getView().refreshViewData();

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
        mLoadingTransitModes.clear();

        Location origin = VoterInformation.getLastKnownLocation();

        if (origin != null && mPollingLocation.location != null) {
            String directionsKey = mContext.getString(R.string.google_api_browser_key);

            for (String transitMode : mAllTransitModes) {
                mLoadingTransitModes.add(transitMode);

                DirectionsRequest request = new DirectionsRequest(directionsKey, transitMode, origin, mPollingLocation.location);

                DirectionsInteractor interactor = new DirectionsInteractor();
                interactor.enqueueRequest(request, this);
            }
        }
    }

    private void updatePresentingRouteIndex(int newIndex) {
        if (newIndex != mIndexOfPresentedRoute) {
            mIndexOfPresentedRoute = newIndex;

            updateViewMap();
        }
    }

    private void updateViewMap() {
        String[] transitModes = getTransitModes();

        if (mIndexOfPresentedRoute < transitModes.length) {
            String transitMode = transitModes[mIndexOfPresentedRoute];

            getView().showRouteOnMap(getRouteForTransitMode(transitMode),
                    mPollingLocation.getDrawableMarker(true));
        }
    }
}
