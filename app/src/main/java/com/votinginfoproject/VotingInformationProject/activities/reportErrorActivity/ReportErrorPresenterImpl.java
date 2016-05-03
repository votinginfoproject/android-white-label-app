package com.votinginfoproject.VotingInformationProject.activities.reportErrorActivity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by max on 5/3/16.
 */
public class ReportErrorPresenterImpl extends ReportErrorPresenter implements Parcelable {

    @Override
    public void onCreate(Bundle savedState) {
        
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {

    }

    @Override
    public void onDestroy() {

    }

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

    }

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
