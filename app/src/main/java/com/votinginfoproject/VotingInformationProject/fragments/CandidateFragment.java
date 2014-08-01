package com.votinginfoproject.VotingInformationProject.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;
import com.votinginfoproject.VotingInformationProject.models.Candidate;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.SocialMediaChannel;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CandidateFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CandidateFragment extends Fragment {
    private static final String CONTEST_NUM = "contest_number";
    private static final String CANDIDATE_NUM = "candidate_number";
    private int contestNum;
    private int candidateNum;

    private VIPTabBarActivity mActivity;
    private VoterInfo voterInfo;
    private Contest contest;
    private Candidate candidate;

    private ViewGroup mContainer;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contestIndex Index of the contest in the contests list
     * @param candidateIndex Index of the candidate in the candidates list
     * @return A new instance of fragment CandidateFragment.
     */
    public static CandidateFragment newInstance(int contestIndex, int candidateIndex) {
        CandidateFragment fragment = new CandidateFragment();
        Bundle args = new Bundle();
        args.putInt(CONTEST_NUM, contestIndex);
        args.putInt(CANDIDATE_NUM, candidateIndex);
        fragment.setArguments(args);
        return fragment;
    }
    public CandidateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contestNum = getArguments().getInt(CONTEST_NUM);
            candidateNum = getArguments().getInt(CANDIDATE_NUM);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("ContestFragment", "In onActivityCreated");
        mActivity = (VIPTabBarActivity)getActivity();
        setContents();
    }

    /**
     * Helper function to populate the candidate view labels
     */
    private void setContents() {
        TextView nameView = (TextView)mActivity.findViewById(R.id.candidate_name);
        TextView partyView = (TextView)mActivity.findViewById(R.id.candidate_party);
        try {
            voterInfo = mActivity.getVoterInfo();
            contest = voterInfo.contests.get(contestNum);
            candidate = contest.candidates.get(candidateNum);

            String name = (candidate.name != null) ? candidate.name : getString(R.string.candidate_no_name);
            nameView.setText(name);

            String party = (candidate.party != null) ? candidate.party : getString(R.string.candidate_no_party);
            partyView.setText(party);

            // SET CANDIDATE DETAILS
            boolean phoneVisible =  setTextView(R.id.candidate_details_phone_text, R.id.candidate_details_phone_row, candidate.phone);
            if (phoneVisible) {
                setDetailClickHandler(R.id.candidate_details_phone_row, "phone", candidate.phone);
            }
            boolean emailVisible =  setTextView(R.id.candidate_details_email_text, R.id.candidate_details_email_row, candidate.email);
            if (emailVisible) {
                setDetailClickHandler(R.id.candidate_details_email_row, "email", candidate.email);
            }
            boolean websiteVisible =  setTextView(R.id.candidate_details_web_site_text, R.id.candidate_details_web_site_row, candidate.candidateUrl);
            if (websiteVisible) {
                setDetailClickHandler(R.id.candidate_details_web_site_row, "website", candidate.candidateUrl);
            }
            if (!websiteVisible && !emailVisible && !phoneVisible) {
                // toggle details_none view to visible
                View view = mActivity.findViewById(R.id.candidate_details_none_row);
                view.setVisibility(View.VISIBLE);
            }

            // SET CANDIDATE SOCIAL MEDIA
            for (String type : SocialMediaChannel.getChannelTypes()) {
                String cleanType = SocialMediaChannel.getCleanType(type);
                String packageName = mActivity.getPackageName();
                SocialMediaChannel channel = candidate.findChannelForType(type);
                int containerId = getResources().getIdentifier("candidate_social_" + cleanType + "_row", "id", packageName);
                int labelId = getResources().getIdentifier("candidate_social_" + cleanType + "_text", "id", packageName);
                boolean isVisible = setTextView(labelId, containerId, channel.type);
                if (isVisible) {
                    setSocialClickHandler(containerId, channel);
                }
            }

            if (candidate.channels.size() == 0) {
                // toggle social_none view to visible
                View view = mActivity.findViewById(R.id.candidate_social_none_row);
                view.setVisibility(View.VISIBLE);
            }


        } catch (Exception ex) {
            Log.e("Candidate Fragment", "Failed to get candidate info!");
            ex.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContainer = container;
        Log.d("CandidateFragment:onCreateView", "Hiding contest container's view");
        container.getChildAt(0).setVisibility(View.INVISIBLE);

        return inflater.inflate(R.layout.fragment_candidate, container, false);
    }

    private boolean setTextView(int labelId, int containerId, String val) {
        boolean isVisible = false;
        TextView textView = (TextView)mActivity.findViewById(labelId);
        if (val != null && !val.isEmpty()) {
            textView.setText(val);
            isVisible = true;
        } else {
            View container = mActivity.findViewById(containerId);
            container.setVisibility(View.GONE);
        }
        return isVisible;
    }

    private void setDetailClickHandler(int containerId, final String type, final String value) {
        View view = mActivity.findViewById(containerId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDetailIntent(type, value);
            }
        });
    }

    private void startDetailIntent(final String type, final String value) {
        String cleanValue = value.trim();
        Intent intent;
        switch (type) {
            case "website":
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(cleanValue));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    showErrorAlert(getString(R.string.no_browser));
                }
                break;
            case "phone":
                try {
                    intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", cleanValue, null));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    showErrorAlert(getString(R.string.no_phone));
                }
                break;
            case "email":
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {cleanValue});
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    showErrorAlert(getString(R.string.no_email));
                }
                break;
        }
    }

    private void setSocialClickHandler(int containerId, final SocialMediaChannel channel) {
        View view = mActivity.findViewById(containerId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, channel.getUri());
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    showErrorAlert(getString(R.string.no_browser));
                }
            }
        });

    }

    private void showErrorAlert(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getString(R.string.ok), null);
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onDetach() {
        // Show contest fragment components again when user goes back
        Log.d("CandidateFragment:onDetach", "Showing contest container's view again");
        mContainer.getChildAt(0).setVisibility(View.VISIBLE);

        super.onDetach();
    }

}
