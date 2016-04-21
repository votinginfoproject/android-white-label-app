package com.votinginfoproject.VotingInformationProject.views.viewHolders;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;

/**
 * Created by marcvandehey on 4/12/16.
 */
public class PollingSiteViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final TextView mSiteType;
    private final TextView mSiteTitle;
    private final TextView mSiteAddress;
    private final TextView mSiteHours;
    private final RelativeLayout mContainer;

    public PollingLocation mLocation;

    /**
     * View Holder for an Polling Location Item. Expects to use the row_polling_site layout
     */
    public PollingSiteViewHolder(View view) {
        super(view);

        mView = view;
        mContainer = (RelativeLayout) view.findViewById(R.id.row_polling_site_container);
        mSiteType = (TextView) view.findViewById(R.id.text_view_polling_site_type);
        mSiteTitle = (TextView) view.findViewById(R.id.site_title);
        mSiteAddress = (TextView) view.findViewById(R.id.site_address);
        mSiteHours = (TextView) view.findViewById(R.id.site_hours);
    }

    public void setPollingLocation(Context context, PollingLocation pollingLocation) {
        mLocation = pollingLocation;

        Drawable dot = ContextCompat.getDrawable(context, mLocation.getDrawableDot());

        String siteType = context.getText(mLocation.getPollingTypeString()).toString();
        mSiteType.setText(siteType.toUpperCase());
        mSiteType.setCompoundDrawablesWithIntrinsicBounds(dot, null, null, null);
        mSiteTitle.setText(mLocation.address.locationName);
        mSiteAddress.setText(mLocation.address.line1);
        mSiteHours.setText(mLocation.pollingHours);

        mContainer.measure(0, 0);
        mView.getLayoutParams().height = mContainer.getMeasuredHeight();
    }

    public void setPollingLocation(final Context context, final PollingLocation pollingLocation, boolean animated) {
        if (!animated) {
            setPollingLocation(context, pollingLocation);
            return;
        }

        mContainer.measure(0, 0);

        final int oldHeight = mContainer.getMeasuredHeight();


        ObjectAnimator containerFadeOut = ObjectAnimator.ofFloat(mContainer, "alpha", 1.f, 0.f);
        containerFadeOut.setDuration(300);
        containerFadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //Not Implemented
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setPollingLocation(context, pollingLocation);
                mContainer.measure(0, 0);

                final int newHeight = mContainer.getMeasuredHeight();

                ObjectAnimator containerFadeIn = ObjectAnimator.ofFloat(mContainer, "alpha", 0.f, 1.f);

                containerFadeIn.setDuration(300);
                containerFadeIn.start();

                mView.getLayoutParams().height = oldHeight;

                Animation a = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        final int deltaHeight = (int) ((newHeight - oldHeight) * interpolatedTime + oldHeight);
                        mView.getLayoutParams().height = deltaHeight;
                        mView.requestLayout();
                    }

                    @Override
                    public boolean willChangeBounds() {
                        return true;
                    }
                };

                a.setDuration(300);
                mView.startAnimation(a);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //Not implemented
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //Not implemented
            }
        });

        containerFadeOut.start();
    }

    public View getView() {
        return mView;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mSiteTitle.getText() + "'";
    }
}