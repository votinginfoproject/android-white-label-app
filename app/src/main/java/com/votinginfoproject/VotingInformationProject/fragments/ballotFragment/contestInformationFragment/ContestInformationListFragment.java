package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.contestInformationFragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity.VoterInformationActivity;
import com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity.VoterInformationView;
import com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.BallotRecyclerViewDecorator;
import com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment.BottomNavigationFragment;
import com.votinginfoproject.VotingInformationProject.models.Candidate;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.Election;

/**
 * Created by marcvandehey on 4/21/16.
 */
public class ContestInformationListFragment extends Fragment implements BottomNavigationFragment, ContestInformationView, ContestInformationRecyclerViewAdapter.ContestInformationItemOnClickListener {
    private static final String TAG = ContestInformationListFragment.class.getSimpleName();

    private static final String ARG_ELECTION = "arg_election";
    private static final String ARG_CONTEST = "arg_contest";

    private ContestInformationRecyclerViewAdapter mAdapter;
    private ContestInformationListener mListener;
    private RecyclerView mRecyclerView;

    public ContestInformationListFragment() {
        //Required Empty Constructor
    }

    public static ContestInformationListFragment newInstance(Election election, Contest contest) {
        ContestInformationListFragment fragment = new ContestInformationListFragment();
        Bundle args = new Bundle();

        args.putParcelable(ARG_ELECTION, election);
        args.putParcelable(ARG_CONTEST, contest);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Election election = getArguments().getParcelable(ARG_ELECTION);
            Contest contest = getArguments().getParcelable(ARG_CONTEST);

            ContestInformationPresenter presenter = new ContestInformationPresenterImpl(this, election, contest);
            mAdapter = new ContestInformationRecyclerViewAdapter(presenter, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.polling_locations_list);
        mRecyclerView.addItemDecoration(new BallotRecyclerViewDecorator(getActivity()));

        // Set the adapter
        Context context = view.getContext();

        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 1));

        if (mAdapter != null) {
            mRecyclerView.setAdapter(mAdapter);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar mToolbar;
        if (view != null) {
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);

            if (mToolbar == null) {
                Log.e(TAG, "No toolbar found in class: " + getClass().getSimpleName());
            } else {
                mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                mToolbar.setTitle(R.string.contest_toolbar);
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getActivity() instanceof VoterInformationActivity) {
                            ((VoterInformationView) getActivity()).navigateBack();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ContestInformationListener) {
            mListener = (ContestInformationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ContestInformationListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof ContestInformationListener) {
            mListener = (ContestInformationListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement ContestInformationListener");
        }
    }

    /**
     * Override getContext to return activity if context is not available
     * <p/>
     * This is a problem with older devices where getContext is not utilized
     *
     * @return
     */

    @Override
    public Context getContext() {
        try {
            return super.getContext();
        } catch (NoSuchMethodError error) {
            return getActivity();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void resetView() {
        //TODO DO THIS
        mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, 0);

    }

    @Override
    public void onCandidateClicked(Candidate candidate) {
        mListener.candidateClicked(candidate);
    }

    @Override
    public void onReportErrorClicked() {
        mListener.reportErrorClicked();
    }

    public interface ContestInformationListener {
        void candidateClicked(Candidate candidate);

        void reportErrorClicked();
    }
}
