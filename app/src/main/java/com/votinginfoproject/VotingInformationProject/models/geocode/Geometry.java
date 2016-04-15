package com.votinginfoproject.VotingInformationProject.models.geocode;

/**
 * Created by marcvandehey on 4/15/16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Bounds;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Location;

public class Geometry {

    @SerializedName("bounds")
    @Expose
    private Bounds bounds;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("location_type")
    @Expose
    private String locationType;
    @SerializedName("viewport")
    @Expose
    private Viewport viewport;

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }
}
