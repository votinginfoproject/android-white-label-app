package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.candidateInformationFragment;

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
import com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment.BottomNavigationFragment;

/**
 * Created by marcvandehey on 5/2/16.
 */
public class CandidateInformationFragment extends Fragment implements CandidateInformationView, BottomNavigationFragment {
    private static final String TAG = CandidateInformationFragment.class.getSimpleName();
    private static final String ARG_PRESENTER = "arg_presenter";

    private CandidateInformationRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private CandidateInformationListener mListener;

    public CandidateInformationFragment() {

    }

    public static CandidateInformationFragment newInstance(CandidateInformationPresenter presenter) {
        CandidateInformationFragment candidateInformationFragment = new CandidateInformationFragment();

        Bundle args = new Bundle();

        args.putParcelable(ARG_PRESENTER, presenter);

        candidateInformationFragment.setArguments(args);

        return candidateInformationFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            CandidateInformationPresenter presenter = getArguments().getParcelable(ARG_PRESENTER);

            mAdapter = new CandidateInformationRecyclerViewAdapter();
            mAdapter.setPresenter(presenter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.polling_locations_list);

        //Update Decoration
//        mRecyclerView.addItemDecoration(new BallotItemDecoration(getActivity()));

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
                mToolbar.setTitle(R.string.candidate_toolbar);
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
        if (context instanceof CandidateInformationListener) {
            mListener = (CandidateInformationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ContestListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAdapter.setPresenter(null);
    }

    @Override
    public void resetView() {
        mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, 0);
    }

    @Override
    public void navigateToUrl(String url) {
        mListener.navigateToURL(url);
    }

    @Override
    public void navigateToPhone(String phoneNumber) {
        mListener.phoneNumberClicked(phoneNumber);
    }

    @Override
    public void navigateToEmail(String email) {
        mListener.emailClicked(email);
    }

    @Override
    public void reportClicked() {
        mListener.reportErrorClicked();
    }

    public interface CandidateInformationListener {
        void navigateToURL(String urlString);

        void phoneNumberClicked(String phoneNumber);

        void emailClicked(String email);

        void reportErrorClicked();
    }
}