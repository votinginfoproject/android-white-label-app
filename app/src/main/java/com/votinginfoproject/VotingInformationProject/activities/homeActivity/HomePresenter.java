package com.votinginfoproject.VotingInformationProject.activities.homeActivity;

import android.content.Context;
import android.support.annotation.NonNull;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;

/**
 * Created by marcvandehey on 3/31/16.
 */
public abstract class HomePresenter extends BasePresenter<HomeView> {
    public abstract void selectedElection(Context context, String address, int election);

    public abstract void selectedParty(int party);

    public abstract void electionTextViewClicked();

    public abstract void partyTextViewClicked();

    public abstract void goButtonClicked();

    public abstract void aboutButtonClicked();

    public abstract void searchButtonClicked(@NonNull Context context, @NonNull String searchAddress);
}
