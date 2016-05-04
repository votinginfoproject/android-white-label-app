package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.candidateInformationFragment.CandidateInformationPresenter;

/**
 * Created by marcvandehey on 5/3/16.
 */
public class CandidateDetailViewHolder extends RecyclerView.ViewHolder implements DecoratedViewHolder {
    private ImageView imageView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView sectionTextView;
    private View clickableView;

    public CandidateDetailViewHolder(View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.image_view);
        titleTextView = (TextView) itemView.findViewById(R.id.text_view_title);
        descriptionTextView = (TextView) itemView.findViewById(R.id.text_view_description);
        sectionTextView = (TextView) itemView.findViewById(R.id.text_view_section);
        clickableView = itemView.findViewById(R.id.clickable_view);
    }

    /**
     * Bind the data for the Candidate Detail View Holder
     *
     * @param context
     * @param drawable
     * @param title
     * @param description
     */
    public void bindData(Context context, CandidateInformationPresenter.DataHolder dataHolder) {
        if (dataHolder == null) {
            return;
        }

        if (context != null) {
            imageView.setImageDrawable(ContextCompat.getDrawable(context, dataHolder.drawable));
        }

        itemView.setContentDescription(dataHolder.accessibilityText);

        titleTextView.setText(dataHolder.title);

        if (dataHolder.description == null) {
            descriptionTextView.setVisibility(View.GONE);
        } else {
            descriptionTextView.setVisibility(View.VISIBLE);
            descriptionTextView.setText(dataHolder.description);
        }

        if (dataHolder.sectionText == null) {
            sectionTextView.setVisibility(View.GONE);
        } else {
            sectionTextView.setVisibility(View.VISIBLE);
            sectionTextView.setText(dataHolder.sectionText);
        }

        clickableView.setClickable(true);
        clickableView.setOnClickListener(dataHolder.listener);
    }

    @Override
    public boolean shouldShowItemDecoration() {
        return sectionTextView.getVisibility() == View.VISIBLE;
    }
}