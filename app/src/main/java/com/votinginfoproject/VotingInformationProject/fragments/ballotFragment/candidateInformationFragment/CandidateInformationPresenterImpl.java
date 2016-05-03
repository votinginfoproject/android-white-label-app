package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.candidateInformationFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Candidate;
import com.votinginfoproject.VotingInformationProject.models.SocialMediaChannel;

/**
 * Created by marcvandehey on 5/2/16.
 */
public class CandidateInformationPresenterImpl extends CandidateInformationPresenter {
    public static final Parcelable.Creator<CandidateInformationPresenterImpl> CREATOR = new Parcelable.Creator<CandidateInformationPresenterImpl>() {
        public CandidateInformationPresenterImpl createFromParcel(Parcel pc) {
            return new CandidateInformationPresenterImpl(pc);
        }

        public CandidateInformationPresenterImpl[] newArray(int size) {
            return new CandidateInformationPresenterImpl[size];
        }
    };

    private static final String TAG = CandidateInformationPresenterImpl.class.getSimpleName();
    private int websiteIndex = -1;
    private int phoneIndex = -1;
    private int emailIndex = -1;
    private int socialMediaHeaderIndex = -1;

    public CandidateInformationPresenterImpl() {
        //Empty Default Constructor
    }

    public CandidateInformationPresenterImpl(Parcel parcel) {
        Candidate candidate = parcel.readParcelable(Candidate.class.getClassLoader());

        setCandidate(candidate);
    }

    @Override
    public void onCreate(Bundle savedState) {
        //Not Implemented
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        //Not Implemented
    }

    @Override
    public void onDestroy() {
        //Not Implemented
    }

    @Override
    public DataHolder getDataForIndex(int index) {
        if (getView() == null) {
            return null;
        }

        Context context = getView().getContext();

        if (index == websiteIndex) {
            return new DataHolder(
                    getSectionTitleForIndex(index),
                    getCandidate().candidateUrl,
                    context.getString(R.string.candidate_details_website),
                    context.getString(R.string.accessibility_description_website),
                    R.drawable.ic_computer,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Navigate to URL
                        }
                    });
        } else if (index == phoneIndex) {
            return new DataHolder(
                    getSectionTitleForIndex(index),
                    getCandidate().phone,
                    context.getString(R.string.candidate_details_phone),
                    context.getString(R.string.accessibility_description_phone),
                    R.drawable.ic_phone,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Navigate to phone
                        }
                    });
        } else if (index == emailIndex) {
            return new DataHolder(
                    getSectionTitleForIndex(index),
                    getCandidate().email,
                    context.getString(R.string.candidate_details_email),
                    context.getString(R.string.accessibility_description_email),
                    R.drawable.ic_email,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Navigate to email
                        }
                    });
        } else if (index >= socialMediaHeaderIndex && index < getRowCount() - 1) {

            int modifiedIndex = index - socialMediaHeaderIndex;

            if (modifiedIndex >= 0 && getCandidate().channels.size() > modifiedIndex) {
                SocialMediaChannel channel = getCandidate().channels.get(modifiedIndex);

                return new DataHolder(
                        getSectionTitleForIndex(index),
                        channel.type,
                        null,//Do not show detail text here
                        channel.getCleanType(),
                        channel.getDrawable(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Navigate to URL
                            }
                        });
            }
        }

        return null;
    }

    @Override
    public Candidate getCandidate() {
        return super.getCandidate();
    }

    @Override
    public int getRowCount() {
        Candidate candidate = getCandidate();

        int rowCount = 1;//Added row for header

        if (candidate.candidateUrl != null) {
            websiteIndex = rowCount;
            rowCount++;
        }

        if (candidate.phone != null) {
            phoneIndex = rowCount;
            rowCount++;
        }

        if (candidate.email != null) {
            emailIndex = rowCount;
            rowCount++;
        }

        if (candidate.channels != null) {
            socialMediaHeaderIndex = rowCount;
            rowCount += candidate.channels.size();
        }

        rowCount++;//Added row for report button

        return rowCount;
    }

    @Override
    public String getSectionTitleForIndex(int index) {
        //If it is not the header, and there is at least one item of candidate details
        if (index == 1 && (websiteIndex > 0 || phoneIndex > 0 || emailIndex > 0)) {
            return getView().getContext().getString(R.string.candidate_details_header);
        } else if (index == socialMediaHeaderIndex) {
            return getView().getContext().getString(R.string.candidate_social_header);
        }

        return null;
    }

    @Override
    public int getViewTypeForIndex(int index) {
        if (index == 0) {
            return HEADER_VIEW_HOLDER;
        } else if (index < getRowCount() - 1) {
            return CANDIDATE_INFO_VIEW_HOLDER;
        } else {
            return REPORT_VIEW_HOLDER;
        }
    }

    @Override
    public void onReportErrorClicked() {
        //Navigate to report error
    }
}
