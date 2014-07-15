package com.votinginfoproject.VotingInformationProject.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.fragments.HomeFragment;
import com.votinginfoproject.VotingInformationProject.models.CivicInfoApiQuery;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.ElectionQueryResponse;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.State;

public class HomeActivity extends FragmentActivity implements HomeFragment.OnInteractionListener {

    TextView testText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onGoButtonPressed(View view) {
        Intent intent = new Intent(this, VIPTabBarActivity.class);
        startActivity(intent);
    }

    //////////////////////////////////////////////////////////////////
    /* TODO: remove test queries below */
    public void testElectionQuery(View view) {
        testText = (TextView) findViewById(R.id.testTextView);
        testText.setText("Fetching data...");
        Context myContext = view.getContext();
        String apiUrl = "elections?key=";
        CivicInfoApiQuery.CallBackListener myListener = new electionsCallback();
        new CivicInfoApiQuery<ElectionQueryResponse>(myContext, ElectionQueryResponse.class, myListener).execute(apiUrl);
    }

    public void testVoterInfoQuery(View view) {
        testText = (TextView) findViewById(R.id.testTextView);
        testText.setText("Fetching data...");
        Context myContext = view.getContext();
        String apiUrl = "voterinfo?address=2121%20I%20St%20NWWashington,%20DC&electionId=4043&key=";
        CivicInfoApiQuery.CallBackListener voterInfoListener = new voterInfoCallback();
        new CivicInfoApiQuery<VoterInfo>(myContext, VoterInfo.class, voterInfoListener).execute(apiUrl);
    }

    public class electionsCallback implements CivicInfoApiQuery.CallBackListener {
        public void callback(Object obj) {
            ElectionQueryResponse qryResult = (ElectionQueryResponse) obj;
            String show = "Elections:\n\n";
            for (Election el : qryResult.elections) {
                show += el.id + ": " + el.name + "\n" + el.electionDay + "\n\n";
            }
            testText.setText(show);
        }
    }

    public class voterInfoCallback implements CivicInfoApiQuery.CallBackListener {
        public void callback(Object obj) {
            VoterInfo qryResult = (VoterInfo) obj;
            Election el = qryResult.election;
            String show = "Election:\n" + el.id + ": " + el.name + "\n" + el.electionDay + "\n\n";
            State state = qryResult.state.get(0);
            show += "State: " + state.name + "\n";
            show += "Election admin: " + state.electionAdministrationBody.name + "\n\n";
            show += "Sources:\n" + state.sources.get(0).name;
            testText.setText(show);
        }
    }
    //////////////////////////////////////////////////////
}
