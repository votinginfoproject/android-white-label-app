package com.votinginfoproject.VotingInformationProject.models.GoogleDirections;

import java.util.List;

/**
 * Created by kathrynkillebrew on 7/31/14.
 */
public class Route {
    public String summary;
    public Bounds bounds;
    public String copyrights;
    public Polyline overview_polyline;
    public List<String> warnings;
    public List<Integer> waypoint_order;
    public List<Leg> legs;
}
