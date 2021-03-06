package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.ballotFragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ContestViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.HeaderViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ReportErrorViewHolder;

public class ContestListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ContestListPresenter mPresenter;

    public ContestListRecyclerViewAdapter(ContestListPresenter presenter) {
        mPresenter = presenter;
    }

    public void setPresenter(ContestListPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case ContestListPresenter.ELECTION_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_election_header, parent, false);

                return new HeaderViewHolder(view);
            case ContestListPresenter.CONTEST_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_blue_two_line, parent, false);

                return new ContestViewHolder(view);
            case ContestListPresenter.REPORT_VIEW_HOLDER:
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_report_error, parent, false);

                return new ReportErrorViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContestViewHolder) {
            ContestViewHolder contestViewHolder = (ContestViewHolder) holder;

            final Contest contest = mPresenter.getContest(position);

            String electionName = "";
            if (mPresenter.getElection() != null) {
                electionName = mPresenter.getElection().getName();
            }

            contestViewHolder.setContest(contest, electionName, mPresenter.getSectionTitleForIndex(position), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.onContestItemClicked(contest);
                }
            });
        } else if (holder instanceof HeaderViewHolder && mPresenter.getElection() != null) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.setData(mPresenter.getElection().getName(), mPresenter.getElection().getFormattedDate());
        } else if (holder instanceof ReportErrorViewHolder) {
            ReportErrorViewHolder errorViewHolder = (ReportErrorViewHolder) holder;
            errorViewHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.onReportErrorClicked();
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
        return mPresenter.getContestCount();
    }
}
