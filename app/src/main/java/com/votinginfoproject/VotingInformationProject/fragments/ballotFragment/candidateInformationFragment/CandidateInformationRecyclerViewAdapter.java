package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.candidateInformationFragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Candidate;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.CandidateDetailViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.CandidateHeaderViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ReportErrorViewHolder;

/**
 * Created by marcvandehey on 5/2/16.
 */
public class CandidateInformationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private CandidateInformationPresenter mPresenter;

    public void setPresenter(CandidateInformationPresenter presenter) {
        mPresenter = presenter;

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case CandidateInformationPresenter.HEADER_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_candidate_header, parent, false);

                return new CandidateHeaderViewHolder(view);
            case CandidateInformationPresenter.CANDIDATE_INFO_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_candidate_detail_item, parent, false);

                return new CandidateDetailViewHolder(view);
            case CandidateInformationPresenter.REPORT_VIEW_HOLDER:
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_report_error, parent, false);

                return new ReportErrorViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CandidateHeaderViewHolder) {
            CandidateHeaderViewHolder candidateHeaderViewHolder = (CandidateHeaderViewHolder) holder;
            Candidate candidate = mPresenter.getCandidate();

            if (mPresenter.getView() != null) {
                candidateHeaderViewHolder.bindData(mPresenter.getView().getContext(), candidate.candidateUrl, candidate.name, candidate.party);
            }

        } else if (holder instanceof CandidateDetailViewHolder) {
            CandidateDetailViewHolder candidateDetailViewHolder = (CandidateDetailViewHolder) holder;

            CandidateInformationPresenter.DataHolder dataHolder = mPresenter.getDataForIndex(position);

            if (mPresenter.getView() != null) {
                candidateDetailViewHolder.bindData(mPresenter.getView().getContext(), dataHolder);
            }
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
        return mPresenter.getRowCount();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        mPresenter = null;
    }
}
