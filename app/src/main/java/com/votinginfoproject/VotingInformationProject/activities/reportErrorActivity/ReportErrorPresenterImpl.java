package com.votinginfoproject.VotingInformationProject.activities.reportErrorActivity;

import android.media.MediaCodecInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Xml;

import com.votinginfoproject.VotingInformationProject.models.VoterInfoResponse;
import com.votinginfoproject.VotingInformationProject.models.singletons.VoterInformation;

import java.nio.charset.StandardCharsets;

/**
 * Created by max on 5/3/16.
 */
public class ReportErrorPresenterImpl extends ReportErrorPresenter implements Parcelable {
    private static final String TAG = ReportErrorPresenterImpl.class.getSimpleName();

    private static final String FEEDBACK_URL = "https://voter-info-tool.appspot.com/feedback";

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

    public ReportErrorPresenterImpl() {
        //Required empty constructor
    }

    private ReportErrorPresenterImpl(Parcel source) {
        //Required parcel constructor
    }

    @Override
    public void onCreate(Bundle savedState) {
        //Not implemented
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        //Not implemented
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
        //Not implemented
    }

    @Override
    void webViewFinishedLoading() {
        if (getView() != null) {
            getView().toggleLoading(false);
        }
    }

    private void postData() {
        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter("electionId", VoterInformation.getElection().getId());
        builder.appendQueryParameter("address", VoterInformation.getHomeAddress().toGeocodeString());

        // strip leading question mark from parameters
        String paramString = builder.build().toString().substring(1);
        byte[] post = paramString.getBytes();

        getView().postUrl(FEEDBACK_URL, post);
    }
}
