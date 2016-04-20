package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.singletons.VoterInformation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by marcvandehey on 4/11/16.
 */
public class PollingSitesPresenterImpl extends PollingSitesPresenter {
    private static final String TAG = PollingSitesPresenterImpl.class.getSimpleName();
    HashMap<Marker, PollingLocation> mappedPollingLocations;
    private
    @LayoutRes
    int currentSort = R.id.sort_all;

    private PollingLocation lastClickedLocation;

    public PollingSitesPresenterImpl(PollingSitesView pollingSitesView) {
        mappedPollingLocations = new HashMap<>();
        setView(pollingSitesView);
    }

    public PollingSitesPresenterImpl(PollingSitesView pollingSitesView, @LayoutRes int selectedSort) {
        mappedPollingLocations = new HashMap<>();
        setView(pollingSitesView);
        currentSort = selectedSort;
    }

    @Override
    public ArrayList<PollingLocation> getSortedLocations() {
        switch (currentSort) {
            case R.id.sort_all:
                return VoterInformation.getAllPollingLocations();
            case R.id.sort_polling_locations:
                return VoterInformation.getPollingLocations();
            case R.id.sort_early_vote:
                return VoterInformation.getEarlyVotingLocations();
            case R.id.sort_drop_boxes:
                return VoterInformation.getDropBoxLocations();
        }

        return new ArrayList<>();
    }

    @Override
    public Election getElection() {
        return VoterInformation.getElection();
    }

    @Override
    public void menuItemClicked(@LayoutRes int sortType) {
        switch (sortType) {
            case R.id.map_view:
                getView().navigateToMap(currentSort);
                break;
            case R.id.list_view:
                getView().navigateToList(currentSort);
            default:
                currentSort = sortType;
                getView().updateList(getSortedLocations());
        }

    }

    @Override
    public void itemClickedAtIndex(int index) {
        ArrayList<PollingLocation> sortedList = getSortedLocations();

        if (sortedList.size() > index) {
            getView().navigateToDirections(sortedList.get(index));
        } else {
            Log.e(TAG, "Cannot retrieve data for item selected: " + index);
        }
    }

    @Override
    public void onMapNeedsLayout(@NonNull GoogleMap map) {
        map.clear();

        addNonPollingLocationsToMap(map);

        mappedPollingLocations = new HashMap<>();

        ArrayList<PollingLocation> locations = getSortedLocations();

        for (PollingLocation location : locations) {
            MarkerOptions markerOptions;
            switch (location.pollingLocationType) {
                case PollingLocation.POLLING_TYPE_DROP_BOX:

                    markerOptions = createMarkerOptions(location, R.drawable.ic_marker_drop_box);

                    break;
                case PollingLocation.POLLING_TYPE_EARLY_VOTE:
                    markerOptions = createMarkerOptions(location, R.drawable.ic_marker_early_voting);

                    break;
                case PollingLocation.POLLING_TYPE_LOCATION:
                default:
                    markerOptions = createMarkerOptions(location, R.drawable.ic_marker_poll);

                    break;
            }

            if (location.address != null) {
                Marker marker = map.addMarker(markerOptions);

                mappedPollingLocations.put(marker, location);
            }
        }
    }

    /**
     * Helper to create a map Marker with drawable
     *
     * @param location
     * @param drawable
     * @return
     */
    private MarkerOptions createMarkerOptions(PollingLocation location, @DrawableRes int drawable) {
        return new MarkerOptions()
                .position(location.getLatLongLocation())
                .title(location.address.locationName)
                .icon(BitmapDescriptorFactory.fromResource(drawable));
    }

    /**
     * Helper function to add everything that isn't a polling site to the map
     */
    private void addNonPollingLocationsToMap(@NonNull GoogleMap map) {
        // add marker for user-entered address
        LatLng homeLatLong = VoterInformation.getHomeAddress().getLocation();

        if (homeLatLong.latitude != 0.0) {
            map.addMarker(new MarkerOptions()
                    .position(VoterInformation.getHomeAddress().getLocation())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_address))
            );
        }

        //TODO fix current location
//        if (currentLocation != null) {
//            // add marker for current user location (used for directions)
//            Marker marker = map.addMarker(new MarkerOptions()
//                    .position(currentLocation)
//                    .title(getContext().getString(R.string.locations_map_label_user_location))
//                    .snippet(currentAddress)
//                    .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation))
//            );
//        }


        //TODO fix admin body location + poly lines
//        if (haveElectionAdminBody) {
//            // add marker for state or local election administration body
//            Marker marker = map.addMarker(new MarkerOptions()
//                    .position(thisLocation)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_leg_body_map))
//            );
//
//            marker.showInfoWindow();
//            // allow for getting directions from election admin body location
//        }
//
//        if (encodedPolyline != null && !encodedPolyline.isEmpty()) {
//            // show directions line on map
//            PolylineOptions polylineOptions = new PolylineOptions();
//            List<LatLng> pts = PolyUtil.decode(encodedPolyline);
//            polylineOptions.addAll(pts);
//            polylineOptions.color(getContext().getResources().getColor(R.color.brand));
//            map.addPolyline(polylineOptions);
//        }
    }

    @Override
    public boolean mapMarkerClicked(GoogleMap map, Marker marker) {
        //Pull Polling Location from Hashmap
        PollingLocation location = mappedPollingLocations.get(marker);
        lastClickedLocation = location;

        if (location != null) {
            getView().showLocationCard(location);

            return true;
        }

        return false;
    }

    @Override
    public void lastPollingLocationClicked() {
        if (lastClickedLocation != null) {
            getView().navigateToDirections(lastClickedLocation);
        }
    }

    @Override
    public int getCurrentSort() {
        return currentSort;
    }

    @Override
    public boolean hasPollingLocations() {
        return !VoterInformation.getPollingLocations().isEmpty();
    }

    @Override
    public boolean hasEarlyVotingLocations() {
        return !VoterInformation.getEarlyVotingLocations().isEmpty();
    }

    @Override
    public boolean hasDropBoxLocations() {
        return !VoterInformation.getDropBoxLocations().isEmpty();
    }

    @Override
    public void onCreate(Bundle savedState) {
        //Not implemented
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        //Not implemented
    }

    @Override
    public void onDestroy() {
        //Not implemented
    }
}
