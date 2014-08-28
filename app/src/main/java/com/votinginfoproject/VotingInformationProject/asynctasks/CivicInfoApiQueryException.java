package com.votinginfoproject.VotingInformationProject.asynctasks;

/**
 * Created by kathrynkillebrew on 7/15/14.
 * Cannot define this as subclass of CivicInfoApiQuery, as that is a generic class
 */

public class CivicInfoApiQueryException extends Exception {
    public CivicInfoApiQueryException(String message) {
        super(message);
    }
}
