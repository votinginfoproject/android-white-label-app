package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by marcvandehey on 4/12/16.
 */
public class ReportErrorViewHolder extends RecyclerView.ViewHolder {
    public View mView;

    /**
     * View Holder for the generic Report Error Row. Expects to use the row_report_error layout
     */
    public ReportErrorViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
    }
}
