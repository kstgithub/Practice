package com.kst.funddemo.customControls;

import com.kst.funddemo.common.util.Tools;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlidingTabLayout extends HorizontalScrollView{

    public interface TabColorizer {

        int getIndicatorColor(int position);

    }

    private final int LIMT_FOR_FONT_SIZE = 720;
    /** title偏移量*/
    private static final int TITLE_OFFSET_DIPS = 24;
    /** tab间的默认padding 值*/
    private static final int TAB_VIEW_PADDING_DIPS = 16;
    /** tab文字默认大小*/
    private static final int TAB_VIEW_TEXT_SIZE_SP = 12;
    /** title偏移量*/
    private int mTitleOffset;
    private int mTabViewLayoutId;
    private int mTabViewTextViewId;
    /** 是否平均排列*/
    private boolean mDistributeEvenly;

    private ViewPager mViewPager;
    private SparseArray<String> mContentDescriptions = new SparseArray<String>();
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

    private final SlidingTabStrip mTabStrip;

    public SlidingTabLayout(Context context) {
        this(context, null);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // 禁止滑块
        setHorizontalScrollBarEnabled(false);
        
        setFillViewport(true);

        mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);

        mTabStrip = new SlidingTabStrip(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public interface OnBehaviorChanged{
        public void onBehaviorChanged(int position,boolean isClick);
    }
    /**
     * 设置TAB 颜色
     * @param tabColorizer
     */
    public void setCustomTabColorizer(TabColorizer tabColorizer) {
        mTabStrip.setCustomTabColorizer(tabColorizer);
    }

    /**
     * tab是否均匀分布
     * @param distributeEvenly
     */
    public void setDistributeEvenly(boolean distributeEvenly) {
        mDistributeEvenly = distributeEvenly;
    }

    /**
     * 设置选中TAB的颜色
     * @param colors
     */
    public void setSelectedIndicatorColors(int... colors) {
        mTabStrip.setSelectedIndicatorColors(colors);
    }

    /**
     * 设置viewpager的滑动事件，必须通过此处设置
     * @param listener
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }

    /**
     * 设置自定义tab
     * @param layoutResId
     * @param textViewId
     */
    public void setCustomTabView(int layoutResId, int textViewId) {
        mTabViewLayoutId = layoutResId;
        mTabViewTextViewId = textViewId;
    }

    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();

        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
        notifyDataSetChanged();
    }
    
    /**
     * 更新tab项，tab动态改变后调用
     */
    public void notifyDataSetChanged() {
        mTabStrip.removeAllViews();
        if (mViewPager != null) {
            populateTabStrip();
        }
    }
    
    /**
     * 创建默认TAB
     * @param context
     * @return
     */
    protected TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                outValue, true);
        textView.setBackgroundResource(outValue.resourceId);
        //textView.setAllCaps(true);

        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);

        return textView;
    }

    /**
     * 填充数据
     */
    private void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final OnClickListener tabClickListener = new TabClickListener();

        for (int i = 0; i < adapter.getCount(); i++) {
            View tabView = null;
            TextView tabTitleView = null;

            if (mTabViewLayoutId != 0) {
                tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip,
                        false);
                tabTitleView = (TextView) tabView.findViewById(mTabViewTextViewId);
            }

            if (tabView == null) {
                tabView = createDefaultTabView(getContext());
            }

            if (tabTitleView == null && TextView.class.isInstance(tabView)) {
                tabTitleView = (TextView) tabView;
            }

            if (mDistributeEvenly) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                lp.width = 0;
                lp.weight = 1;
            }
            if (tabTitleView != null) {
                tabTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Tools.getScreenWidth() > LIMT_FOR_FONT_SIZE ? 14 : 13);
                tabTitleView.setText(adapter.getPageTitle(i));
            }
            tabView.setOnClickListener(tabClickListener);
            String desc = mContentDescriptions.get(i, null);
            if (desc != null) {
                tabView.setContentDescription(desc);
            }

            mTabStrip.addView(tabView);
            if (i == mViewPager.getCurrentItem()) {
                tabView.setSelected(true);
            }
        }
    }

    /**
     * 设置 描述
     * @param i
     * @param desc
     */
    public void setContentDescription(int i, String desc) {
        mContentDescriptions.put(i, desc);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    /**
     * 滑动到指定TAB
     * @param tabIndex
     * @param positionOffset
     */
    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {
                targetScrollX -= mTitleOffset;
            }

            scrollTo(targetScrollX, 0);
        }
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null)
                    ? (int) (positionOffset * selectedTitle.getWidth())
                    : 0;
            scrollToTab(position, extraOffset);

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                mTabStrip.getChildAt(i).setSelected(position == i);
            }
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }

    }

    private class TabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                if (v == mTabStrip.getChildAt(i)) {
                    if (mViewPager!=null&&mViewPager.getCurrentItem()!=i) {
                        mViewPager.setCurrentItem(i);
                    }
                    return;
                }
            }
        }
    }


}
