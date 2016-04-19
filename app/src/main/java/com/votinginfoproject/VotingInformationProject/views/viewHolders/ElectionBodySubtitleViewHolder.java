package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;

/**
 * Created by max on 4/18/16.
 */
public class ElectionBodySubtitleViewHolder extends RecyclerView.ViewHolder {

    private final View mView;
    private final ImageView mImageView;
    private final ImageView mChevronImageView;
    private final TextView mTextView;
    private boolean mIsExpanded;

    public ElectionBodySubtitleViewHolder(View v) {
        super(v);

        mView = v;
        mImageView = (ImageView) mView.findViewById(R.id.body_subtitle_image);
        mChevronImageView = (ImageView) mView.findViewById(R.id.body_subtitle_arrow);
        mTextView = (TextView) mView.findViewById(R.id.body_subtitle);
    }

    public void setTitle(String title) {
        mTextView.setText(title);
    }

    public void setImage(@DrawableRes int id) {
        mImageView.setImageResource(id);
    }

    public void setExpanded(boolean expanded) {
        mIsExpanded = expanded;
        @DrawableRes int resId = expanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down;
        mChevronImageView.setImageResource(resId);
    }

    public int getLeftDividerMargin() {
        return mTextView.getLeft();
    }

    public boolean isExpanded() {
        return mIsExpanded;
    }
}
