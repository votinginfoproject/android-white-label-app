package com.votinginfoproject.VotingInformationProject.activities.reportErrorActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.votinginfoproject.VotingInformationProject.R;

/**
 * Created by max on 5/3/16.
 */
public class ReportErrorPresenterImpl extends ReportErrorPresenter implements Parcelable {
    private static final String TAG = ReportErrorPresenterImpl.class.getSimpleName();

    private static final String KEY_HOME_ADDRESS = "home_address";
    private static final String KEY_ELECTION_ID = "election_id";

    private final String mFeedbackURL;
    private String mElectionID;
    private String mHomeAddress;

    public static Creator<ReportErrorPresenterImpl> CREATOR = new Creator<ReportErrorPresenterImpl>() {
        @Override
        public ReportErrorPresenterImpl createFromParcel(Parcel source) {
            return new ReportErrorPresenterImpl(source);
        }

        @Override
        public ReportErrorPresenterImpl[] newArray(int size) {
            return new ReportErrorPresenterImpl[size];
        }
    };

    public ReportErrorPresenterImpl(Context context, String electionID, String homeAddress) {
        mElectionID = electionID;
        mHomeAddress = homeAddress;

        mFeedbackURL = context.getString(R.string.feedback_url);
    }

    private ReportErrorPresenterImpl(Parcel source) {
        mFeedbackURL = source.readString();
        mElectionID = source.readString();
        mHomeAddress = source.readString();
    }

    @Override
    public void onCreate(Bundle savedState) {
        if (savedState != null) {
            mElectionID = savedState.getString(KEY_ELECTION_ID);

            mHomeAddress = savedState.getString(KEY_HOME_ADDRESS);
        }
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        state.putString(KEY_ELECTION_ID, mElectionID);

        state.putString(KEY_HOME_ADDRESS, mHomeAddress);
    }

    @Override
    public void onDestroy() {
        setView(null);
    }

    @Override
    public void onAttachView(ReportErrorView view) {
        super.onAttachView(view);

        getView().toggleLoading(true);

        postData();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFeedbackURL);
        dest.writeString(mElectionID);
        dest.writeString(mHomeAddress);
    }

    @Override
    void webViewFinishedLoading() {
        if (getView() != null) {
            getView().toggleLoading(false);
        }
    }

    private void postData() {
        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter("electionId", mElectionID);
        builder.appendQueryParameter("address", mHomeAddress);

        // strip leading question mark from parameters
        String paramString = builder.build().toString().substring(1);
        byte[] post = paramString.getBytes();

        getView().postUrl(mFeedbackURL, post);
    }
}
