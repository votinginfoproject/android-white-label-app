package com.votinginfoproject.VotingInformationProject.models;

import com.votinginfoproject.VotingInformationProject.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kathrynkillebrew on 7/17/14.
 */
public class CivicApiError {

    /**
     * Here are some of the possible error codes.
     * TODO:  Find out what the other error codes are.
     *
     * reason -> message (notes)
     * ------------------
     * invalid -> Election unknown
     * required ->  No address provided
     * parseError -> Failed to parse address
     * notFound -> No information for this address
     */

    // map error reason to string resource error message
    public static final Map<String, Integer> errorMessages;
    static {
        Map<String, Integer> messages = new HashMap<>(4);
        messages.put("invalid", R.string.fragment_home_error_no_election_for_address);
        messages.put("required", R.string.fragment_home_error_no_address);
        messages.put("parseError", R.string.fragment_home_error_parsing_address);
        messages.put("notFound", R.string.fragment_home_error_address_not_found);
        errorMessages = Collections.unmodifiableMap(messages);
    }


    public class Error {
        public String domain;
        public String reason;
        public String message;
    }

    public Long code;
    public String message;  // seems to be same message as first error in list
    public List<Error> errors;
}
