package com.votinginfoproject.VotingInformationProject.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;
import com.votinginfoproject.VotingInformationProject.adapters.CandidatesAdapter;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;


public class ContestFragment extends Fragment {
    private static final String CONTEST_NUM = "contest_number";
    private int contestNum;
    VoterInfo voterInfo;
    Contest contest;
    private ViewGroup mContainer;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contest_number Index of this contest within the list of contests on VoterInfo object
     * @return A new instance of fragment ContestFragment.
     */
    public static ContestFragment newInstance(int contest_number) {
        ContestFragment fragment = new ContestFragment();
        Bundle args = new Bundle();
        args.putInt(CONTEST_NUM, contest_number);
        fragment.setArguments(args);
        return fragment;
    }
    public ContestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            contestNum = getArguments().getInt(CONTEST_NUM);
            Log.d("ContestFragment", "Got contest #" + contestNum);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("ContestFragment", "In onActivityCreated");
        setContents();
    }

    /** Helper function to populate the view labels.
    *
    */
    private void setContents() {
        final VIPTabBarActivity myActivity = (VIPTabBarActivity)getActivity();
        TextView title = (TextView)myActivity.findViewById(R.id.contest_title);
        TextView subtitle = (TextView)myActivity.findViewById(R.id.contest_subtitle);

        try {
            voterInfo = ((VIPTabBarActivity) myActivity).getVoterInfo();
            contest = voterInfo.getContestAt(contestNum);
            Log.d("ContestFragment", "Got contest for office: " + contest.office);

            // title / subtitle is referendumTitle and referendumSubtitle, if election is
            // of type 'Referendum'; else title is office and subtitle is election name
            if (!contest.type.equals("Referendum")) {
                title.setText(contest.office);
                subtitle.setText(voterInfo.election.name);
            } else if (contest.type != null) {
                title.setText(contest.referendumTitle);

                if (contest.referendumSubtitle != null && !contest.referendumSubtitle.isEmpty()) {
                    subtitle.setText(contest.referendumSubtitle);
                } else {
                    subtitle.setVisibility(View.GONE);
                }

                // deal with huge referendum descriptions by reducing font size.
                // Cannot make fragment a ScrollView, because it already contains a
                // scrolling list of candidates.
                if (contest.referendumTitle.length() > 20) {
                    title.setTextSize(18);
                }

                if (contest.referendumSubtitle.length() > 20) {
                    subtitle.setTextSize(16);
                }
            }

            // populate candidate list
            if (contest.candidates != null && !contest.candidates.isEmpty()) {
                CandidatesAdapter adapter = new CandidatesAdapter(myActivity, contest.candidates);
                ListView candidateList = (ListView) myActivity.findViewById(R.id.contest_candidate_list);
                candidateList.setAdapter(adapter);
                candidateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("CandidateList", "clicked: " + contest.candidates.get(position).name);
                        myActivity.showCandidateDetails(contestNum, position);
                    }
                });
            } else {
                Log.d("ContestFragment", "No candidates found for selected contest.");
                myActivity.findViewById(R.id.contest_candidate_list_header).setVisibility(View.GONE);
            }

        } catch (Exception ex) {
            Log.e("ContestFragment", "Failed to get contest info!");
            ex.printStackTrace();
        }
    }

    /**
     * Hide ballot fragment components here, then show them again when user goes back.
     * Doing this because replacing a fragment within a TabBar doesn't remove the old fragment.
     * Ballot layout has its contents wrapped in an inner RelativeLayout so there is only one
     * child view to hide/show here.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContainer = container;
        Log.d("ContestFragment:onCreateView", "Hiding ballot container's view");
        container.getChildAt(0).setVisibility(View.INVISIBLE);

        return inflater.inflate(R.layout.fragment_contest, container, false);
    }

    @Override
    public void onDetach() {
        // Show ballot fragment components again when user goes back
        Log.d("ContestFragment:onDetach", "Showing ballot container's view again");
        mContainer.getChildAt(0).setVisibility(View.VISIBLE);

        super.onDetach();
    }

    public interface OnInteractionListener {
    }
}
