package com.kst.funddemo.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.kst.funddemo.R;
import com.kst.funddemo.adapter.SlidePagerAdapter;
import com.kst.funddemo.common.util.Tools;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class IndicatorLayout extends LinearLayout{
    private static final int DURATION = 300;

    private final int SELECTED_COLOR = 0xbb25c4a6;
    private final int UNSELECTED_COLOR = 0x55000000;

    private int mIndicatorMargin = Tools.dipToPixel(1.5f);
    private int mIndicatorWidth = Tools.dipToPixel(4);
    private int mIndicatorHeight = Tools.dipToPixel(4);

    private int oldPosition;
    private int currentPositon;

    private ViewPager mViewpager;

    private SlidePagerAdapter mAdapter;

    private AnimatorSet mAnimatorInSet;

    private AnimatorSet mAnimatorOutSet;

    private int rideX = 1;

    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

    public IndicatorLayout(Context context) {
        super(context);
        init();
    }

    public IndicatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        inAnim();
        outAnim();
    }

    public void setRideX(int rideX){
        this.rideX = rideX;
    }

    public void setViewPager(ViewPager viewPager) {
        mAdapter = (SlidePagerAdapter) viewPager.getAdapter();
        mViewpager = viewPager;
        if (mViewpager != null && mAdapter != null) {
            createIndicators();
            mViewpager.setOnPageChangeListener(new IndicatorViewPagerListener());
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }

    private void startInAnim(View view){
        mAnimatorInSet.setTarget(view);
        mAnimatorInSet.start();
    }

    private void startOutAnim(View view){
        mAnimatorOutSet.setTarget(view);
        mAnimatorOutSet.start();
    }

    private void inAnim(){
        mAnimatorInSet = new AnimatorSet();
        mAnimatorInSet.playTogether(
                ObjectAnimator.ofFloat(null, "alpha", 0.5f, 1f),
                ObjectAnimator.ofFloat(null, "scaleX", 1f,1.5f),
                ObjectAnimator.ofFloat(null, "scaleY", 1f,1.5f)
        );
        mAnimatorInSet.setDuration(DURATION);
    }

    private void outAnim(){
        mAnimatorOutSet = new AnimatorSet();
        mAnimatorOutSet.playTogether(
//                ObjectAnimator.ofFloat(null, "alpha", 0.5f, 1f),
                ObjectAnimator.ofFloat(null, "scaleX", 1f,1.5f),
                ObjectAnimator.ofFloat(null, "scaleY", 1f,1.5f)
        );
        mAnimatorOutSet.setInterpolator(new ReverseInterpolator());
        mAnimatorOutSet.setDuration(DURATION);
    }

    private void createIndicators() {
        removeAllViews();
        int count = mAdapter.getRealCount();
        if (count <= 0) {
            return;
        }
        int currentItem = mViewpager.getCurrentItem()%(count/rideX);

        for (int i = 0; i < count/rideX; i++) {
            if (currentItem == i) {
                addIndicator(true);
            } else {
                addIndicator(false);
            }
        }
    }

    private void addIndicator( boolean isChecked) {

        View Indicator = new View(getContext());
        Indicator.setBackgroundResource(R.drawable.white_radius);
        if (isChecked){
            GradientDrawable myGrad = (GradientDrawable)Indicator.getBackground();
            myGrad.setColor(SELECTED_COLOR);
//            startOutAnim(Indicator);
        }else {
            GradientDrawable myGrad = (GradientDrawable)Indicator.getBackground();
            myGrad.setColor(UNSELECTED_COLOR);
//            startInAnim(Indicator);
        }
        addView(Indicator, mIndicatorWidth, mIndicatorHeight);
        LayoutParams lp = (LayoutParams) Indicator.getLayoutParams();
        lp.leftMargin = mIndicatorMargin;
        lp.rightMargin = mIndicatorMargin;
        Indicator.setLayoutParams(lp);
    }

    private class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float value) {
            return Math.abs(1.0f - value);
        }
    }

    private class IndicatorViewPagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {



            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {

            int realPosition = position%(mAdapter.getRealCount()/rideX);
            currentPositon = realPosition;

            View oldIndicator;
            if (oldPosition >= 0 && (oldIndicator = getChildAt(oldPosition)) != null) {
                GradientDrawable myGrad = (GradientDrawable)oldIndicator.getBackground();
                myGrad.setColor(UNSELECTED_COLOR);
                startOutAnim(oldIndicator);
            }

            View selectedIndicator = getChildAt(realPosition);
            if (selectedIndicator != null){
                GradientDrawable myGrad = (GradientDrawable)selectedIndicator.getBackground();
                myGrad.setColor(SELECTED_COLOR);
                startInAnim(selectedIndicator);

            }

            oldPosition = realPosition;
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    }

}
