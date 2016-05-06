package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.votinginfoproject.VotingInformationProject.views.viewHolders.DecoratedViewHolder;

/**
 * Created by marcvandehey on 5/3/16.
 */
public class BallotRecyclerViewDecorator extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    private final Drawable mDivider;

    public BallotRecyclerViewDecorator(Context context) {
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

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int bottom = child.getTop() - params.topMargin + (int) (child.getTranslationY() + 0.5);

            //Line up divider with left padding
            int leftBounds = 0;

            //If the child is the header show full length
            //If the next child is the footer show full length as well
            boolean shouldDrawDecoration = false;

            if (viewHolder instanceof DecoratedViewHolder) {
                DecoratedViewHolder decoratedViewHolder = (DecoratedViewHolder) viewHolder;
                shouldDrawDecoration = decoratedViewHolder.shouldShowItemDecoration();
            }

            if (shouldDrawDecoration) {
                mDivider.setBounds(leftBounds, bottom - mDivider.getIntrinsicHeight(), right, bottom);
                mDivider.draw(c);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    }
}