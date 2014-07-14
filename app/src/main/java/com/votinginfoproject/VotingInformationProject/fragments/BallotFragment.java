package com.votinginfoproject.VotingInformationProject.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.votinginfoproject.VotingInformationProject.R;

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
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "ballot_num";

    private int ballotId;

    private OnInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ballot_num Ballot number
     * @return A new instance of fragment BallotFragment.
     */
    public static BallotFragment newInstance(int ballot_num) {
        BallotFragment fragment = new BallotFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, ballot_num);
        fragment.setArguments(args);
        return fragment;
    }
    public BallotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ballotId= -1;
        if (getArguments() != null) {
            ballotId = getArguments().getInt(ARG_PARAM1, 1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ballot, container, false);
        TextView label = (TextView)rootView.findViewById(R.id.section_label);
        label.setText("Ballot ID: " + ballotId);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnInteractionListener) activity;
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
