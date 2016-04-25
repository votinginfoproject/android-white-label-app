package com.votinginfoproject.VotingInformationProject.fragments.directionsListFragment;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        TextView textView = new TextView(parent.getContext());
        RecyclerView.ViewHolder holder = new ViewHolder(textView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.mTextView.setText(stripHtml(mPresenter.getSteps().get(position).html_instructions));
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
