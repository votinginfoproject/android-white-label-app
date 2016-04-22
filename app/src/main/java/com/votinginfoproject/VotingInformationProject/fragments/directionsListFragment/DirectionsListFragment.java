package com.votinginfoproject.VotingInformationProject.fragments.directionsListFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;

/**
 * Created by max on 4/22/16.
 */
public class DirectionsListFragment extends Fragment {
    private static final String ARG_TRANSIT_MODE = "transit_mode";

    public static DirectionsListFragment newInstance(String transitMode) {
        DirectionsListFragment fragment = new DirectionsListFragment();

        Bundle args = new Bundle();
        args.putString(ARG_TRANSIT_MODE, transitMode);

        fragment.setArguments(args);

        return  fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_directions2, container, false);

        Bundle args = getArguments();

        String transitMode = args.getString(ARG_TRANSIT_MODE);

        ((TextView) rootView.findViewById(R.id.section_label)).setText(transitMode);

        return rootView;
    }

}
