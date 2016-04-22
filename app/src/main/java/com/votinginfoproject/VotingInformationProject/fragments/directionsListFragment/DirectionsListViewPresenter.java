package com.votinginfoproject.VotingInformationProject.fragments.directionsListFragment;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Step;

import java.util.List;

/**
 * Created by max on 4/22/16.
 */
public abstract class DirectionsListViewPresenter extends BasePresenter<DirectionsListView> {
    abstract List<Step> getSteps();
}
