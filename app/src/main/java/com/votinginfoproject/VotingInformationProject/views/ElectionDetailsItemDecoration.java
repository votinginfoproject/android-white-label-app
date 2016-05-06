package com.votinginfoproject.VotingInformationProject.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionBodySubtitleViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionBodyTitleViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.HeaderViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ReportErrorViewHolder;

/**
 * Created by max on 4/19/16.
 */
public class ElectionDetailsItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };
    private final Drawable mDivider;

    public ElectionDetailsItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawVertical(c, parent);
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {

            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            final RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(child);

            final int top = child.getTop() - params.topMargin + (int) (child.getTranslationY() + 0.5);
            final int bottom = top + mDivider.getIntrinsicHeight();

            if (viewHolder instanceof ElectionBodySubtitleViewHolder) {
                ElectionBodySubtitleViewHolder subtitleViewHolder = (ElectionBodySubtitleViewHolder) viewHolder;
                if (subtitleViewHolder.isFirstSubtitle) {
                    continue;
                }

                mDivider.setBounds(left + subtitleViewHolder.getLeftDividerMargin(), top, right, bottom);
                mDivider.draw(c);

            } else if ((viewHolder instanceof HeaderViewHolder) ||
                    (viewHolder instanceof ElectionBodyTitleViewHolder) ||
                    (viewHolder instanceof ReportErrorViewHolder)) {

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
