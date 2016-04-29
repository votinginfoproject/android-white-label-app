package com.votinginfoproject.VotingInformationProject.activities.directionsActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by max on 4/25/16.
 */
public class DirectionsPresenterImpl extends DirectionsPresenter implements DirectionsInteractor.DirectionsCallback {
    private static final String TAG = DirectionsPresenterImpl.class.getSimpleName();

    private Context mContext;

    private boolean mUsingLastKnownLocation;
    private PollingLocation mPollingLocation;

    private String[] mAllTransitModes = TransitModes.ALL;
    private HashMap<String, Route> transitModesToRoutes = new HashMap<>();

    private List<String> mQueuedTransitModes = new ArrayList<>();
    private HashMap<String, DirectionsInteractor> mTransitModesToInteractors = new HashMap<>();

    private int mIndexOfPresentedRoute;
    private boolean mIsPresentingMap;

    public DirectionsPresenterImpl(Context context, PollingLocation pollingLocation, boolean useLastKnownLocation) {
        mContext = context;
        mPollingLocation = pollingLocation;
        mUsingLastKnownLocation = useLastKnownLocation;
    }

    @Override
    public void onCreate(Bundle savedState) {
        for (String transitMode : mAllTransitModes) {
            if (savedState != null && savedState.containsKey(transitMode)) {
                transitModesToRoutes.put(transitMode, (Route) savedState.getParcelable(transitMode));
            } else {
                mQueuedTransitModes.add(transitMode);
            }
        }

        if (!mUsingLastKnownLocation) {
            tryEnqueueRequestsFromOrigin();
        }

        if (getView() != null) {
            refreshViewData();
            updateViewMap();
        }
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        for (String transitMode: getTransitModes()) {
            state.putParcelable(transitMode, getRouteForTransitMode(transitMode));
        }
    }

    @Override
    public void onDestroy() {
        for (DirectionsInteractor interactor : mTransitModesToInteractors.values()) {
            interactor.cancel(true);
        }

        setView(null);
    }

    @Override
    public void onAttachView(DirectionsView view) {
        super.onAttachView(view);

        if (getView() != null) {
            if (mUsingLastKnownLocation && !locationServicesEnabled()) {
                getView().toggleEnableLocationView(true);
            }

            getView().toggleLoadingView(isLoading());
            refreshViewData();
        }
    }

    @Override
    public void lastKnownLocationUpdated() {
        if (mUsingLastKnownLocation) {
            requestAllTransitModes();
        }
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
        return !mTransitModesToInteractors.isEmpty();
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

        mTransitModesToInteractors.remove(transitMode);

        if (!response.hasErrors()) {
            if (response.routes.size() > 0) {
                transitModesToRoutes.put(transitMode, response.routes.get(0));

            } else {
                transitModesToRoutes.put(transitMode, null);
            }

            if (getView() != null) {
                refreshViewData();

                //If done loading, tell the view to update its map
                if (!isLoading()) {
                    updateViewMap();
                }
            }
        } else {
            showConnectionError();
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

    @Override
    public void launchSettingsButtonPressed() {
        getView().navigateToAppSettings();
    }

    @Override
    public void retryButtonPressed() {
        requestAllTransitModes();

        getView().toggleConnectionErrorView(false);
        getView().toggleLoadingView(true);
    }

    @Override
    public void onMapReady() {
        updateViewMap();
    }

    @Override
    public boolean locationServicesEnabled() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAllTransitModes() {
        mQueuedTransitModes.clear();
        mQueuedTransitModes.addAll(Arrays.asList(mAllTransitModes));

        tryEnqueueRequestsFromOrigin();
    }

    private void showConnectionError() {
        if (getView() != null) {
            getView().toggleConnectionErrorView(true);
        }
    }

    private void refreshViewData() {
        getView().refreshViewData();
        getView().toggleLoadingView(isLoading());

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

    private void tryEnqueueRequestsFromOrigin() {
        Location origin = null;

        if (mUsingLastKnownLocation) {
            if (locationServicesEnabled()) {
                origin = VoterInformation.getLastKnownLocation();
            } else if (getView() != null){
                getView().toggleEnableLocationView(true);
            }
        } else {
            origin = new Location();
            origin.lat = (float) VoterInformation.getHomeAddress().getLocation().latitude;
            origin.lng = (float) VoterInformation.getHomeAddress().getLocation().longitude;
        }

        if (origin != null) {
            for (String transitMode : mQueuedTransitModes) {
                enqueueRequest(transitMode, origin);
            }
        }
    }

    private void enqueueRequest(String transitMode, Location origin) {
        if (origin != null && mPollingLocation.location != null) {
            String directionsKey = mContext.getString(R.string.google_api_browser_key);

            DirectionsRequest request = new DirectionsRequest(directionsKey, transitMode, origin, mPollingLocation.location);

            DirectionsInteractor interactor = new DirectionsInteractor();
            interactor.enqueueRequest(request, this);

            mTransitModesToInteractors.put(transitMode, interactor);
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
