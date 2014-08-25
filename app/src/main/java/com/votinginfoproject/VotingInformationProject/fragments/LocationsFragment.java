package com.votinginfoproject.VotingInformationProject.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;
import com.votinginfoproject.VotingInformationProject.adapters.LocationsAdapter;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.util.ArrayList;
import java.util.List;


public class LocationsFragment extends Fragment {

    VoterInfo voterInfo;
    LocationsAdapter listAdapter;
    View rootView;
    TextView noneFoundMessage;
    ListView locationsList;
    ArrayList<PollingLocation> allLocations;

    int selectedButtonTextColor;
    int unselectedButtonTextColor;

    // track which location filter button was last clicked, and only refresh list if it changed
    int lastSelectedButtonId = R.id.locations_list_all_button;
    Button lastSelectedButton;

    public static LocationsFragment newInstance() {
        return new LocationsFragment();
    }
    public LocationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_locations, container, false);
        final VIPTabBarActivity myActivity = (VIPTabBarActivity)this.getActivity();

        Resources res = myActivity.getResources();
        unselectedButtonTextColor = res.getColor(R.color.button_blue);
        selectedButtonTextColor = res.getColor(R.color.white);

        // election labels
        TextView election_name_label = (TextView)rootView.findViewById(R.id.locations_election_title);
        TextView election_date_label = (TextView)rootView.findViewById(R.id.locations_election_subtitle);
        election_name_label.setText(voterInfo.election.name);
        election_date_label.setText(voterInfo.election.getFormattedDate());

        // find label for empty results
        noneFoundMessage = (TextView) rootView.findViewById(R.id.locations_none_found_message);

        // highlight default button
        Button allButton = (Button)rootView.findViewById(R.id.locations_list_all_button);
        allButton.setTextColor(selectedButtonTextColor);
        allButton.setBackgroundResource(R.drawable.button_bar_button_selected);
        lastSelectedButton = allButton;

        // add click handlers for button bar filter buttons
        setButtonInBarClickListener(R.id.locations_list_all_button);
        setButtonInBarClickListener(R.id.locations_list_polling_button);
        setButtonInBarClickListener(R.id.locations_list_early_button);

        // clear directions polyline, if set from directions view
        myActivity.polylineCallback("", null);

        // click handler for map button
        rootView.findViewById(R.id.locations_list_map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // zoom to user address
                myActivity.showMap("home");
            }
        });

        // set up locations list
        locationsList = (ListView)rootView.findViewById(R.id.locations_list);
        locationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get location ID tagged onto its distance label in the list adapter
                String itemTag = view.findViewById(R.id.location_list_item_distance).getTag().toString();
                // open directions view on list item tap (will open map view on item button tap)
                Log.d("LocationsFragment", "Clicked list item " + itemTag);
                myActivity.showDirections(itemTag);
            }
        });

        // initialize list adapter with all locations
        allLocations = myActivity.getAllLocations();
        // copy the list, so the original isn't destroyed by the adapter construction
        //noinspection unchecked
        listAdapter = new LocationsAdapter(myActivity, (ArrayList<PollingLocation>) allLocations.clone());
        locationsList.setAdapter(listAdapter);

        if (allLocations.isEmpty()) {
            locationsList.setVisibility(View.GONE);
            noneFoundMessage.setText(R.string.locations_none_found);
            noneFoundMessage.setVisibility(View.VISIBLE);
        } else {
            listAdapter.sortList();
        }

        return rootView;
    }

    /**
     * Helper function to set click handlers for the list filter buttons
     * @param buttonId R id of the button to listen to
     */
    private void setButtonInBarClickListener(final int buttonId) {

        rootView.findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
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


                if (buttonId == R.id.locations_list_polling_button) {
                    setFilter(voterInfo.pollingLocations, R.string.locations_no_polling_found);
                } else if (buttonId == R.id.locations_list_early_button) {
                    setFilter(voterInfo.earlyVoteSites, R.string.locations_no_early_voting_found);
                } else {
                    setFilter(allLocations, R.string.locations_none_found);
                }

                lastSelectedButtonId = buttonId;
                lastSelectedButton = btn;
            }
        });
    }

    /**
     * Helper function to set locations list when filter button clicked
     * @param locations PollingLocation objects to put in the list
     * @param empty_message String message to show if there are no locations to display
     */
    private void setFilter(List<PollingLocation> locations, int empty_message) {
        if (locations != null && !locations.isEmpty()) {
            listAdapter.clear();
            listAdapter.addAll(locations);
            listAdapter.sortList();
            locationsList.setVisibility(View.VISIBLE);
            noneFoundMessage.setVisibility(View.GONE);
        } else {
            locationsList.setVisibility(View.GONE);
            noneFoundMessage.setText(empty_message);
            noneFoundMessage.setVisibility(View.VISIBLE);
        }
    }

    public void refreshList() {
        if (listAdapter != null) {
            listAdapter.sortList();  // sorting the list will also refresh it
            locationsList.invalidate();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // get election info
        voterInfo = ((VIPTabBarActivity) activity).getVoterInfo();
    }

}
