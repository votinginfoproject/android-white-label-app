package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionInformationViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.PollingSiteViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ReportErrorViewHolder;

import java.util.ArrayList;
import java.util.List;

public class PollingSiteItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ELECTION_VIEW_HOLDER = 0x0;
    private static final int POLLING_LOCATION_VIEW_HOLDER = 0x1;
    private static final int REPORT_ERROR_VIEW_HOLDER = 0x2;
    private final Election mElection;
    private final PollingSitesListFragment.PollingSitesListener mListener;
    private ArrayList<PollingLocation> mPollingLocations;
    private boolean hasHeader;

    private Context mContext;

    public PollingSiteItemRecyclerViewAdapter(Context context,
                                              Election election,
                                              List<PollingLocation> items,
                                              PollingSitesListFragment.PollingSitesListener listener) {
        mElection = election;
        mPollingLocations = new ArrayList<>(items);
        mListener = listener;
        mContext = context;
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

    public void updatePollingLocations(List<PollingLocation> newPollingLocations) {
        mPollingLocations.clear();
        mPollingLocations.addAll(newPollingLocations);

        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && hasHeader) {
            return ELECTION_VIEW_HOLDER;
        } else if (position < mPollingLocations.size() + (hasHeader ? 1 : 0)) {
            return POLLING_LOCATION_VIEW_HOLDER;
        } else {
            return REPORT_ERROR_VIEW_HOLDER;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PollingSiteViewHolder) {
            PollingSiteViewHolder pollingSiteViewHolder = (PollingSiteViewHolder) holder;
            final PollingLocation location = mPollingLocations.get(position - (hasHeader ? 1 : 0));

            pollingSiteViewHolder.setPollingLocation(mContext, location);

            pollingSiteViewHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.pollingSiteClicked(location);
                }
            });

        } else if (holder instanceof ElectionInformationViewHolder) {
            ElectionInformationViewHolder electionInformationViewHolder = (ElectionInformationViewHolder) holder;
            electionInformationViewHolder.setElection(mElection);
        } else if (holder instanceof ReportErrorViewHolder) {
            ReportErrorViewHolder errorViewHolder = (ReportErrorViewHolder) holder;
            errorViewHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.reportErrorClicked();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        //Add two here for the header and the footer
        return mPollingLocations.size() + (hasHeader ? 1 : 0) + 1;
    }
}
