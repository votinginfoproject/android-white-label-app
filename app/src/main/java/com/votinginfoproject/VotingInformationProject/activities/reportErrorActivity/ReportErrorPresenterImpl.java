package com.votinginfoproject.VotingInformationProject.activities.reportErrorActivity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by max on 5/3/16.
 */
public class ReportErrorPresenterImpl implements Parcelable {
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

    private ReportErrorPresenterImpl(Parcel source) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
