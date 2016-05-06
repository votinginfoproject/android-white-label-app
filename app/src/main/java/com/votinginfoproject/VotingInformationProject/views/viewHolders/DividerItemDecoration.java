package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * https://gist.github.com/alexfu/0f464fc3742f134ccd1e
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    private final Drawable mDivider;

    public DividerItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawVertical(c, parent);
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();

        //For all children, except the last one. Ignore the last line
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(child);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int top = child.getBottom() + params.bottomMargin + (int) (child.getTranslationY() + 0.5);
            int bottom = top + mDivider.getIntrinsicHeight();

            //Line up divider with left padding
            int leftBounds = child.getPaddingLeft();

            //If the child is the header show full length
            //If the next child is the footer show full length as well
            if (viewHolder instanceof HeaderViewHolder) {
                leftBounds = 0;
            } else if (viewHolder instanceof ReportErrorViewHolder) {
                leftBounds = right;
            } else if (i + 1 < childCount) {
                final View nextChild = parent.getChildAt(i + 1);
                final RecyclerView.ViewHolder nextViewHolder = parent.getChildViewHolder(nextChild);

                if (nextViewHolder instanceof ReportErrorViewHolder) {
                    leftBounds = 0;
                }
            }

            mDivider.setBounds(leftBounds, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    }
}