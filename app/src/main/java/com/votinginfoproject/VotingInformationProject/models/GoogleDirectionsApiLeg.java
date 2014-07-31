package com.votinginfoproject.VotingInformationProject.models;

import java.util.List;

/**
 * Routes with no waypoints will consist of a single leg.
 *
 * Created by kathrynkillebrew on 7/31/14.
 */
public class GoogleDirectionsApiLeg {
    public List<GoogleDirectionsApiStep> steps;
    public List<Integer> via_waypoint;
    public GoogleDirectionsApiDistance distance;
    public GoogleDirectionsApiDuration duration;
    public String start_address;
    public String end_address;
    public GoogleDirectionsApiLocation start_location;
    public GoogleDirectionsApiLocation end_location;
}
