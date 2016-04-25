package com.votinginfoproject.VotingInformationProject.fragments.directionsListFragment;

import android.graphics.Path;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.DirectionsStepViewHolder;

/**
 * Created by max on 4/22/16.
 */
public class DirectionsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private DirectionsListViewPresenter mPresenter;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

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
            //viewHolder.mTextView.setText(stripHtml(mPresenter.getSteps().get(position).html_instructions));
        }
    }

    @Override
    public int getItemCount() {
        return mPresenter.getSteps().size();
    }

    private String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }
}
