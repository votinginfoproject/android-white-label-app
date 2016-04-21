package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by marcvandehey on 4/21/16.
 */
public class BallotItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    private Drawable mDivider;

    public BallotItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int right = parent.getWidth();

        final int childCount = parent.getChildCount();

        //For all children, except the last one. Ignore the last line
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(child);
            int top = child.getTop();

            //Line up divider with left padding
            int leftBounds = 0;

            //If the child is the header show full length
            //If the next child is the footer show full length as well
            boolean shouldDrawDecoration = (viewHolder instanceof ReportErrorViewHolder);

            if (viewHolder instanceof ContestViewHolder) {
                ContestViewHolder contestViewHolder = (ContestViewHolder) viewHolder;
                shouldDrawDecoration = contestViewHolder.hasSectionTitle();
            }

            if (shouldDrawDecoration) {
                mDivider.setBounds(leftBounds, top, right, top + mDivider.getIntrinsicHeight());
                mDivider.draw(c);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    }
}
