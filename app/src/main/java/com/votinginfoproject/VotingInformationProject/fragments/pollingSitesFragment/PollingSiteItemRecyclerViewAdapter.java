package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment.dummy.DummyContent.DummyItem;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PollingSiteItemRecyclerViewAdapter extends RecyclerView.Adapter<PollingSiteItemRecyclerViewAdapter.ViewHolder> {

    private final List<PollingLocation> mValues;
    private final PollingSitesFragment.OnListFragmentInteractionListener mListener;

    public PollingSiteItemRecyclerViewAdapter(List<PollingLocation> items, PollingSitesFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_polling_site, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PollingLocation location = mValues.get(position);

        holder.mItem = location;
        holder.mSiteType.setText("Site type here");
        holder.mSiteTitle.setText(location.address.locationName);
        holder.mSiteAddress.setText(location.address.line1);
        holder.mSiteHours.setText(location.pollingHours);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mColorKeyImageView;
        public final TextView mSiteType;
        public final TextView mSiteTitle;
        public final TextView mSiteAddress;
        public final TextView mSiteHours;

        public PollingLocation mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mColorKeyImageView = (ImageView) view.findViewById(R.id.color_key);
            mSiteType = (TextView) view.findViewById(R.id.site_type);
            mSiteTitle = (TextView) view.findViewById(R.id.site_title);
            mSiteAddress = (TextView) view.findViewById(R.id.site_address);
            mSiteHours = (TextView) view.findViewById(R.id.site_hours);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mSiteTitle.getText() + "'";
        }
    }
}
