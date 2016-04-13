package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment.BottomNavigationFragment;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.DividerItemDecoration;

import java.util.ArrayList;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PollingSitesFragment extends BottomNavigationFragment implements Toolbar.OnMenuItemClickListener, PollingSitesView {

    private static final String TAG = PollingSitesFragment.class.getSimpleName();

    private static final String ARG_ELECTION = "election";

    private PollingSitesPresenterImpl mPresenter;

    private PollingSiteOnClickListener mListener;

    private PollingSiteItemRecyclerViewAdapter mAdapter;

    private RecyclerView mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PollingSitesFragment() {
    }

    public static PollingSitesFragment newInstance() {
        PollingSitesFragment fragment = new PollingSitesFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new PollingSitesPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pollings_sites_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.polling_locations_list);

        Context context = view.getContext();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new PollingSiteItemRecyclerViewAdapter(context, mPresenter.getElection(), mPresenter.getAllLocations(), mListener);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.invalidate();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setOnMenuItemClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PollingSiteOnClickListener) {
            mListener = (PollingSiteOnClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public int getTitle() {
        return R.string.bottom_navigation_title_polls;
    }

    @Override
    public int getMenu() {
        return R.menu.polling_sites_list;
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
        mListener.pollingSiteClicked(pollingLocation);
    }

    @Override
    public void navigateToErrorForm() {
        mListener.reportErrorClicked();
    }

    @Override
    public void navigateToMap() {
        mListener.navigateToMap();
    }

    @Override
    public void updateList(ArrayList<PollingLocation> locations) {
        mAdapter.updatePollingLocations(locations);
        mRecyclerView.invalidate();
        mRecyclerView.invalidateItemDecorations();
    }

    /**
     * Recycler View Interaction methods
     * <p/>
     * Must be implemented by the Activity
     */
    public interface PollingSiteOnClickListener {
        void navigateToMap();

        void pollingSiteClicked(PollingLocation location);

        void reportErrorClicked();
    }
}
