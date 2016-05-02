package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;

/**
 * Created by marcvandehey on 4/29/16.
 */
public class ReferendumItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView mTextView;

    /**
     * View Holder for an Referendum Item. Expects to use the row_referendum_item layout
     */
    public ReferendumItemViewHolder(View itemView) {
        super(itemView);

        mTextView = (TextView) itemView.findViewById(R.id.text_view);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mTextView.getText() + "'";
    }
}
