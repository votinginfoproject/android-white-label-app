package com.votinginfoproject.VotingInformationProject.fragments.directionsListFragment;

import android.content.Context;
import android.util.Log;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;
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

    private Context mContext;

    private DirectionsInteractor mDirectionsInteractor;

    private String mTransitMode;
    private String mOriginCoordinates;
    private String mDestinationCoordinates;

    private List<Route> mRoutes = new ArrayList<>();

    public DirectionsListViewPresenterImpl(Context context, String transitMode, String originCoordinates, String destinationCoordinates) {
        mContext = context;

        mTransitMode = transitMode;
        mOriginCoordinates = originCoordinates;
        mDestinationCoordinates = destinationCoordinates;

        mDirectionsInteractor = new DirectionsInteractor();

        requestDirections();
    }

    private void requestDirections() {
        String directionsKey = mContext.getString(R.string.google_api_browser_key);
        DirectionsRequest request = new DirectionsRequest(directionsKey, mTransitMode, mOriginCoordinates, mDestinationCoordinates);

        mDirectionsInteractor.enqueueRequest(request, this);
    }

    @Override
    public void directionsResponse(DirectionsResponse response) {
        mRoutes = response.routes;
        if (mRoutes.size() > 0) {
            Log.e(TAG, mTransitMode + " " + mRoutes.get(0).summary);
        }
    }
}
