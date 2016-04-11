package com.votinginfoproject.VotingInformationProject.models.api.interactors;

import android.content.Context;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.votinginfoproject.VotingInformationProject.models.api.requests.RequestType;

/**
    Created by maxwade on 4/8/16.
 */

public class OpenSourceLicenseInteractor extends BaseInteractor<String, OpenSourceLicenseInteractor.OpenSourceLicenceCallback> {
    private static final String TAG = OpenSourceLicenseInteractor.class.getSimpleName();

    private Context mContext;

    public OpenSourceLicenseInteractor(Context context) {
        super();

        mContext = context;
    }

    protected String doInBackground(RequestType... params) {
        return GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(mContext);
    }

    protected  void onPostExecute(String licenseInfo) {
        OpenSourceLicenceCallback callback = getCallback();

        if (callback != null) {
            callback.openSourceLicenseResponse(licenseInfo);
        }
    }

    public interface OpenSourceLicenceCallback {
        void openSourceLicenseResponse(String licenseInfo);
    }
}
