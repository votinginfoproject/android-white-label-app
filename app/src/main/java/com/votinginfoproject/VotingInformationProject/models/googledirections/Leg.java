package com.votinginfoproject.VotingInformationProject.models.googledirections;

import java.util.List;

/**
 * Routes with no waypoints will consist of a single leg.
 *
 * Created by kathrynkillebrew on 7/31/14.
 */
public class Leg {
    public List<Step> steps;
    public List<Integer> via_waypoint;
    public Distance distance;
    public Duration duration;
    public String start_address;
    public String end_address;
    public Location start_location;
    public Location end_location;
}
