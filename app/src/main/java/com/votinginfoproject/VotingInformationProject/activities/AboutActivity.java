package com.votinginfoproject.VotingInformationProject.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.votinginfoproject.VotingInformationProject.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // add required attribution for Google Maps, prepended with app license
        TextView legalNoticesView = (TextView)this.findViewById(R.id.about_legal_notices);
        String legalNotice = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this);
        String license = this.getResources().getString(R.string.about_license);
        legalNoticesView.setText(license + legalNotice);
    }

}
