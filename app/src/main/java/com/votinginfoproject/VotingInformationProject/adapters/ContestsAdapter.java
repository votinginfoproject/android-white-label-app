package com.votinginfoproject.VotingInformationProject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;
import com.votinginfoproject.VotingInformationProject.models.Contest;

import java.util.Comparator;
import java.util.List;

/**
 * Created by kathrynkillebrew on 8/4/14.
 */
public class ContestsAdapter extends ArrayAdapter<Contest> {

    VIPTabBarActivity myActivity;
    Comparator<Contest> contestComparator;
    String electionName;

    // View lookup cache.  Pattern from here:
    // https://github.com/thecodepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
    private static class ViewHolder {
        TextView title;
        TextView subTitle;
    }

    public void sortList() {
        sort(contestComparator);
    }

    /**
     *
     * @param context VIPTabBarActivity that owns the ballot view
     * @param contests list of contests to display
     * @param electionName voterInfo.election.name, to be used for non-referendum contest descriptions
     */
    public ContestsAdapter(VIPTabBarActivity context, List<Contest> contests, String electionName) {
        super(context, R.layout.contest_list_item, contests);
        this.myActivity = context;
        this.electionName = electionName;

        // sort contests by ballot placement
        contestComparator = new Comparator<Contest>() {
            @Override
            public int compare(Contest contest1, Contest contest2) {
                return Double.compare(contest1.ballotPlacement, contest2.ballotPlacement);
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contest contest = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.contest_list_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.contest_list_item_title);
            viewHolder.subTitle = (TextView) convertView.findViewById(R.id.contest_list_item_subtitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        if (contest.type.equals("Referendum")) {
            setTextView(viewHolder.title, contest.referendumTitle);
            setTextView(viewHolder.subTitle, contest.referendumSubtitle);

            // deal with huge referendum names by only showing first 20 characters
            if (contest.referendumTitle.length() > 20) {
                viewHolder.title.setText(contest.referendumTitle.substring(0, 20) + "...");
            }
        } else {
            setTextView(viewHolder.title, contest.office);
            setTextView(viewHolder.subTitle, electionName);
        }

        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * Helper function to set text in view, or hide view if text empty or missing
     * @param view TextView to put text into
     * @param text String to put in the TextView
     */
    private void setTextView(TextView view, String text) {
        if (text != null && !text.isEmpty()) {
            view.setText(text);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
