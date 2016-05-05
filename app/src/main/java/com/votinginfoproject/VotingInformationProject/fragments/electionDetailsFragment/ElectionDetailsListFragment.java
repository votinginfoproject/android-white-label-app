package com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity.VoterInformationActivity;
import com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity.VoterInformationView;
import com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment.BottomNavigationFragment;
import com.votinginfoproject.VotingInformationProject.views.ElectionDetailsItemDecoration;

/**
 * Created by max on 4/15/16.
 */
public class ElectionDetailsListFragment extends Fragment implements BottomNavigationFragment, ElectionDetailsView {
    private ElectionDetailsPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private ElectionDetailsRecyclerViewAdapter mAdapter;
    private Toolbar mToolbar;

    public ElectionDetailsListFragment() {
    }

    public static ElectionDetailsListFragment newInstance() {
        ElectionDetailsListFragment fragment = new ElectionDetailsListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPresenter = new ElectionDetailsPresenterImpl();
        mPresenter.setView(this);

        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        Context context = view.getContext();

        mAdapter = new ElectionDetailsRecyclerViewAdapter(context, mPresenter);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addItemDecoration(new ElectionDetailsItemDecoration(getActivity(), ElectionDetailsItemDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.invalidate();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (view != null) {
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);

            if (mToolbar != null) {
                mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                mToolbar.setTitle(R.string.bottom_navigation_title_details);
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
    public void resetView() {
        mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, 0);
        mAdapter.collapseAll();
    }

    @Override
    public void navigateToURL(String urlString) {
        if (getActivity() instanceof ElectionDetailsListFragmentCallback) {
            ((ElectionDetailsListFragmentCallback) getActivity()).navigateToURL(urlString);
        }
    }

    @Override
    public void navigateToErrorView() {
        if (getActivity() instanceof ElectionDetailsListFragmentCallback) {
            ((ElectionDetailsListFragmentCallback) getActivity()).reportErrorButtonClicked();
        }
    }

    @Override
    public void navigateToDirectionsView(String address) {
        if (getActivity() instanceof ElectionDetailsListFragmentCallback) {
            ((ElectionDetailsListFragmentCallback) getActivity()).navigateToDirectionsView(address);
        }
    }

    public interface ElectionDetailsListFragmentCallback {
        void navigateToURL(String urlString);

        void reportErrorButtonClicked();

        void navigateToDirectionsView(String address);
    }
}
