package com.votinginfoproject.VotingInformationProject.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by marcvandehey on 4/7/16.
 */
public class BaseActivity<Presenter> extends AppCompatActivity {

    private BasePresenter mPresenter;

    public Presenter getPresenter() {
        return (Presenter) mPresenter;
    }

    public void setPresenter(BasePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mPresenter != null) {
            mPresenter.onSaveState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (mPresenter != null) {
            mPresenter.onCreate(savedInstanceState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mPresenter != null) {
            mPresenter.onDetachView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPresenter != null) {
            mPresenter.onAttachView(this);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mPresenter != null) {
            mPresenter.onAttachView(this);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mPresenter != null) {
            mPresenter.onDetachView();
        }
    }
}
