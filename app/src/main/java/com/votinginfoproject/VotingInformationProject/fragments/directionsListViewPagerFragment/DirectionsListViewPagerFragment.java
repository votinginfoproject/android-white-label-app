package com.votinginfoproject.VotingInformationProject.fragments.directionsListViewPagerFragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.votinginfoproject.VotingInformationProject.R;

public class DirectionsListViewPagerFragment extends Fragment implements  DirectionsListViewPagerView {
    public DirectionsListViewPagerPresenter mPresenter;
    public ViewPager mViewPager;

    public DirectionsListViewPagerFragment() {
        // Required empty public constructor
    }

    public static DirectionsListViewPagerFragment newInstance() {
        return new DirectionsListViewPagerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_directions_list_view_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter = new DirectionsListViewPagerPresenterImpl();
        mPresenter.setView(this);

        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPager.setAdapter(new DirectionsListViewPagerAdapter(getFragmentManager(), mPresenter));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
