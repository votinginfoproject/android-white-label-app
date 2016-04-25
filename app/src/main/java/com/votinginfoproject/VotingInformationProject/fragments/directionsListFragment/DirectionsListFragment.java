package com.votinginfoproject.VotingInformationProject.fragments.directionsListFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;

/**
 * Created by max on 4/22/16.
 */
public class DirectionsListFragment extends Fragment implements DirectionsListView {
    private static final String TAG = DirectionsListFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private DirectionsListViewPresenter mPresenter;
    private DirectionsRecyclerViewAdapter mAdapter;

    private static final String ARG_ROUTE = "route";
    private static final String ARG_TRANSIT_MODE = "transit_mode";
    private static final String ARG_ORIGIN_COORDINATES = "origin_coordinates";
    private static final String ARG_DESTINATION_COORDINATES = "destination_coordinates";

//    public static DirectionsListFragment newInstance(String transitMode, String originCoordinates, String destinationCoordinates) {
//        DirectionsListFragment fragment = new DirectionsListFragment();
//
//        Bundle args = new Bundle();
//        args.putString(ARG_TRANSIT_MODE, transitMode);
//        args.putString(ARG_ORIGIN_COORDINATES, originCoordinates);
//        args.putString(ARG_DESTINATION_COORDINATES, destinationCoordinates);
//
//        fragment.setArguments(args);
//
//        return  fragment;
//    }

    public static DirectionsListFragment newInstance(Route route) {
        DirectionsListFragment fragment = new DirectionsListFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_ROUTE, route);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_directions_list, container, false);

        Bundle args = getArguments();
        Route route = args.getParcelable(ARG_ROUTE);
        //String transitMode = args.getString(ARG_TRANSIT_MODE);
        //String originCoordinates = args.getString(ARG_ORIGIN_COORDINATES);
        //String destinationCoordinates = args.getString(ARG_DESTINATION_COORDINATES);

        mPresenter = new DirectionsListViewPresenterImpl(rootView.getContext(), route);
        //mPresenter = new DirectionsListViewPresenterImpl(rootView.getContext(), transitMode, originCoordinates, destinationCoordinates);
        mPresenter.setView(this);

        mAdapter = new DirectionsRecyclerViewAdapter(mPresenter);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void refresh() {
        mAdapter.notifyDataSetChanged();
    }
}
