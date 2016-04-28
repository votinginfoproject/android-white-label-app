package com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;

/**
 * Created by max on 4/15/16.
 */
public abstract class ElectionDetailsPresenter extends BasePresenter<ElectionDetailsView> {
    public abstract ElectionAdministrationBody getLocalAdmin();

    public abstract ElectionAdministrationBody getStateAdmin();

    public abstract Election getElection();

    public abstract void linkSelected(String urlString);

    public abstract void addressSelected(String addressString);

    public abstract void reportErrorClicked();
}
