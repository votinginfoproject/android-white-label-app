package com.votinginfoproject.VotingInformationProject.models;

import java.util.List;

/**
 * Created by kathrynkillebrew on 7/31/14.
 */
public class GoogleDirectionsApiRoute {
    public String summary;
    public GoogleDirectionsApiBounds bounds;
    public String copyrights;
    public GoogleDirectionsApiPolyline overview_polyline;
    public List<String> warnings;
    public List<Integer> waypoint_order;
    public List<GoogleDirectionsApiLeg> legs;
}
