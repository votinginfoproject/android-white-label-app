package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.contestInformationFragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Candidate;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.CandidateViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ContestDetailViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionInformationViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ReportErrorViewHolder;

/**
 * Created by marcvandehey on 4/21/16.
 */
public class ContestInformationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ContestInformationItemOnClickListener mListener;
    private ContestInformationPresenter mPresenter;

    public ContestInformationRecyclerViewAdapter(ContestInformationPresenter presenter, ContestInformationItemOnClickListener listener) {
        mPresenter = presenter;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case ContestInformationPresenter.ELECTION_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_election_header, parent, false);

                return new ElectionInformationViewHolder(view);
            case ContestInformationPresenter.CANDIDATE_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_blue_two_line, parent, false);

                return new CandidateViewHolder(view);
            case ContestInformationPresenter.CONTEST_DETAIL_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contest_detail, parent, false);

                return new ContestDetailViewHolder(view);
            case ContestInformationPresenter.REPORT_VIEW_HOLDER:
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_report_error, parent, false);

                return new ReportErrorViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CandidateViewHolder) {
            CandidateViewHolder candidateViewHolder = (CandidateViewHolder) holder;

            final Candidate candidate = mPresenter.getCandidate(position);

            candidateViewHolder.setCandidate(candidate, mPresenter.getSectionTitleForIndex(position), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onCandidateClicked(candidate);
                }
            });
        } else if(holder instanceof ContestDetailViewHolder) {

        } else if (holder instanceof ElectionInformationViewHolder) {
            ElectionInformationViewHolder electionInformationViewHolder = (ElectionInformationViewHolder) holder;
            electionInformationViewHolder.setElection(mPresenter.getElection());
        } else if (holder instanceof ReportErrorViewHolder) {
            ReportErrorViewHolder errorViewHolder = (ReportErrorViewHolder) holder;
            errorViewHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onReportErrorClicked();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mPresenter.getViewTypeForIndex(position);
    }

    @Override
    public int getItemCount() {
        return mPresenter.getRowCount();
    }

    public interface ContestInformationItemOnClickListener {
        void onCandidateClicked(Candidate candidate);

        void onReportErrorClicked();
    }
}