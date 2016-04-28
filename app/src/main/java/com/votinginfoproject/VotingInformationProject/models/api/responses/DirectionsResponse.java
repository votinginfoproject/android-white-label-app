package com.votinginfoproject.VotingInformationProject.models.api.responses;

import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;

import java.util.List;

/**
 * Created by kathrynkillebrew on 7/31/14.
 */
public class DirectionsResponse {
    public String status;
    public List<Route> routes;
    public String mode;

    public boolean hasErrors() {
        return status != null && !status.equals("OK");
    }
}
