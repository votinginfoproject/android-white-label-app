package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment.dummy.DummyContent.DummyItem;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionInformationViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.PollingSiteViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ReportErrorViewHolder;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PollingSiteItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ELECTION_VIEW_HOLDER = 0x0;
    private static final int POLLING_LOCATION_VIEW_HOLDER = 0x1;
    private static final int REPORT_ERROR_VIEW_HOLDER = 0x2;
    private final Election mElection;
    private final List<PollingLocation> mValues;
    private final PollingSitesFragment.OnListFragmentInteractionListener mListener;
    private boolean hasHeader;

    public PollingSiteItemRecyclerViewAdapter(Election election,
                                              List<PollingLocation> items,
                                              PollingSitesFragment.OnListFragmentInteractionListener listener) {
        mElection = election;
        mValues = items;
        mListener = listener;

        hasHeader = mElection != null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case ELECTION_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_election_header, parent, false);
                viewHolder = new ElectionInformationViewHolder(view);
                break;
            case REPORT_ERROR_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_report_error, parent, false);
                viewHolder = new ReportErrorViewHolder(view);
                break;
            case POLLING_LOCATION_VIEW_HOLDER:
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_polling_site, parent, false);
                viewHolder = new PollingSiteViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ELECTION_VIEW_HOLDER;
        } else if (position < mValues.size() + (hasHeader ? 1 : 0)) {
            return POLLING_LOCATION_VIEW_HOLDER;
        } else {
            return REPORT_ERROR_VIEW_HOLDER;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PollingSiteViewHolder) {
            PollingSiteViewHolder pollingSiteViewHolder = (PollingSiteViewHolder) holder;
            PollingLocation location = mValues.get(position - (hasHeader ? 1 : 0));

            pollingSiteViewHolder.setPollingLocation(location);

//            pollingSiteViewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (null != mListener) {
//                        // Notify the active callbacks interface (the activity, if the
//                        // fragment is attached to one) that an item has been selected.
////                    mListener.onListFragmentInteraction(holder.mItem);
//                    }
//                }
//            });
        } else if (holder instanceof ElectionInformationViewHolder) {
            ElectionInformationViewHolder electionInformationViewHolder = (ElectionInformationViewHolder) holder;
            electionInformationViewHolder.setElection(mElection);
        } else if (holder instanceof ReportErrorViewHolder) {
            ReportErrorViewHolder errorViewHolder = (ReportErrorViewHolder) holder;

        }
    }

    @Override
    public int getItemCount() {
        //Add two here for the header and the footer
        return mValues.size() + (hasHeader ? 1 : 0) + 1;
    }
}
