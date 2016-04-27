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
import com.votinginfoproject.VotingInformationProject.views.viewHolders.DividerItemDecoration;

/**
 * Created by max on 4/22/16.
 */
public class DirectionsListFragment extends Fragment implements DirectionsListView {
    private static final String TAG = DirectionsListFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private DirectionsListViewPresenter mPresenter;
    private DirectionsRecyclerViewAdapter mAdapter;

    private static final String ARG_ROUTE = "route";

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

        mPresenter = new DirectionsListViewPresenterImpl(route);
        mPresenter.setView(this);

        mAdapter = new DirectionsRecyclerViewAdapter(mPresenter);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), DividerItemDecoration.VERTICAL_LIST));

        return rootView;
    }

    @Override
    public void refreshViewData() {
        mAdapter.notifyDataSetChanged();
    }
}
