package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;

/**
 * Created by marcvandehey on 4/12/16.
 */
public class PollingSiteViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
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
        mSiteType = (TextView) view.findViewById(R.id.text_view_polling_site_type);
        mSiteTitle = (TextView) view.findViewById(R.id.site_title);
        mSiteAddress = (TextView) view.findViewById(R.id.site_address);
        mSiteHours = (TextView) view.findViewById(R.id.site_hours);
    }

    public void setPollingLocation(Context context, PollingLocation pollingLocation) {
        mLocation = pollingLocation;

        Drawable dot = ContextCompat.getDrawable(context, mLocation.getDrawableDot());

        String siteType = context.getText(mLocation.getPollingTypeString()).toString();
        mSiteType.setText(siteType.toUpperCase());
        mSiteType.setCompoundDrawablesWithIntrinsicBounds(dot, null, null, null);
        mSiteTitle.setText(mLocation.address.locationName);
        mSiteAddress.setText(mLocation.address.line1);
        mSiteHours.setText(mLocation.pollingHours);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mSiteTitle.getText() + "'";
    }
}