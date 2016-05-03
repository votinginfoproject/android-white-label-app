package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.votinginfoproject.VotingInformationProject.R;

/**
 * Created by marcvandehey on 5/3/16.
 */
public class CandidateHeaderViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private TextView titleTextView;
    private TextView descriptionTextView;

    public CandidateHeaderViewHolder(View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.image_view);
        titleTextView = (TextView) itemView.findViewById(R.id.text_view_title);
        descriptionTextView = (TextView) itemView.findViewById(R.id.text_view_description);
    }

    /**
     * Bind the data for the Header View Holder
     *
     * @param context
     * @param imageURL
     * @param title
     * @param description
     */
    public void bindData(Context context, String imageURL, String title, String description) {
        if (imageURL != null) {
            //Currently assumes only one Header View per Recycler view, don't reload the image if it is already visible
            if (imageView.getVisibility() != View.VISIBLE) {
                imageView.setVisibility(View.VISIBLE);
                Picasso.with(context).load(imageURL).into(imageView);
            }
        } else {
            imageView.setVisibility(View.GONE);
        }

        itemView.setContentDescription(title);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
    }
}
