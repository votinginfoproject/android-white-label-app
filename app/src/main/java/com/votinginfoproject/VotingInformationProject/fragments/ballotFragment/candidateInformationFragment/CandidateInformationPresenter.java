package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.candidateInformationFragment;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.view.View;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;
import com.votinginfoproject.VotingInformationProject.models.Candidate;

/**
 * Created by marcvandehey on 5/2/16.
 */
public abstract class CandidateInformationPresenter extends BasePresenter<CandidateInformationView> implements Parcelable {
    public static final int HEADER_VIEW_HOLDER = 0x0;
    public static final int CANDIDATE_INFO_VIEW_HOLDER = 0x1;
    public static final int REPORT_VIEW_HOLDER = 0x2;

    private Candidate candidate;

    public CandidateInformationPresenter() {
        //Empty Default Constructor
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public abstract DataHolder getDataForIndex(int index);

    public abstract int getRowCount();

    public abstract String getSectionTitleForIndex(int index);

    public abstract int getViewTypeForIndex(int index);

    public abstract void onReportErrorClicked();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(candidate, flags);
    }

    public class DataHolder {
        @DrawableRes
        public final int drawable;
        public final String title;
        public final String description;
        public final String accessibilityText;
        public final String sectionText;

        public final View.OnClickListener listener;

        public DataHolder(String sectionText, String title, String description, String accessibilityText, @DrawableRes int drawable, View.OnClickListener listener) {
            this.sectionText = sectionText;
            this.title = title;
            this.description = description;
            this.accessibilityText = accessibilityText;
            this.drawable = drawable;
            this.listener = listener;
        }
    }
}
