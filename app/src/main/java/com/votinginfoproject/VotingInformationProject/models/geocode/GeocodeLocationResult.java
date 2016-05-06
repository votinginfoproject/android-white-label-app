package com.votinginfoproject.VotingInformationProject.models.geocode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcvandehey on 4/15/16.
 */
public class GeocodeLocationResult {
    @SerializedName("results")
    @Expose
    private List<Result> results = new ArrayList<>();
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("error_message")
    private String errorMessage;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
