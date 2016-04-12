package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;

/**
 * Created by marcvandehey on 4/12/16.
 */
public class PollingSiteViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final ImageView mColorKeyImageView;
    private final TextView mSiteType;
    private final TextView mSiteTitle;
    private final TextView mSiteAddress;
    private final TextView mSiteHours;

    public PollingLocation mLocation;

    /**
     * View Holder for an Polling Location Item. Expects to use the row_polling_site layout
     */
    public PollingSiteViewHolder(View view) {
        super(view);

        mView = view;
        mColorKeyImageView = (ImageView) view.findViewById(R.id.color_key);
        mSiteType = (TextView) view.findViewById(R.id.site_type);
        mSiteTitle = (TextView) view.findViewById(R.id.site_title);
        mSiteAddress = (TextView) view.findViewById(R.id.site_address);
        mSiteHours = (TextView) view.findViewById(R.id.site_hours);
    }

    public void setPollingLocation(PollingLocation pollingLocation) {
        mLocation = pollingLocation;

        mColorKeyImageView.setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY);

        mSiteType.setText("Site type here");
        mSiteTitle.setText(mLocation.address.locationName);
        mSiteAddress.setText(mLocation.address.line1);
        mSiteHours.setText(mLocation.pollingHours);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mSiteTitle.getText() + "'";
    }
}