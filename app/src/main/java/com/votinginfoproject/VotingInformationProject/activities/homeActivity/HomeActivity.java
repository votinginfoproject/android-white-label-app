package com.votinginfoproject.VotingInformationProject.activities.homeActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
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
import com.google.gson.Gson;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.BaseActivity;
import com.votinginfoproject.VotingInformationProject.activities.aboutActivity.AboutVIPActivity;
import com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity.VoterInformationActivity;
import com.votinginfoproject.VotingInformationProject.adapters.HomePickerAdapter;
import com.votinginfoproject.VotingInformationProject.constants.ExtraConstants;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.singletons.GATracker;

import java.util.ArrayList;


public class HomeActivity extends BaseActivity<HomePresenter> implements HomeView {

    private final String TAG = HomeActivity.class.getSimpleName();

    private Button mGoButton;
    private EditText mAddressEditText;
    private TextView mStatusTextView;
    private View mElectionSelectorWrapper;
    private View mPartySelectorWrapper;
    private TextView mElectionsTextView;
    private TextView mPartyTextView;
    private ImageButton mAboutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        if (getPresenter() == null) {
            setPresenter(new HomePresenterImpl(getBaseContext()));
        }

        getPresenter().onCreate(savedInstanceState);

        mGoButton = (Button) findViewById(R.id.home_button_go);
        mAddressEditText = (EditText) findViewById(R.id.home_edit_text_address);
        mStatusTextView = (TextView) findViewById(R.id.home_label_status);
        mElectionSelectorWrapper = findViewById(R.id.home_container_election_selector);
        mPartySelectorWrapper = findViewById(R.id.home_container_party_selector);

        mElectionsTextView = (TextView) findViewById(R.id.home_selector_election);
        mPartyTextView = (TextView) findViewById(R.id.home_selector_party);

        mAboutButton = (ImageButton) findViewById(R.id.home_button_about_us);

        setupViewListeners();

        // Get analytics tracker (should auto-report)
        GATracker.getTracker(GATracker.TrackerName.APP_TRACKER);
    }

    private void setupViewListeners() {
        // Go Button onClick Listener
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPresenter().goButtonClicked();
            }
        });

        // About Us Button onClickListener
        mAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPresenter().aboutButtonClicked();
            }
        });

        // EditText onSearch Listener
        mAddressEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    getPresenter().searchButtonClicked(getBaseContext(), mAddressEditText.getText().toString());
                }

                // Return false to close the keyboard
                return false;
            }
        });

        // election spinner listener
        mElectionsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().electionTextViewClicked();
            }
        });

        mPartyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().partyTextViewClicked();
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

    @Override
    public void navigateToAboutActivity() {
        Intent intent = new Intent(this, AboutVIPActivity.class);
        startActivity(intent);
    }

    @Override
    public void navigateToVoterInformationActivity(VoterInfo voterInfo, String filter) {
        Intent intent = new Intent(this, VoterInformationActivity.class);

        intent.putExtra(ExtraConstants.VOTER_INFO, new Gson().toJson(voterInfo));
        intent.putExtra(ExtraConstants.PARTY_FILTER, filter);

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

                getPresenter().selectedElection(getBaseContext(), mAddressEditText.getText().toString(), which);
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

                getPresenter().selectedParty(which);
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
    public void showMessage(@StringRes int message) {
        String stringMessage = getString(message);

        showMessage(stringMessage);
    }

    @Override
    public void hideStatusView() {
        mStatusTextView.setVisibility(View.GONE);
    }
}
