package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Distance;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Duration;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Step;

/**
 * Created by max on 4/25/16.
 */
public class DirectionsStepViewHolder extends RecyclerView.ViewHolder {
    private TextView mInstructionsText;
    private TextView mDistanceText;
    private TextView mDurationText;
    private View mDistanceDurationDivider;

    private Step mStep;

    public DirectionsStepViewHolder(View view) {
        super(view);

        mInstructionsText = (TextView) view.findViewById(R.id.directions_step_instructions);
        mDistanceText = (TextView) view.findViewById(R.id.directions_step_distance);
        mDurationText = (TextView) view.findViewById(R.id.directions_step_duration);
        mDistanceDurationDivider = view.findViewById(R.id.directions_step_divider);

        updateUI();
    }

    public void setStep(Step step) {
        mStep = step;
        updateUI();
    }

    private void updateUI() {
        mInstructionsText.setText("");
        mDistanceText.setText("");
        mDurationText.setText("");

        if (mStep != null) {
            mInstructionsText.setText(trimTrailingWhitespace(Html.fromHtml(mStep.html_instructions)));

            Duration duration = mStep.duration;
            Distance distance = mStep.distance;

            if (duration != null) {
                mDurationText.setText(duration.text);
            }

            if (distance != null) {
                mDistanceText.setText(distance.text);
            }

            if (duration != null && distance != null) {
                mDistanceDurationDivider.setVisibility(View.VISIBLE);
            } else {
                mDistanceDurationDivider.setVisibility(View.GONE);
            }
        }
    }

    private static CharSequence trimTrailingWhitespace(CharSequence source) {
        if(source == null) {
            return "";
        }

        int i = source.length();

        // loop back to the first non-whitespace character
        while(--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }

        return source.subSequence(0, i+1);
    }
}
