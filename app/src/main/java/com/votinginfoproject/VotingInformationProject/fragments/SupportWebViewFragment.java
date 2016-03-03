package com.votinginfoproject.VotingInformationProject.fragments;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.singletons.UserPreferences;

import org.apache.http.util.EncodingUtils;

/**
 * This web view fragment is based on the Android library web view fragment,
 * but made to extend the support library version of Fragment, to be compatible with
 * the rest of the app.  Also, this fragment handles loading the feedback form via POST
 * and sets/unsets the text view for loading status.
 */
public class SupportWebViewFragment extends Fragment {
    private static final String PARENT_VIEW = "parent_view";
    private final String TAG = SupportWebViewFragment.class.getSimpleName();
    private WebView mWebView;
    private boolean mIsWebViewAvailable;
    private VIPTabBarActivity myActivity;
    private Resources res;
    private int parentViewId;

    public SupportWebViewFragment() {
    }

    public static SupportWebViewFragment newInstance(int parent_view) {
        SupportWebViewFragment fragment = new SupportWebViewFragment();
        Bundle args = new Bundle();
        args.putInt(PARENT_VIEW, parent_view);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            parentViewId = getArguments().getInt(PARENT_VIEW);
            Log.d(TAG, "Got parent view ID #" + parentViewId);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Called to instantiate the view. Creates and returns the WebView.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        res = getResources();
        myActivity = (VIPTabBarActivity) getActivity();
        myActivity.setLoadingFeedBackForm(true);

        Log.d(TAG + ":onCreateView", "Setting up new WebView fragment");

        // web view takes a few seconds to load, so show a message
        setTextView(res.getString(R.string.feedback_loading));

        if (mWebView != null) {
            mWebView.destroy();
        }

        mWebView = new WebView(myActivity);
        mIsWebViewAvailable = true;

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    Log.d(TAG, "Done loading feedback form.");
                    // set the text back from the loading message when done loading
                    setTextView(res.getString(R.string.feedback_link));
                }
            }
        });

        // build the POST
        Uri.Builder builder = new Uri.Builder();
        VoterInfo info = UserPreferences.getVoterInfo();
        builder.appendQueryParameter("electionId", info.election.getId());
        builder.appendQueryParameter("address", info.normalizedInput.toGeocodeString());

        // strip leading question mark from parameters
        String paramString = builder.build().toString().substring(1);
        byte[] post = EncodingUtils.getBytes(paramString, "BASE64");

        mWebView.postUrl("https://voter-info-tool.appspot.com/feedback", post);

        return mWebView;
    }

    private void setTextView(String txt) {
        View parent = myActivity.findViewById(parentViewId);

        TextView tv;
        if (parent == null) {
            // in case of a referendum, structure is different (not in a list)
            parent = myActivity.findViewById(R.id.contest_referendum_scrollview);
            if (parent == null) {
                // ...or in case of election details
                parent = myActivity.findViewById(R.id.election_details_fragment);
            }
        }

        tv = ((TextView) parent.findViewById(R.id.feedback_footer));
        tv.setText(txt);
    }

    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @Override
    public void onPause() {
        super.onPause();

        mWebView.onPause();
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @Override
    public void onResume() {
        mWebView.onResume();
        myActivity.setLoadingFeedBackForm(true);

        super.onResume();
    }

    /**
     * Called when the WebView has been detached from the fragment.
     * The WebView is no longer available after this time.
     */
    @Override
    public void onDestroyView() {
        mIsWebViewAvailable = false;
        myActivity.setLoadingFeedBackForm(false);

        super.onDestroyView();
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        myActivity.setLoadingFeedBackForm(false);

        super.onDestroy();
    }

    /**
     * Gets the WebView.
     */
    public WebView getWebView() {
        return mIsWebViewAvailable ? mWebView : null;
    }
}
