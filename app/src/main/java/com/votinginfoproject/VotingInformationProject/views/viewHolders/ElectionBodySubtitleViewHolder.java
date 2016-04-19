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
    private final TextView mTextView;

    public ElectionBodySubtitleViewHolder(View v) {
        super(v);

        mView = v;
        mImageView = (ImageView) mView.findViewById(R.id.body_subtitle_image);
        mTextView = (TextView) mView.findViewById(R.id.body_subtitle);
    }

    public void setTitle(String title) {
        mTextView.setText(title);
    }

    public void setImage(@DrawableRes int id) {
        mImageView.setImageResource(id);
    }
}
