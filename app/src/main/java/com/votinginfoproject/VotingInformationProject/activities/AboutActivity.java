package com.votinginfoproject.VotingInformationProject.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.asynctasks.LoadOpenSourceLicense;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // asynchronously add required attribution for Google Maps, prepended with app license
        TextView legalNoticesView = (TextView) this.findViewById(R.id.about_legal_notices);
        new LoadOpenSourceLicense(this).execute(legalNoticesView);
    }
}
