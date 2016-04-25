package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Step;

/**
 * Created by max on 4/25/16.
 */
public class DirectionsStepViewHolder extends RecyclerView.ViewHolder {
    private TextView mInstructionsText;
    private TextView mDistanceText;
    private TextView mDurationText;
    private View mDivider;

    private Step mStep;

    public DirectionsStepViewHolder(View view) {
        super(view);

        mInstructionsText = (TextView) view.findViewById(R.id.directions_step_instructions);
        mDistanceText = (TextView) view.findViewById(R.id.directions_step_distance);
        mDurationText = (TextView) view.findViewById(R.id.directions_step_duration);
        mDivider = view.findViewById(R.id.directions_step_divider);

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
            mInstructionsText.setText(stripHtml(mStep.html_instructions));
            if (mStep.duration != null) {
                mDurationText.setText(mStep.duration.text);
            }

            if (mStep.distance != null) {
                mDistanceText.setText(mStep.distance.text);
            }
        }
    }

    private String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }
}
