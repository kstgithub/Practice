package com.kst.funddemo.customControls;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author zyc
 * 自定义ViewPager屏蔽滑动事件
 */
public class UnableSlideViewPager extends ViewPager {
	/**是否禁止滚动*/
	private boolean isUnableScroll = true;
	/**
	 * 设置是否需要ViewPager滑动
	 * @param isUnableScroll
	 */
	public void setUnableScroll(boolean isUnableScroll) {
		this.isUnableScroll = isUnableScroll;
	}
	public UnableSlideViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UnableSlideViewPager(Context context) {
		super(context);
	}
	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (isUnableScroll)
			return false;
		else
			return super.onTouchEvent(arg0);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (isUnableScroll)
			return false;
		else
			return super.onInterceptTouchEvent(arg0);
	}

	@Override
	public void setCurrentItem(int item) {
		super.setCurrentItem(item);
	}
}
