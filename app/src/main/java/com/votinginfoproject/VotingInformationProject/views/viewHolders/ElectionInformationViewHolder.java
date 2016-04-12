package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Election;

/**
 * Created by marcvandehey on 4/12/16.
 */
public class ElectionInformationViewHolder extends RecyclerView.ViewHolder {

    private final View mView;
    private final TextView mTitle;
    private final TextView mDescription;

    private Election mElection;

    /**
     * View Holder for an Election Item. Expects to use the row_election_header layout
     */
    public ElectionInformationViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        mTitle = (TextView) itemView.findViewById(R.id.text_view_title);
        mDescription = (TextView) itemView.findViewById(R.id.text_view_description);
    }

    public void setElection(Election election) {
        mElection = election;

        mTitle.setText(election.getName());
        mDescription.setText(election.getFormattedDate());
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mTitle.getText() + " " + mDescription.getText() + "'";
    }
}
