package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Candidate;

/**
 * Created by marcvandehey on 4/28/16.
 */
public class CandidateViewHolder extends RecyclerView.ViewHolder implements DecoratedViewHolder {
    private View mClickableView;
    private TextView mTitle;
    private TextView mDescription;
    private TextView mSectionTitle;

    public CandidateViewHolder(View itemView) {
        super(itemView);

        mClickableView = itemView.findViewById(R.id.clickable_view);
        mTitle = (TextView) itemView.findViewById(R.id.text_view_title);
        mDescription = (TextView) itemView.findViewById(R.id.text_view_description);
        mSectionTitle = (TextView) itemView.findViewById(R.id.text_view_section);
    }

    /**
     * Setup candidate information for the row
     *
     * @param candidate
     * @param section
     * @param listener
     */
    public void setCandidate(Candidate candidate, @Nullable String section, View.OnClickListener listener) {
        if (candidate != null) {
            if (section != null && !section.isEmpty()) {
                mSectionTitle.setText(section);
                mSectionTitle.setVisibility(View.VISIBLE);
            } else {
                mSectionTitle.setVisibility(View.GONE);
            }

            mDescription.setText(candidate.party);
            mTitle.setText(candidate.name);

            mClickableView.setOnClickListener(listener);
        }
    }

    @Override
    public boolean shouldShowItemDecoration() {
        return mSectionTitle.getVisibility() == View.VISIBLE;
    }
}
