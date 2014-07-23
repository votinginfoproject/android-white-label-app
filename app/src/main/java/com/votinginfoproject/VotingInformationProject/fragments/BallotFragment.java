package com.votinginfoproject.VotingInformationProject.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BallotFragment.OnInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BallotFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class BallotFragment extends Fragment {

    VoterInfo voterInfo;

    private OnInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BallotFragment.
     */
    public static BallotFragment newInstance() {
        BallotFragment fragment = new BallotFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public BallotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ballot, container, false);
        final VIPTabBarActivity myActivity = (VIPTabBarActivity)this.getActivity();
        // election label
        TextView election_name_label = (TextView)rootView.findViewById(R.id.ballot_election_name);
        TextView election_date_label = (TextView)rootView.findViewById(R.id.ballot_election_date);
        election_name_label.setText(voterInfo.election.name);
        election_date_label.setText(voterInfo.election.getFormattedDate());

        // populate contest list
        ArrayList contestInfo = new ArrayList<String>(voterInfo.contests.size());
        for (Contest contest : voterInfo.contests) {
            if (contest.type == "Referendum") {
                contestInfo.add(contest.referendumTitle + "\n" + contest.referendumSubtitle);
            } else {
                contestInfo.add(contest.office + "\n" + voterInfo.election.name);
            }
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(myActivity, android.R.layout.simple_selectable_list_item, contestInfo);
        ListView contestList = (ListView)rootView.findViewById(R.id.ballot_contests_list);
        contestList.setAdapter(adapter);
        contestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ContestsList", "clicked: " + voterInfo.contests.get(position).office);
                myActivity.showContestDetails(position);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnInteractionListener) activity;

            // get election info
            voterInfo = ((VIPTabBarActivity) activity).getVoterInfo();
            Log.d("BallotFragment", "Got election: " + voterInfo.election.name);

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnInteractionListener {
        // TODO: Add methods here as necessary
    }

}
