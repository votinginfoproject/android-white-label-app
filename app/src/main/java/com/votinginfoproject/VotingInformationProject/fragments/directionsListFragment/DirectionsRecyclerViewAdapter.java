package com.votinginfoproject.VotingInformationProject.fragments.directionsListFragment;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by max on 4/22/16.
 */
public class DirectionsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private DirectionsListViewPresenter mPresenter;

    public DirectionsRecyclerViewAdapter(DirectionsListViewPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
