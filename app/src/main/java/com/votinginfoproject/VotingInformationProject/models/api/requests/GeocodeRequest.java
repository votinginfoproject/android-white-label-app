package com.votinginfoproject.VotingInformationProject.models.api.requests;

import com.votinginfoproject.VotingInformationProject.models.PollingLocation;

import java.util.ArrayList;

/**
 * Created by marcvandehey on 4/15/16.
 */
public class GeocodeRequest implements RequestType {

    private ArrayList<PollingLocation> pollingLocations;

    @Override
    public String buildQueryString() {
        return null;
    }
}
