package com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

/**
 * Created by max on 4/15/16.
 */
public abstract class ElectionDetailsPresenter extends BasePresenter<ElectionDetailsView> {
    public abstract VoterInfo getVoterInfo();

    public abstract void linkSelected(String urlString);
}
