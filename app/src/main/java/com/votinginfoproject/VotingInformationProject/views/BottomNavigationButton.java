package com.votinginfoproject.VotingInformationProject.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;

/**
 * Created by marcvandehey on 4/5/16.
 */
public class BottomNavigationButton extends RelativeLayout {

    private ImageView imageView;
    private TextView textView;
    private boolean isSelected = false;

    private int textSizeSelected;
    private int textSizeUnselected;
    private float textAlphaSelected;
    private float textAlphaUnselected;
    private float imageAlphaSelected;
    private float imageAlphaUnselected;

    public BottomNavigationButton(Context context) {
        super(context);

        styleUI(context, null);
    }

    public BottomNavigationButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        styleUI(context, attrs);
    }

    public BottomNavigationButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        styleUI(context, attrs);
    }

    private void styleUI(Context context, AttributeSet attrs) {
        inflate(context, R.layout.bottom_navigation_button, this);

        //Load sizes from resources
        textSizeSelected = context.getResources().getInteger(R.integer.bottom_nav_text_size_selected);
        textSizeUnselected = context.getResources().getInteger(R.integer.bottom_nav_text_size_unselected);

        textAlphaSelected = context.getResources().getInteger(R.integer.bottom_nav_text_alpha_selected) / 100.f;
        textAlphaUnselected = context.getResources().getInteger(R.integer.bottom_nav_text_alpha_unselected) / 100.f;
        imageAlphaSelected = context.getResources().getInteger(R.integer.bottom_nav_image_alpha_selected) / 100.f;
        imageAlphaUnselected = context.getResources().getInteger(R.integer.bottom_nav_image_alpha_unselected) / 100.f;

        imageView = (ImageView) findViewById(R.id.bottom_nav_image_view);
        textView = (TextView) findViewById(R.id.bottom_nav_text_view);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BottomNavigationButton, 0, 0);

        String bottomNavText = typedArray.getString(R.styleable.BottomNavigationButton_bottom_nav_text);
        String bottomNavAccessibility = typedArray.getString(R.styleable.BottomNavigationButton_bottom_nav_accessibility);

        int drawable = typedArray.getResourceId(R.styleable.BottomNavigationButton_bottom_nav_src, -1);

        if (drawable != -1) {
            imageView.setImageDrawable(ContextCompat.getDrawable(context, drawable));
        }

        imageView.setContentDescription(bottomNavAccessibility);

        textView.setText(bottomNavText);

        typedArray.recycle();
    }

    public void setSelected() {
        Log.d("BNB", "setting selected?");
        if (!isSelected) {
            isSelected = true;

            animateButtonToState(true);
        }
    }

    public void setUnselected() {
        Log.d("BNB", "setting unselected?");

        if (isSelected) {
            isSelected = false;

            animateButtonToState(false);
        }
    }

    public void setSelected(boolean animated) {
        if (animated) {
            setSelected();
        } else {
            textView.setAlpha(textAlphaSelected);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSelected);
            imageView.setAlpha(imageAlphaSelected);

            isSelected = true;
        }
    }

    public void setUnselected(boolean animated) {
        if (animated) {
            setUnselected();
        } else {
            textView.setAlpha(textAlphaUnselected);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeUnselected);
            imageView.setAlpha(imageAlphaUnselected);

            isSelected = false;
        }
    }

    private void animateButtonToState(boolean buttonSelected) {
        float endImageAlphaValue;
        float endTextAlphaValue;
        int startTextSizeValue;
        int endTextSizeValue;

        if (buttonSelected) {
            endImageAlphaValue = imageAlphaSelected;
            endTextAlphaValue = textAlphaSelected;

            startTextSizeValue = textSizeUnselected;
            endTextSizeValue = textSizeSelected;
        } else {
            endImageAlphaValue = imageAlphaUnselected;
            endTextAlphaValue = textAlphaUnselected;

            startTextSizeValue = textSizeSelected;
            endTextSizeValue = textSizeUnselected;
        }

        ValueAnimator animator = ValueAnimator.ofFloat(startTextSizeValue, endTextSizeValue);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, animatedValue);
            }
        });

        ObjectAnimator imageAlphaAnimation = ObjectAnimator.ofFloat(imageView, "alpha", imageView.getAlpha(), endImageAlphaValue);
        ObjectAnimator textAlphaAnimation = ObjectAnimator.ofFloat(textView, "alpha", textView.getAlpha(), endTextAlphaValue);

        AnimatorSet alphaSet = new AnimatorSet();
        alphaSet.playTogether(imageAlphaAnimation, textAlphaAnimation, animator);
        alphaSet.setDuration(200);

        alphaSet.start();
    }
}
