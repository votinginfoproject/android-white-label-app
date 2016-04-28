package com.votinginfoproject.VotingInformationProject.models;

import java.util.List;

/**
 * Created by kathrynkillebrew on 7/14/14.
 * https://developers.google.com/civic-information/docs/v1/voterinfo/voterInfoQuery
 */
public class ElectionAdministrationBody {
    public String name;
    public String electionInfoUrl;
    public String electionRegistrationUrl;
    public String electionRegistrationConfirmationUrl;
    public String absenteeVotingInfoUrl;
    public String votingLocationFinderUrl;
    public String ballotInfoUrl;
    public String electionRulesUrl;
    public List<String> voter_services;
    public String hoursOfOperation;
    public List<ElectionOfficial> electionOfficials;
    private CivicApiAddress correspondenceAddress;
    private CivicApiAddress physicalAddress;

    /**
     * Helper function to return pretty-printed list of voter services
     *
     * @return String with each service separated by a newline (or empty string if none)
     */
    public String getVoterServices() {
        if (voter_services == null || voter_services.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (String service : voter_services) {
            builder.append(service);
            builder.append("\n");
        }

        // remove trailing newline
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public String getElectionOfficials() {
        if (electionOfficials == null || electionOfficials.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (ElectionOfficial official : electionOfficials) {
            if (official.name != null && !official.name.isEmpty()) {
                builder.append(official.name);
            }
            if (official.title != null && !official.title.isEmpty()) {
                builder.append(" - ");
                builder.append(official.title);
            }
            builder.append("\n");
        }

        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public CivicApiAddress getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddress(CivicApiAddress correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }

    public CivicApiAddress getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(CivicApiAddress physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public interface AdminBody {
        String STATE = "state_eab";
        String LOCAL = "local_eab";
    }
}
