package com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.fragments.ElectionDetailsFragment;
import com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment.BottomNavigationFragment;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.DividerItemDecoration;

/**
 * Created by max on 4/15/16.
 */
public class ElectionDetailsListFragment extends Fragment implements BottomNavigationFragment, ElectionDetailsView {

    private ElectionDetailsPresenter mPresenter;

    private RecyclerView mRecyclerView;

    private ElectionDetailsRecyclerViewAdapter mAdapter;

    public static ElectionDetailsListFragment newInstance() {
        ElectionDetailsListFragment fragment = new ElectionDetailsListFragment();
        return fragment;
    }

    public ElectionDetailsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPresenter = new ElectionDetailsPresenterImpl();

        View view = inflater.inflate(R.layout.fragment_recycler_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        Context context = view.getContext();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new ElectionDetailsRecyclerViewAdapter(context, mPresenter.getVoterInfo());

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.invalidate();

        return view;
    }


    @Override
    public void resetView() {

    }
}
