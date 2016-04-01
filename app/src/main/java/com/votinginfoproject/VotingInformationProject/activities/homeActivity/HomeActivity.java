package com.votinginfoproject.VotingInformationProject.activities.homeActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.AboutActivity;
import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;
import com.votinginfoproject.VotingInformationProject.adapters.HomePickerAdapter;
import com.votinginfoproject.VotingInformationProject.fragments.HomeFragment;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.singletons.GATracker;
import com.votinginfoproject.VotingInformationProject.models.singletons.UserPreferences;

import java.util.ArrayList;


public class HomeActivity extends FragmentActivity implements HomeView, HomeFragment.OnInteractionListener {

    private final String TAG = HomeActivity.class.getSimpleName();

    private String selectedParty;

    private HomePresenter mPresenter;

    private Button mGoButton;
    private EditText mAddressEditText;
    private TextView mStatusTextView;
    private View mElectionSelectorWrapper;
    private View mPartySelectorWrapper;
    private TextView mElectionsTextView;
    private TextView mPartyTextView;
    private ImageButton mContactsButton;
    private ImageButton mAboutButton;

    public String getSelectedParty() {
        return selectedParty;
    }

    public void setSelectedParty(String selectedParty) {
        this.selectedParty = selectedParty;

        UserPreferences.setSelectedParty(selectedParty);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        this.mPresenter = new HomePresenterImpl(getBaseContext(), this);

        mGoButton = (Button) findViewById(R.id.home_go_button);
        mAddressEditText = (EditText) findViewById(R.id.home_edittext_address);
        mStatusTextView = (TextView) findViewById(R.id.home_textview_status);
        mElectionSelectorWrapper = findViewById(R.id.home_election_selector_wrapper);
        mPartySelectorWrapper = findViewById(R.id.home_party_selector_wrapper);

        mElectionsTextView = (TextView) findViewById(R.id.home_election_text);
        mPartyTextView = (TextView) findViewById(R.id.home_party_text);

        mContactsButton = (ImageButton) findViewById(R.id.home_select_contact_button);
        mAboutButton = (ImageButton) findViewById(R.id.home_about_us_button);

        selectedParty = "";

        setupViewListeners();

        // Get analytics tracker (should auto-report)
        GATracker.getTracker(GATracker.TrackerName.APP_TRACKER);
    }

    private void setupViewListeners() {
        // Go Button onClick Listener
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.goButtonClicked();
            }
        });

        // About Us Button onClickListener
        mAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.aboutButtonClicked();
            }
        });

        // EditText image button onClick listener
        mContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.contactsButtonClicked();
            }
        });

        // EditText onSearch Listener
        mAddressEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    mPresenter.searchButtonClicked(mAddressEditText.getText().toString());
                }

                // Return false to close the keyboard
                return false;
            }
        });

        // election spinner listener
        mElectionsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.electionTextViewClicked();
            }
        });

        mPartyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.partyTextViewClicked();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Stop analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Get an Analytics tracker to report app starts, uncaught exceptions, etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void onGoButtonPressed(View view) {
        mPresenter.goButtonClicked();
    }

    @Override
    public void onAboutUsButtonPressed() {
        // Navigate to About us
        mPresenter.aboutButtonClicked();
    }

    public void onSelectContactButtonPressed(View view) {
        // contact picker intent to return an address; will only show contacts that have an address
        mPresenter.contactsButtonClicked();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                // Get the URI that points to the selected contact
                mPresenter.contactSelected(getLoaderManager(), data.getData());
            } else {
                // PASS (user didn't pick an address)
            }
        }
    }

    public void searchedAddress(VoterInfo voterInfo) {
        // set VoterInfo object on app singleton
        if (voterInfo == null) {
            mPresenter.selectedParty(0);
            mPresenter.selectedElection(0);
            UserPreferences.setVoterInfo(null);
        } else {
            UserPreferences.setSelectedParty(selectedParty);
            UserPreferences.setVoterInfo(voterInfo);
        }
    }

    @Override
    public void navigateToContactsActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    @Override
    public void navigateToAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void navigateToVIPResultsActivity(VoterInfo voterInfo) {
        Intent intent = new Intent(this, VIPTabBarActivity.class);
        startActivity(intent);
    }

    @Override
    public void showElectionPicker() {
        mElectionSelectorWrapper.setVisibility(View.VISIBLE);
    }

    @Override
    public void setElectionText(String electionText) {
        mElectionsTextView.setText(electionText);
    }

    @Override
    public void hideElectionPicker() {
        mElectionSelectorWrapper.setVisibility(View.GONE);
    }

    @Override
    public void displayElectionPickerWithItems(ArrayList<String> elections, int selected) {
        //Build Alert dialog for election picker
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        HomePickerAdapter adapter = new HomePickerAdapter(this, android.R.layout.simple_selectable_list_item);
        adapter.addAll(elections);

        adapter.highlightItemAtIndex(selected);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                mPresenter.selectedElection(which);
            }
        });

        builder.show();
    }

    @Override
    public void showPartyPicker() {
        mPartySelectorWrapper.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPartyText(String partyText) {
        mPartyTextView.setText(partyText);
    }

    @Override
    public void displayPartyPickerWithItems(ArrayList<String> parties, int selected) {
        //Build Alert dialog for party picker
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        HomePickerAdapter adapter = new HomePickerAdapter(this, android.R.layout.simple_selectable_list_item);
        adapter.addAll(parties);

        adapter.highlightItemAtIndex(selected);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                mPresenter.selectedParty(which);
            }
        });

        builder.show();
    }

    @Override
    public void hidePartyPicker() {
        mPartySelectorWrapper.setVisibility(View.GONE);
    }

    @Override
    public void showGoButton() {
        mGoButton.setVisibility(View.VISIBLE);
        mGoButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
    }

    @Override
    public void hideGoButton() {
        mGoButton.setVisibility(View.GONE);
    }

    @Override
    public void overrideSearchAddress(String searchAddress) {
        mAddressEditText.setText(searchAddress);
    }

    @Override
    public void showMessage(String message) {
        mStatusTextView.setText(message);

        mStatusTextView.setVisibility(View.VISIBLE);
        mStatusTextView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
    }

    @Override
    public void hideStatusView() {
        mStatusTextView.setVisibility(View.GONE);
    }
}
