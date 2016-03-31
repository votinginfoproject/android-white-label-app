package com.votinginfoproject.VotingInformationProject.models.api.requests;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.votinginfoproject.VotingInformationProject.R;

/**
 * Created by marcvandehey on 3/22/16.
 */
public class CivicInfoRequest implements RequestType {
    private static final String TAG = CivicInfoRequest.class.getSimpleName();

    private boolean officialOnly;
    private boolean preproductionData;
    private String electionId;
    private String address;
    private String browserKey;
    private String apiVersion;

    /**
     * Creates a CivicInfoRequest request body for the CivicInfoInteractor to consume
     *
     * @param electionId
     * @param address
     */
    public CivicInfoRequest(@NonNull Context context, @Nullable String electionId, @NonNull String address) {
        this.electionId = electionId;
        this.address = address;

        /**
         * Make sure the API keys have been added to the project
         * Check out the "Adding API keys for the app" section of the readme for more details
         */
        this.browserKey = context.getResources().getString(R.string.google_api_browser_key);
        this.officialOnly = context.getResources().getBoolean(R.bool.civic_info_official_only);
        this.preproductionData = context.getResources().getBoolean(R.bool.use_preproduction);
        this.apiVersion = context.getResources().getString(R.string.civic_info_api_version);
    }

    public String getAddress() {
        return address;
    }

    public String buildQueryString() {
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https").authority(getAuthorityString()).appendPath("civicinfo");
            builder.appendPath(apiVersion);
            builder.appendPath("voterinfo");
            builder.appendQueryParameter("officialOnly", String.valueOf(officialOnly));

            if (preproductionData) {
                builder.appendQueryParameter("productionDataOnly", "false");
            }

            if (electionId != null && !electionId.isEmpty()) {
                builder.appendQueryParameter("electionId", electionId);
            }

            builder.appendQueryParameter("address", address);

            if (browserKey == null || browserKey.isEmpty()) {
                Log.e(TAG, "----------------------- Google Civic Browser Key is not set -----------------------");
                Log.e(TAG, "Please reference the \"Adding API keys for the app\" section of the Readme for more details.");
                Log.e(TAG, "----------------------- Google Civic Browser Key is not set -----------------------");
            }

            builder.appendQueryParameter("key", browserKey);
            String apiUrl = builder.build().toString();

            Log.d(TAG, "searchedAddress: " + apiUrl);

            return builder.build().toString();
        } catch (Exception e) {
            Log.e(TAG, "There was an error building Uri. " + e.getLocalizedMessage());
        }

        return "";
    }

    public String getAuthorityString() {
        return "www.googleapis.com";
    }
}
