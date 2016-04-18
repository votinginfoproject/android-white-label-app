package com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionInformationViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ReportErrorViewHolder;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 4/15/16.
 */
public class ElectionDetailsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ELECTION_VIEW_HOLDER = 0x0;
    private static final int REPORT_ERROR_VIEW_HOLDER = 0x1;

    private final Context mContext;
    private boolean hasHeader;

    private final VoterInfo mVoterInfo;
    private final Election mElection;
    private final ElectionAdministrationBody mLocalAdmin;
    private final ElectionAdministrationBody mStateAdmin;

    public ElectionDetailsRecyclerViewAdapter(Context context, VoterInfo voterInfo) {
        mContext = context;
        mVoterInfo = voterInfo;
        mElection = mVoterInfo.election;
        mLocalAdmin = mVoterInfo.getLocalAdmin();
        mStateAdmin = mVoterInfo.getStateAdmin();

        hasHeader = (mElection != null);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;

        switch(viewType) {
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
            default:
                viewHolder = null;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ElectionInformationViewHolder) {
            ElectionInformationViewHolder electionInformationViewHolder = (ElectionInformationViewHolder) holder;
            electionInformationViewHolder.setElection(mElection);
        } else if (holder instanceof ReportErrorViewHolder) {
            ReportErrorViewHolder errorViewHolder = (ReportErrorViewHolder) holder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && hasHeader) {
            return ELECTION_VIEW_HOLDER;
        } else {
            return REPORT_ERROR_VIEW_HOLDER;
        }
    }


    @Override
    public int getItemCount() {
        return 1 + (hasHeader ? 1 : 0);
    }

    public List<ParentListNode> getListNodesForBody(ElectionAdministrationBody body, String sectionName) {
        List<ParentListNode> toReturn = new ArrayList<>();

        ParentListNode sectionTitleNode = new ParentListNode();
        sectionTitleNode.isSectionTitle = true;
        sectionTitleNode.mText = sectionName;
        toReturn.add(sectionTitleNode);

        return toReturn;
    }

}

class ParentListNode {
    public String mText = "";
    public int mImageId = 0;
    public boolean isSectionTitle = false;
    public ArrayList<ChildListNode> mChildListNodes = new ArrayList<>();
}

class ChildListNode {
    public String mText;
    public String mURL;
}

