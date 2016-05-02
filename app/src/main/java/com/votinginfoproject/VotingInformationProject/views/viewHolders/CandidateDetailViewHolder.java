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
public class CandidateDetailViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private TextView titleTextView;
    private TextView descriptionTextView;

    public CandidateDetailViewHolder(View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.image_view);
        titleTextView = (TextView) itemView.findViewById(R.id.text_view_title);
        descriptionTextView = (TextView) itemView.findViewById(R.id.text_view_description);
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
        imageView.setImageDrawable(ContextCompat.getDrawable(context, dataHolder.drawable));
        titleTextView.setText(dataHolder.title);
        descriptionTextView.setText(dataHolder.description);

        itemView.setOnClickListener(dataHolder.listener);
    }
}