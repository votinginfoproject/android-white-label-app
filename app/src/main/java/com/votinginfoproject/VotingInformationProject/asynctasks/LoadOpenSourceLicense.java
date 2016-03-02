package com.votinginfoproject.VotingInformationProject.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;

import java.lang.ref.WeakReference;

/**
 * Load the open source license text for Google Maps attribution, and set it in the About text field.
 * Doing this asynchronously because adding it to the view directly causes it to take several
 * seconds to load.
 * <p/>
 * Created by kathrynkillebrew on 8/29/14.
 */
public class LoadOpenSourceLicense extends AsyncTask<TextView, Void, String> {
    private WeakReference<TextView> textView = null;
    private Context mContext;

    public LoadOpenSourceLicense(Context context) {
        super();

        mContext = context;
    }

    protected String doInBackground(TextView... textViews) {
        try {
            this.textView = new WeakReference(textViews[0]);
            // append the text to the end of what's already there (the app license)
            StringBuilder builder = new StringBuilder(textViews[0].getText());
            builder.append(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(mContext));

            return builder.toString();
        } catch (Exception ex) {
            Log.e("LoadOpenSourceLicense", "Failed to asynchronously fetch Google Play services attribution");
            ex.printStackTrace();
        }

        return "";
    }


    @Override
    protected void onPostExecute(String text) {
        if (textView != null) {
            // get weakly referenced text view
            TextView view = textView.get();
            if (view != null && !text.isEmpty()) {
                Log.d("LoadOpenSourceLicense:onPostExecute", "Setting text in view.");
                view.setText(text);
            } else {
                Log.e("LoadOpenSourceLicense:onPostExecute", "Missing either view or text to set");
            }
        } else {
            Log.e("LoadOpenSourceLicense:onPostExecute", "No TextView found to give the text.");
        }
    }
}
