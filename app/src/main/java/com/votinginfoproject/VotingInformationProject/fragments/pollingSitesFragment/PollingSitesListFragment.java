package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity.VoterInformationActivity;
import com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity.VoterInformationView;
import com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment.BottomNavigationFragment;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.DividerItemDecoration;

import java.util.ArrayList;

public class PollingSitesListFragment extends Fragment implements BottomNavigationFragment, Toolbar.OnMenuItemClickListener, PollingSitesView {

    private static final String TAG = PollingSitesListFragment.class.getSimpleName();

    private static final String ARG_CURRENT_SORT = "current_sort";

    private PollingSitesPresenterImpl mPresenter;

    private PollingSitesListener mListener;

    private PollingSiteItemRecyclerViewAdapter mAdapter;

    private RecyclerView mRecyclerView;

    private View mEmptyView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PollingSitesListFragment() {
    }

    public static PollingSitesListFragment newInstance() {
        return new PollingSitesListFragment();
    }

    public static PollingSitesListFragment newInstance(@LayoutRes int currentSort) {
        PollingSitesListFragment fragment = new PollingSitesListFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_CURRENT_SORT, currentSort);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            mPresenter = new PollingSitesPresenterImpl(getArguments().getInt(ARG_CURRENT_SORT, R.id.sort_all));
        } else {
            mPresenter = new PollingSitesPresenterImpl();
        }

        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mEmptyView = view.findViewById(R.id.empty);

        TextView emptyText = (TextView) mEmptyView.findViewById(R.id.empty_text);
        emptyText.setText(R.string.locations_empty);

        Context context = view.getContext();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        mAdapter = new PollingSiteItemRecyclerViewAdapter(context, mPresenter.getElection(), mPresenter.getSortedLocations(), mListener);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.invalidate();

        mPresenter.setView(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof PollingSitesListener) {
            mListener = (PollingSitesListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof PollingSitesListener) {
            mListener = (PollingSitesListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (view != null) {
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

            if (toolbar == null) {
                Log.e(TAG, "No toolbar found in class: " + getClass().getSimpleName());
            } else {
                toolbar.inflateMenu(R.menu.polling_sites_list);

                toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                toolbar.setTitle(R.string.bottom_navigation_title_polls);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getActivity() instanceof VoterInformationActivity) {
                            ((VoterInformationView) getActivity()).navigateBack();
                        }
                    }
                });

                toolbar.setOnMenuItemClickListener(this);

                if (!mPresenter.hasPollingLocations()) {
                    toolbar.getMenu().removeItem(R.id.sort_polling_locations);
                }

                if (!mPresenter.hasEarlyVotingLocations()) {
                    toolbar.getMenu().removeItem(R.id.sort_early_vote);
                }

                if (!mPresenter.hasDropBoxLocations()) {
                    toolbar.getMenu().removeItem(R.id.sort_drop_boxes);
                }

                //If there aren't any polling locations
                if (!mPresenter.hasPollingLocations() &&
                        !mPresenter.hasEarlyVotingLocations() &&
                        !mPresenter.hasDropBoxLocations()) {
                    toolbar.getMenu().removeGroup(R.id.filter_polling_sites);
                } else {
                    toolbar.getMenu().findItem(mPresenter.getCurrentSort()).setChecked(true);
                }
            }
        }
    }

    @Override
    public void resetView() {
        mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, 0);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        mPresenter.menuItemClicked(item.getItemId());
        item.setChecked(true);

        return true;
    }

    @Override
    public void navigateToDirections(PollingLocation pollingLocation) {
        mListener.navigateToDirections(pollingLocation);
    }

    @Override
    public void navigateToMap(@LayoutRes int currentSort) {
        mListener.mapButtonClicked(currentSort);
    }

    @Override
    public void navigateToList(@LayoutRes int currentSort) {
        //Not implemented
    }

    @Override
    public void updateList(ArrayList<PollingLocation> locations) {
        mAdapter.updatePollingLocations(locations);
        mRecyclerView.invalidate();
        mRecyclerView.invalidateItemDecorations();
    }

    @Override
    public void showLocationCard(PollingLocation location) {
        //Not Implemented
    }

    @Override
    public void toggleEmpty(boolean empty) {
        int visibility = empty ? View.VISIBLE : View.GONE;

        mEmptyView.setVisibility(visibility);
        mRecyclerView.setVisibility(View.GONE);
    }

    /**
     * Recycler View Interaction methods
     * <p/>
     * Must be implemented by the Activity
     */
    public interface PollingSitesListener {
        void mapButtonClicked(@LayoutRes int currentSort);

        void listButtonClicked(@LayoutRes int currentSort);

        void navigateToDirections(PollingLocation location);

        void reportErrorClicked();

        void startPollingLocation();
    }
}
