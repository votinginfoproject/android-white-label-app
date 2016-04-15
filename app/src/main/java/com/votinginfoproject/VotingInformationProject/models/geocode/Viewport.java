package com.votinginfoproject.VotingInformationProject.models.geocode;

/**
 * Created by marcvandehey on 4/15/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Bounds;

public class Viewport {

    @SerializedName("northeast")
    @Expose
    private Bounds northeast;
    @SerializedName("southwest")
    @Expose
    private Bounds southwest;

    public Bounds getNortheast() {
        return northeast;
    }

    public void setNortheast(Bounds northeast) {
        this.northeast = northeast;
    }

    public Bounds getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Bounds southwest) {
        this.southwest = southwest;
    }

}
