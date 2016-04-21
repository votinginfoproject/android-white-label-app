package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;

/**
 * Created by max on 4/18/16.
 */
public class ElectionBodyTextViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final TextView mTextView;

    public ElectionBodyTextViewHolder(View v) {
        super(v);

        mView = v;
        mTextView = (TextView) mView.findViewById(R.id.body_text);
    }

    public void setTitle(String title) {
        mTextView.setText(title);
    }
}