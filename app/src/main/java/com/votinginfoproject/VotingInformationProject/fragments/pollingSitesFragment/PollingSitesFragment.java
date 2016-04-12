package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment.BottomNavigationFragment;
import com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment.dummy.DummyContent.DummyItem;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PollingSitesFragment extends BottomNavigationFragment {

    private static final String TAG = PollingSitesFragment.class.getSimpleName();

    // TODO: Customize parameter argument names
    private static final String ARG_POLLING_ITEMS = "polling-locations";
    // TODO: Customize parameters
    private OnListFragmentInteractionListener mListener;

    private PollingSitesPresenterImpl mPresenter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PollingSitesFragment() {
    }

    public static PollingSitesFragment newInstance(ArrayList<PollingLocation> pollingLocations) {
        PollingSitesFragment fragment = new PollingSitesFragment();
        Bundle args = new Bundle();

        args.putParcelableArrayList(ARG_POLLING_ITEMS, pollingLocations);
        fragment.setArguments(args);

        Log.v(TAG, pollingLocations + "");


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //Setup
            ArrayList<PollingLocation> locations = getArguments().getParcelableArrayList(ARG_POLLING_ITEMS);

            mPresenter = new PollingSitesPresenterImpl(locations);

            Log.v(TAG, getArguments().get(ARG_POLLING_ITEMS) + "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pollingsiteitem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(new PollingSiteItemRecyclerViewAdapter(mPresenter.getAllLocations(), mListener));

            recyclerView.invalidate();
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
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
    public void scrollToTop() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
