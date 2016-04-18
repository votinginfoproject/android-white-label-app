package com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.ElectionOfficial;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionBodyLinkViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionBodySubtitleViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionBodyTextViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionBodyTitleViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionInformationViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ReportErrorViewHolder;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by max on 4/15/16.
 */
public class ElectionDetailsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ELECTION_VIEW_HOLDER = 0x0;
    private static final int REPORT_ERROR_VIEW_HOLDER = 0x1;
    private static final int BODY_TITLE_VIEW_HOLDER = 0x2;
    private static final int BODY_PARENT_VIEW_HOLDER = 0x4;
    private static final int BODY_CHILD_TEXT_VIEW_HOLDER = 0x5;
    private static final int BODY_CHILD_LINK_VIEW_HOLDER = 0x6;

    private final Context mContext;
    private boolean hasHeader;

    private final VoterInfo mVoterInfo;
    private final Election mElection;
    private final ElectionAdministrationBody mLocalAdmin;
    private final ElectionAdministrationBody mStateAdmin;

    private List<ListItem> parentListNodes;

    public ElectionDetailsRecyclerViewAdapter(Context context, VoterInfo voterInfo) {
        mContext = context;
        mVoterInfo = voterInfo;
        mElection = mVoterInfo.election;
        mLocalAdmin = mVoterInfo.getLocalAdmin();
        mStateAdmin = mVoterInfo.getStateAdmin();

        parentListNodes = getListNodesForBody(mLocalAdmin, "Local");
        parentListNodes.addAll(getListNodesForBody(mStateAdmin, "State"));

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
            case BODY_TITLE_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_election_body_title, parent, false);
                viewHolder = new ElectionBodyTitleViewHolder(view);
                break;
            case BODY_PARENT_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_election_body_subtitle, parent, false);
                viewHolder = new ElectionBodySubtitleViewHolder(view);
                break;
            case BODY_CHILD_LINK_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_election_body_link, parent, false);
                viewHolder = new ElectionBodyLinkViewHolder(view);
                break;
            case BODY_CHILD_TEXT_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_election_body_text, parent, false);
                viewHolder = new ElectionBodyTextViewHolder(view);
                break;
            default:
                viewHolder = null;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ElectionInformationViewHolder) {
            ElectionInformationViewHolder electionInformationViewHolder = (ElectionInformationViewHolder) holder;
            electionInformationViewHolder.setElection(mElection);
        } else if (holder instanceof ReportErrorViewHolder) {
            ReportErrorViewHolder errorViewHolder = (ReportErrorViewHolder) holder;
        } else if (holder instanceof ElectionBodySubtitleViewHolder) {

            final ListItem item = itemForListPosition(position);
            ElectionBodySubtitleViewHolder viewHolder = (ElectionBodySubtitleViewHolder) holder;

            viewHolder.setTitle(item.mText);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClicked(item);
                }
            });

        } else if (holder instanceof  ElectionBodyTitleViewHolder) {

            ListItem item = itemForListPosition(position);
            ElectionBodyTitleViewHolder viewHolder = (ElectionBodyTitleViewHolder) holder;
            viewHolder.setTitle(item.mText);

        } else if (holder instanceof  ElectionBodyLinkViewHolder) {

            ListItem item = itemForListPosition(position);
            ElectionBodyLinkViewHolder viewHolder = (ElectionBodyLinkViewHolder) holder;
            viewHolder.setTitle(item.mText);

        } else if (holder instanceof  ElectionBodyTextViewHolder) {

            ListItem item = itemForListPosition(position);
            ElectionBodyTextViewHolder viewHolder = (ElectionBodyTextViewHolder) holder;
            viewHolder.setTitle(item.mText);
        }
    }

    private ListItem itemForListPosition(int position) {
        if (hasHeader) {
            position--;
        }

        if (position < 0 || position >= parentListNodes.size()) {
            return null;
        }

        return parentListNodes.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && hasHeader) {
            return ELECTION_VIEW_HOLDER;
        } else {
            if (hasHeader) {
                position--;
            }
            if (position < parentListNodes.size()) {
                return parentListNodes.get(position).mViewType;
            }
        }
        return REPORT_ERROR_VIEW_HOLDER;
    }


    @Override
    public int getItemCount() {
        return 1 + (hasHeader ? 1 : 0) + parentListNodes.size();
    }

    public List<ListItem> getListNodesForBody(ElectionAdministrationBody body, String sectionName) {
        List<ListItem> toReturn = new ArrayList<>();

        if (body != null) {
            ListItem sectionTitleNode = new ListItem(BODY_TITLE_VIEW_HOLDER, sectionName);
            toReturn.add(sectionTitleNode);

            ListItem websitesParent = new ListItem(BODY_PARENT_VIEW_HOLDER, mContext.getString(R.string.details_section_header_links));
            toReturn.add(websitesParent);

            ListItem[] children = {
                    new ListItem(BODY_CHILD_LINK_VIEW_HOLDER, mContext.getString(R.string.details_label_voter_registration_url)),
                    new ListItem(BODY_CHILD_LINK_VIEW_HOLDER, mContext.getString(R.string.details_label_absentee_voting_url)),
                    new ListItem(BODY_CHILD_LINK_VIEW_HOLDER, mContext.getString(R.string.details_label_voting_location_finder_url)),
                    new ListItem(BODY_CHILD_LINK_VIEW_HOLDER, mContext.getString(R.string.details_label_election_rules_url))
            };
            websitesParent.mHiddenListItems.addAll(Arrays.asList(children));

            ListItem hoursParent = new ListItem(BODY_PARENT_VIEW_HOLDER, mContext.getString(R.string.details_label_hours_of_operation));
            toReturn.add(hoursParent);
            hoursParent.mHiddenListItems.add(new ListItem(BODY_CHILD_TEXT_VIEW_HOLDER, body.hoursOfOperation));

            ListItem addressParent = new ListItem(BODY_PARENT_VIEW_HOLDER, mContext.getString(R.string.details_label_physical_address));
            toReturn.add(addressParent);
            addressParent.mHiddenListItems.add(new ListItem(BODY_CHILD_TEXT_VIEW_HOLDER, body.physicalAddress.toString()));

            ListItem officialsParent = new ListItem(BODY_PARENT_VIEW_HOLDER, mContext.getString(R.string.details_label_election_officials));
            toReturn.add(officialsParent);
            for (ElectionOfficial official : body.electionOfficials) {
                officialsParent.mHiddenListItems.add(new ListItem(BODY_CHILD_TEXT_VIEW_HOLDER, official.name));
            }
        }

        return toReturn;
    }

    private void itemClicked(ListItem item) {
        if (!item.isChild()) {
            if (item.isExpanded) {
                collapseListItem(item);
            } else {
                expandListItem(item);
            }
        }
    }

    private void expandListItem(ListItem item) {
        int position = parentListNodes.indexOf(item);
        int index = position + 1;
        for (ListItem child : item.mHiddenListItems) {
            parentListNodes.add(index, child);
            index++;
        }

        item.isExpanded = true;

        notifyItemRangeChanged(position + 1, item.mHiddenListItems.size());
    }

    private void collapseListItem(ListItem item) {
        int position = parentListNodes.indexOf(item);
        while (parentListNodes.size() > position + 1 && parentListNodes.get(position + 1).isChild()) {
            parentListNodes.remove(position + 1);
        }

        item.isExpanded = false;

        notifyItemRangeChanged(position + 1, item.mHiddenListItems.size());
    }

    class ListItem {
        public final int mViewType;
        public String mText;
        public int mImageId = 0;
        public boolean isExpanded = false;
        public List<ListItem> mHiddenListItems = new ArrayList<>();

        public ListItem(int viewType, String text) {
            mViewType = viewType;
            mText= text;
        }

        public boolean isChild() {
            return (mViewType == BODY_CHILD_LINK_VIEW_HOLDER)
                    || (mViewType == BODY_CHILD_TEXT_VIEW_HOLDER);
        }

    }
}
