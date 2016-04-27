package com.votinginfoproject.VotingInformationProject.fragments.directionsListFragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.DirectionsStepViewHolder;

/**
 * Created by max on 4/22/16.
 */
public class DirectionsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = DirectionsRecyclerViewAdapter.class.getSimpleName();

    private DirectionsListViewPresenter mPresenter;

    public DirectionsRecyclerViewAdapter(DirectionsListViewPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_directions_step, parent, false);
        return new DirectionsStepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DirectionsStepViewHolder) {
            DirectionsStepViewHolder viewHolder = (DirectionsStepViewHolder) holder;
            viewHolder.setStep(mPresenter.getSteps().get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mPresenter.getSteps().size();
    }
}
