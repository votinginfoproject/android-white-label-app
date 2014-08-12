package com.votinginfoproject.VotingInformationProject.fragments;

import android.app.Activity;
import android.content.res.Resources;
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
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.State;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ElectionDetailsFragment extends Fragment {

    private Activity mActivity;
    Resources resources;
    private MovementMethod mLinkMovementMethod;

    int selectedButtonTextColor;
    int unselectedButtonTextColor;
    int selectedSectionBackground;
    int unselectedSectionBackground;

    ElectionAdministrationBody stateAdmin;
    ElectionAdministrationBody localAdmin;

    // track which location filter button was last clicked, and only refresh list if it changed
    int lastSelectedButtonId = R.id.locations_list_all_button;
    Button lastSelectedButton;

    // collapsible section headers, and their sub-sections
    // 0 -> section header, 1-> section, 2-> unselected section icon, 3-> selected section icon
    static final List<List<Integer>> detailSections = new ArrayList<List<Integer>>(14) {{
        add(Arrays.asList(R.id.details_links_section_header, R.id.details_links_section, R.drawable.ic_website, R.drawable.ic_website_active));
        add(Arrays.asList(R.id.details_voter_services_section_header, R.id.details_voter_services_section, R.drawable.ic_vservices, R.drawable.ic_vservices_active));
        add(Arrays.asList(R.id.details_hours_of_operation_section_header, R.id.details_hours_of_operation_section, R.drawable.ic_hours, R.drawable.ic_hours_active));
        add(Arrays.asList(R.id.details_correspondence_address_section_header, R.id.details_correspondence_address_section, R.drawable.ic_address, R.drawable.ic_address_active));
        add(Arrays.asList(R.id.details_physical_address_section_header, R.id.details_physical_address_section, R.drawable.ic_address, R.drawable.ic_address_active));
        add(Arrays.asList(R.id.details_election_officials_section_header, R.id.details_election_officials_section, R.drawable.ic_officials, R.drawable.ic_officials_active));
    }};

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ElectionDetailsFragment.
     */
    public static ElectionDetailsFragment newInstance() {
        return new ElectionDetailsFragment();
    }
    public ElectionDetailsFragment() {
        // Required empty public constructor
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
        resources = mActivity.getResources();
        mLinkMovementMethod = LinkMovementMethod.getInstance();

        // get state and local election administration bodies
        VoterInfo voterInfo = ((VIPTabBarActivity) mActivity).getVoterInfo();
        // should have only one state returned for addresses in the US
        State thisState = voterInfo.state.get(0);
        stateAdmin = thisState.electionAdministrationBody;
        if (thisState.local_jurisdiction != null && thisState.local_jurisdiction.electionAdministrationBody != null ) {
            localAdmin = thisState.local_jurisdiction.electionAdministrationBody;
            Log.d("ElectionDetailsFragment", "Got local election admin body " + localAdmin.name);
            if (stateAdmin == null) {
                // no state admin body; hide button bar and just show local
                setContents(localAdmin);
                View btnBar = mActivity.findViewById(R.id.details_button_bar);
                btnBar.setVisibility(View.GONE);
            } else {
                // have both; show state by default
                setContents(stateAdmin);
            }
        } else {
            // have no local admin body
            if (stateAdmin != null) {
                // hide button bar and just show state
                setContents(stateAdmin);
                View btnBar = mActivity.findViewById(R.id.details_button_bar);
                btnBar.setVisibility(View.GONE);
            } else {
                // have neither admin body; hide everything else and show "no info" message
                View btnBar = mActivity.findViewById(R.id.details_button_bar);
                btnBar.setVisibility(View.GONE);
                View detailsView = mActivity.findViewById(R.id.details_admin_body_table);
                detailsView.setVisibility(View.GONE);
                View noneMsg = mActivity.findViewById(R.id.details_none_found);
                noneMsg.setVisibility(View.VISIBLE);
            }

        }

        // set up button bar
        if (stateAdmin != null && localAdmin != null) {
            unselectedButtonTextColor = resources.getColor(R.color.button_blue);
            selectedButtonTextColor = resources.getColor(R.color.white);

            // highlight default button
            Button stateButton = (Button) mActivity.findViewById(R.id.details_state_button);
            stateButton.setTextColor(selectedButtonTextColor);
            stateButton.setBackgroundResource(R.drawable.button_bar_button_selected);
            lastSelectedButton = stateButton;
            lastSelectedButtonId = R.id.details_state_button;

            // add click handlers for button bar filter buttons
            setButtonInBarClickListener(R.id.details_state_button);
            setButtonInBarClickListener(R.id.details_local_button);
        }

        // set expandable section header click listeners
        selectedSectionBackground = R.drawable.details_toggle_selected;
        unselectedSectionBackground = R.drawable.details_toggle_unselected;

        for (List<Integer>detail : detailSections) {
            setSectionClickListener(detail.get(0), detail.get(1), detail.get(2), detail.get(3));
        }
    }

    /**
     * Helper function to set click listeners for expandable section headers.
     *
     * @param sectionHeaderId R id of section header (button)
     * @param sectionId R id of sub-section to show/hide on click
     */
    private void setSectionClickListener(int sectionHeaderId, final int sectionId,
                                         final int unselectedIcon, final int selectedIcon) {

        mActivity.findViewById(sectionHeaderId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btn = (Button) view;
                View section = mActivity.findViewById(sectionId);

                if (section.getVisibility() == View.GONE) {
                    section.setVisibility(View.VISIBLE);
                    btn.setBackgroundResource(selectedSectionBackground);
                    btn.setCompoundDrawablesWithIntrinsicBounds(selectedIcon, 0, 0, 0);
                } else {
                    section.setVisibility(View.GONE);
                    btn.setBackgroundResource(unselectedSectionBackground);
                    btn.setCompoundDrawablesWithIntrinsicBounds(unselectedIcon, 0, 0, 0);
                }
            }
        });
    }

    /**
     * Helper function to hide all collapsible subsections when switching between election bodies
     */
    private void collapseAllSubSections() {
        for (List<Integer>detail : detailSections) {
            View subsection = mActivity.findViewById(detail.get(1));
            subsection.setVisibility(View.GONE);
        }
    }

    /**
     * Helper function to set click handlers for the election body selection buttons
     * @param buttonId R id of the button to listen to
     */
    private void setButtonInBarClickListener(final int buttonId) {

        mActivity.findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonId == lastSelectedButtonId) {
                    return; // ignore button click if already viewing that list
                }

                Button btn = (Button)v;

                // highlight current selection (and un-highlight others)
                btn.setBackgroundResource(R.drawable.button_bar_button_selected);
                btn.setTextColor(selectedButtonTextColor);
                lastSelectedButton.setTextColor(unselectedButtonTextColor);
                lastSelectedButton.setBackgroundResource(R.drawable.button_bar_button);


                if (buttonId == R.id.details_state_button) {
                    setContents(stateAdmin);
                } else {
                    // local jurisdiction
                    setContents(localAdmin);
                }

                collapseAllSubSections();
                lastSelectedButtonId = buttonId;
                lastSelectedButton = btn;
            }
        });
    }

    /** Helper function to populate the administrative body table values.
     *
     * @param body Administration body to show in the view contents
     */
    private void setContents(ElectionAdministrationBody body) {
        try {
            // set header with administrative body name
            setTextView(R.id.details_admin_body_name, R.id.details_admin_body_name, body.name);

            // set fields that are links
            setLink(R.id.details_election_info_url_label, R.id.details_election_info_url_row, body.electionInfoUrl);
            setLink(R.id.details_registration_url_label, R.id.details_registration_url_row, body.electionRegistrationUrl);
            setLink(R.id.details_registration_confirmation_url_label, R.id.details_registration_confirmation_url_row, body.electionRegistrationConfirmationUrl);
            setLink(R.id.details_absentee_url_label, R.id.details_absentee_url_row, body.absenteeVotingInfoUrl);
            setLink(R.id.details_location_finder_url_label, R.id.details_location_finder_url_row, body.votingLocationFinderUrl);
            setLink(R.id.details_ballot_info_url_label, R.id.details_ballot_info_url_row, body.ballotInfoUrl);
            setLink(R.id.details_election_rules_url_label, R.id.details_election_rules_url_row, body.electionRulesUrl);

            // set non-link field values
            setTextView(R.id.details_voter_services, R.id.details_voter_services_section_header, body.getVoterServices());
            setTextView(R.id.details_hours_of_operation, R.id.details_hours_of_operation_section_header, body.hoursOfOperation);
            setTextView(R.id.details_correspondence_address, R.id.details_correspondence_address_section_header, body.getCorrespondenceAddress());
            setTextView(R.id.details_physical_address, R.id.details_physical_address_section_header, body.getPhysicalAddress());
            setTextView(R.id.details_election_officials, R.id.details_election_officials_section_header, body.getElectionOfficials());
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
        View container = mActivity.findViewById(containerId);
        if (val != null && !val.isEmpty()) {
            String label = textView.getText().toString();
            // make links look like links, in case they don't already;
            // otherwise Android won't know what Intent type to open.
            if (!val.startsWith("http")) {
                val = "http://" + val;
            }
            textView.setText(Html.fromHtml("<a href=\"" + val + "\">" + label + "</a>"));
            textView.setMovementMethod(mLinkMovementMethod);
            container.setVisibility(View.VISIBLE);
        } else {
            container.setVisibility(View.GONE);
        }
    }

    /**
     * Helper function to set a TextView to a string, or hide the TextView's containing view,
     * if the string is null or empty.
     *
     * @param textViewId R id of the TextView to set
     * @param containerId R id of the TextView's parent view, to hide if value is missing
     * @param val String to put in the TextView
     */
    private void setTextView(int textViewId, int containerId, String val) {
        TextView textView = (TextView) mActivity.findViewById(textViewId);
        View container = mActivity.findViewById(containerId);
        if (val != null && !val.isEmpty()) {
            textView.setText(val);
            container.setVisibility(View.VISIBLE);
        } else {
            container.setVisibility(View.GONE);
        }
    }

}
