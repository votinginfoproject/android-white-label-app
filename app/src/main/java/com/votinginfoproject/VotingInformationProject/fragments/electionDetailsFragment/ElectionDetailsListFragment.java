package com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity.VoterInformationActivity;
import com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity.VoterInformationView;
import com.votinginfoproject.VotingInformationProject.fragments.BaseFragment;
import com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment.BottomNavigationFragment;
import com.votinginfoproject.VotingInformationProject.views.ElectionDetailsItemDecoration;

/**
 * Created by max on 4/15/16.
 */
public class ElectionDetailsListFragment extends BaseFragment<ElectionDetailsPresenter> implements BottomNavigationFragment, ElectionDetailsView {
    private RecyclerView mRecyclerView;
    private ElectionDetailsRecyclerViewAdapter mAdapter;
    private View mNoContentContainer;

    public static ElectionDetailsListFragment newInstance() {
        return new ElectionDetailsListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ElectionDetailsPresenter presenter = new ElectionDetailsPresenterImpl();
        presenter.setView(this);
        setPresenter(presenter);

        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        Context context = view.getContext();

        mAdapter = new ElectionDetailsRecyclerViewAdapter(context, presenter);

        mNoContentContainer = view.findViewById(R.id.fragment_recycler_container_no_content);

        TextView emptyText = (TextView) mNoContentContainer.findViewById(R.id.fragment_recycler_text_view_no_content);
        emptyText.setText(R.string.details_label_empty_list);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addItemDecoration(new ElectionDetailsItemDecoration(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.invalidate();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (view != null) {
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

            if (toolbar != null) {
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                toolbar.setTitle(R.string.bottom_navigation_title_details);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
    public void showNoContentView() {
        mNoContentContainer.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
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
