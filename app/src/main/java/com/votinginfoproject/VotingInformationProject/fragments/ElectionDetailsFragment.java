package com.votinginfoproject.VotingInformationProject.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.State;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ElectionDetailsFragment extends Fragment {

    private Activity mActivity;
    private MovementMethod mLinkMovementMethod;

    // collapsible section headers, and their sub-sections
    static final List<List<Integer>> detailSections = new ArrayList<List<Integer>>(14) {{
        add(Arrays.asList(R.id.details_state_admin_body_section_label, R.id.details_state_admin_body_table));
        add(Arrays.asList(R.id.details_local_admin_body_section_label, R.id.details_local_admin_body_table));
        add(Arrays.asList(R.id.details_state_links_section_header, R.id.details_state_links_section));
        add(Arrays.asList(R.id.details_local_links_section_header, R.id.details_local_links_section));
        add(Arrays.asList(R.id.details_state_voter_services_section_header, R.id.details_state_voter_services_section));
        add(Arrays.asList(R.id.details_local_voter_services_section_header, R.id.details_local_voter_services_section));
        add(Arrays.asList(R.id.details_state_hours_of_operation_section_header, R.id.details_state_hours_of_operation_section));
        add(Arrays.asList(R.id.details_local_hours_of_operation_section_header, R.id.details_local_hours_of_operation_section));
        add(Arrays.asList(R.id.details_state_correspondence_address_section_header, R.id.details_state_correspondence_address_section));
        add(Arrays.asList(R.id.details_local_correspondence_address_section_header, R.id.details_local_correspondence_address_section));
        add(Arrays.asList(R.id.details_state_physical_address_section_header, R.id.details_state_physical_address_section));
        add(Arrays.asList(R.id.details_local_physical_address_section_header, R.id.details_local_physical_address_section));
        add(Arrays.asList(R.id.details_state_election_officials_section_header, R.id.details_state_election_officials_section));
        add(Arrays.asList(R.id.details_local_election_officials_section_header, R.id.details_local_election_officials_section));
    }};

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ElectionDetailsFragment.
     */
    public static ElectionDetailsFragment newInstance() {
        ElectionDetailsFragment fragment = new ElectionDetailsFragment();
        return fragment;
    }
    public ElectionDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_election_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("ElectionDetailsFragment", "In onActivityCreated");
        mActivity = getActivity();
        mLinkMovementMethod = LinkMovementMethod.getInstance();

        // set expandable section header click listeners
        for (List<Integer>detail : detailSections) {
            setSectionClickListener(detail.get(0), detail.get(1));
        }

        // populate fields with API results
        setContents();
    }

    /**
     * Helper function to set click listeners for expandable section headers.
     *
     * @param sectionHeaderId R id of section header (button)
     * @param sectionId R id of sub-section to show/hide on click
     */
    private void setSectionClickListener(int sectionHeaderId, int sectionId) {
        mActivity.findViewById(sectionHeaderId).setOnClickListener(view -> {
            Button btn = (Button)view;
            View section = mActivity.findViewById(sectionId);
            if (section.getVisibility() == View.GONE) {
                section.setVisibility(View.VISIBLE);
                btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_find_previous_holo_dark, 0);
            } else {
                section.setVisibility(View.GONE);
                btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_find_next_holo_dark, 0);
            }
        });
    }

    /** Helper function to populate the view labels.
     *
     */
    private void setContents() {
        TextView title = (TextView) mActivity.findViewById(R.id.details_election_title);
        TextView subTitle = (TextView) mActivity.findViewById(R.id.details_election_subtitle);

        try {
            VoterInfo voterInfo = ((VIPTabBarActivity) mActivity).getVoterInfo();
            Election election = voterInfo.election;
            title.setText(election.name);
            subTitle.setText(election.getFormattedDate());

            // TODO: what if election is in multiple states?  Find state for current address, and use that?
            State thisState = voterInfo.state.get(0);
            ElectionAdministrationBody stateAdmin = thisState.electionAdministrationBody;

            if (stateAdmin != null) {
                TextView state_name = (TextView) mActivity.findViewById(R.id.details_state_admin_body_name);
                state_name.setText(stateAdmin.name);

                // set state admin body table values

                // set fields that are links
                setLink(R.id.details_state_election_info_url_label, R.id.details_state_election_info_url_row, stateAdmin.electionInfoUrl);
                setLink(R.id.details_state_registration_url_label, R.id.details_state_registration_url_row, stateAdmin.electionRegistrationUrl);
                setLink(R.id.details_state_registration_confirmation_url_label, R.id.details_state_registration_confirmation_url_row, stateAdmin.electionRegistrationConfirmationUrl);
                setLink(R.id.details_state_absentee_url_label, R.id.details_state_absentee_url_row, stateAdmin.absenteeVotingInfoUrl);
                setLink(R.id.details_state_location_finder_url_label, R.id.details_state_location_finder_url_row, stateAdmin.votingLocationFinderUrl);
                setLink(R.id.details_state_ballot_info_url_label, R.id.details_state_ballot_info_url_row, stateAdmin.ballotInfoUrl);
                setLink(R.id.details_state_election_rules_url_label, R.id.details_state_election_rules_url_row, stateAdmin.electionRulesUrl);

                // set non-link field values
                setTextView(R.id.details_state_voter_services, R.id.details_state_voter_services_section_header, stateAdmin.getVoterServices());
                setTextView(R.id.details_state_hours_of_operation, R.id.details_state_hours_of_operation_section_header, stateAdmin.hoursOfOperation);
                setTextView(R.id.details_state_correspondence_address, R.id.details_state_correspondence_address_section_header, stateAdmin.getCorrespondenceAddress());
                setTextView(R.id.details_state_physical_address, R.id.details_state_physical_address_section_header, stateAdmin.getPhysicalAddress());
                setTextView(R.id.details_state_election_officials, R.id.details_state_election_officials_section_header, stateAdmin.getElectionOfficials());

            } else {
                View stateTable = mActivity.findViewById(R.id.details_state_admin_body_table);
                stateTable.setVisibility(View.GONE);
                View stateTableHeader = mActivity.findViewById(R.id.details_state_admin_body_section_label);
                stateTableHeader.setVisibility(View.GONE);
            }

            if (thisState.local_jurisdiction != null && thisState.local_jurisdiction.electionAdministrationBody != null ) {
                ElectionAdministrationBody localAdmin = thisState.local_jurisdiction.electionAdministrationBody;
                Log.d("ElectionDetailsFragment", "Got local election admin body " + localAdmin.name);

                TextView local_name = (TextView) mActivity.findViewById(R.id.details_local_admin_body_name);
                local_name.setText(localAdmin.name);

                // set local admin body table values

                // set fields that are links
                setLink(R.id.details_local_election_info_url_label, R.id.details_local_election_info_url_row, localAdmin.electionInfoUrl);
                setLink(R.id.details_local_registration_url_label, R.id.details_state_registration_url_row, localAdmin.electionRegistrationUrl);
                setLink(R.id.details_local_registration_confirmation_url_label, R.id.details_local_registration_confirmation_url_row, localAdmin.electionRegistrationConfirmationUrl);
                setLink(R.id.details_local_absentee_url_label, R.id.details_state_absentee_url_row, localAdmin.absenteeVotingInfoUrl);
                setLink(R.id.details_local_location_finder_url_label, R.id.details_local_location_finder_url_row, localAdmin.votingLocationFinderUrl);
                setLink(R.id.details_local_ballot_info_url_label, R.id.details_local_ballot_info_url_row, localAdmin.ballotInfoUrl);
                setLink(R.id.details_local_election_rules_url_label, R.id.details_local_election_rules_url_row, localAdmin.electionRulesUrl);

                // set non-link field values
                setTextView(R.id.details_local_voter_services, R.id.details_local_voter_services_section_header, localAdmin.getVoterServices());
                setTextView(R.id.details_local_hours_of_operation, R.id.details_local_hours_of_operation_section_header, localAdmin.hoursOfOperation);
                setTextView(R.id.details_local_correspondence_address, R.id.details_local_correspondence_address_section_header, localAdmin.getCorrespondenceAddress());
                setTextView(R.id.details_local_physical_address, R.id.details_local_physical_address_section_header, localAdmin.getPhysicalAddress());
                setTextView(R.id.details_local_election_officials, R.id.details_local_election_officials_section_header, localAdmin.getElectionOfficials());


            } else {
                View localTable = mActivity.findViewById(R.id.details_local_admin_body_table);
                localTable.setVisibility(View.GONE);
                View localTableHeader = mActivity.findViewById(R.id.details_local_admin_body_section_label);
                localTableHeader.setVisibility(View.GONE);
            }

        } catch (Exception ex) {
            Log.e("ElectionDetailsFragment", "Failed to set election details info!");
            ex.printStackTrace();
        }

    }

    /**
     * Helper function to turn labels into links for fields that are URLs.
     *
     * @param labelId R id of the label to link-ify
     * @param containerId R id of the label's parent view (to hide it if there's no link found)
     * @param val String containing the URL
     */
    private void setLink(int labelId, int containerId, String val) {
        TextView textView = (TextView) mActivity.findViewById(labelId);
        if (val != null && !val.isEmpty()) {
            String label = textView.getText().toString();
            textView.setText(Html.fromHtml("<a href=\"" + val + "\">" + label + "</a>"));
            textView.setMovementMethod(mLinkMovementMethod);
        } else {
            View container = mActivity.findViewById(containerId);
            container.setVisibility(View.GONE);
        }
    }

    /**
     * Helper function to set a TextView to a string, or hide the TextView's containing view.
     * if the string is null or empty.
     *
     * @param textViewId R id of the TextView to set
     * @param containerId R id of the TextView's parent view, to hide if value is missing
     * @param val String to put in the TextView
     */
    private void setTextView(int textViewId, int containerId, String val) {
        TextView textView = (TextView) mActivity.findViewById(textViewId);
        if (val != null && !val.isEmpty()) {
            textView.setText(val);
        } else {
            View container = mActivity.findViewById(containerId);
            container.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
