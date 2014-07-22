package com.votinginfoproject.VotingInformationProject.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Contest;


public class ContestFragment extends Fragment {
    private static final String CONTEST_NUM = "contest_number";
    private int contestNum;
    Contest contest;
    private OnInteractionListener mListener;
    private ViewGroup mContainer;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contest_number Index of this contest within the list of contests on VoterInfo object
     * @return A new instance of fragment ContestFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        TextView contest_label = (TextView)this.getActivity().findViewById(R.id.contest_title);
        if (contest_label == null) {
            Log.d("ContestFragment", "contest_label is null in onActivityCreated");
        } else {
            Log.d("ContestFragment", "GOT CONTEST LABEL");
            contest_label.setText("CONTEST IS #" + contestNum);
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

        View rootView = inflater.inflate(R.layout.fragment_contest, container, false);
        return rootView;
    }

    @Override
    public void onDetach() {
        // Show ballot fragment components again when user goes back
        Log.d("ContestFragment:onDetach", "Showing ballot container's view again");
        mContainer.getChildAt(0).setVisibility(View.VISIBLE);

        super.onDetach();
        mListener = null;
    }

    public interface OnInteractionListener {
        // TODO: Add methods here as necessary
    }
}
