package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;

/**
 * Created by marcvandehey on 4/12/16.
 */
public class HeaderViewHolder extends RecyclerView.ViewHolder {

    private final TextView mTitle;
    private final TextView mDescription;

    /**
     * View Holder for an Election Item. Expects to use the row_election_header layout
     */
    public HeaderViewHolder(View itemView) {
        super(itemView);

        mTitle = (TextView) itemView.findViewById(R.id.text_view_title);
        mDescription = (TextView) itemView.findViewById(R.id.text_view_description);
    }

    public void setData(String title, String description) {
        mTitle.setText(title);
        mDescription.setText(description);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mTitle.getText() + " " + mDescription.getText() + "'";
    }
}
