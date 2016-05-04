package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.contestInformationFragment.ContestInformationPresenter;

/**
 * Created by marcvandehey on 4/28/16.
 */
public class ContestDetailViewHolder extends RecyclerView.ViewHolder implements DecoratedViewHolder {
    private TextView mTitle;
    private TextView mDescription;
    private TextView mSectionTitle;

    public ContestDetailViewHolder(View itemView) {
        super(itemView);

        mTitle = (TextView) itemView.findViewById(R.id.text_view_title);
        mDescription = (TextView) itemView.findViewById(R.id.text_view_description);
        mSectionTitle = (TextView) itemView.findViewById(R.id.text_view_section);
    }

    /**
     * Setup detail information for the row
     *
     * @param dataHolder
     * @param section
     * @param listener
     */
    public void setData(ContestInformationPresenter.DataHolder dataHolder, @Nullable String section) {
        if (dataHolder != null) {
            if (section != null && !section.isEmpty()) {
                mSectionTitle.setText(section);
                mSectionTitle.setVisibility(View.VISIBLE);
            } else {
                mSectionTitle.setVisibility(View.GONE);
            }

            mDescription.setText(dataHolder.description);
            mTitle.setText(dataHolder.title);
        }
    }

    @Override
    public boolean shouldShowItemDecoration() {
        return mSectionTitle.getVisibility() == View.VISIBLE;
    }
}
