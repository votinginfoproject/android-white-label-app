package com.votinginfoproject.VotingInformationProject.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;

/**
 * Created by max on 5/10/16.
 */
public class BaseFragment<Presenter> extends Fragment {

    private BasePresenter mPresenter;

    public Presenter getPresenter() {
        return (Presenter) mPresenter;
    }

    public void setPresenter(BasePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mPresenter != null) {
            mPresenter.onSaveState(outState);
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (mPresenter != null) {
            mPresenter.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mPresenter != null) {
            mPresenter.onDetachView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPresenter != null) {
            mPresenter.onAttachView(this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (mPresenter != null) {
            mPresenter.onAttachView(this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (mPresenter != null) {
            mPresenter.onAttachView(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mPresenter != null) {
            mPresenter.onDetachView();
        }
    }
}
