package com.votinginfoproject.VotingInformationProject.models;

/**
 * Created by marcvandehey on 3/29/16.
 */
// track the labels used in the filter drop-down
public class FilterLabels {
    public final String ALL;
    public final String EARLY;
    public final String POLLING;
    public final String DROP_BOX;

    public FilterLabels(String all, String early, String polling, String dropbox) {
        this.ALL = all;
        this.EARLY = early;
        this.POLLING = polling;
        this.DROP_BOX = dropbox;
    }
}