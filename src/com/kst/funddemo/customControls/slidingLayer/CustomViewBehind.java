package com.kst.funddemo.customControls.slidingLayer;

import com.kst.funddemo.R;
import com.kst.funddemo.customControls.slidingLayer.SlidingMenu.CanvasTransformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class CustomViewBehind extends ViewGroup {

	private static final String TAG = "CustomViewBehind";

	/** TOUCHMODE_MARGIN默认触摸宽度*/
	private static final int MARGIN_THRESHOLD = 48; // dips
	/** 默认touch model 只响应边界*/
	private int mTouchMode = SlidingMenu.TOUCHMODE_MARGIN;
	/** 自定义above view 处理自己的touch事件*/
	private CustomViewAbove mViewAbove;
	
	/** 主视图*/
	private View mContent;
	/** 右侧视图*/
	private View mSecondaryContent;
	/** 触摸响应区域宽度*/
	private int mMarginThreshold;
	/** 相对屏幕的距离*/
	private int mWidthOffset;
	/** 侧边布局Canvas动态处理*/
	private CanvasTransformer mTransformer;
	private boolean mChildrenEnabled;

	public CustomViewBehind(Context context) {
		this(context, null);
	}

	public CustomViewBehind(Context context, AttributeSet attrs) {
		super(context, attrs);
		mMarginThreshold = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
				MARGIN_THRESHOLD, getResources().getDisplayMetrics());
	}
	
	/**
	 * 添加自定义above，自己处理touch事件
	 * @param customViewAbove
	 */
	public void setCustomViewAbove(CustomViewAbove customViewAbove) {
		mViewAbove = customViewAbove;
	}

	/**
	 * 滑动过程操作侧边布局Canvas
	 * @param t
	 */
	public void setCanvasTransformer(CanvasTransformer t) {
		mTransformer = t;
	}

	/**
	 * 设置相对屏幕宽度
	 * @param i
	 */
	public void setWidthOffset(int i) {
		mWidthOffset = i;
		requestLayout();
	}
	
	/**
	 * 设置触摸响应区域宽度
	 * @param marginThreshold
	 */
	public void setMarginThreshold(int marginThreshold) {
		mMarginThreshold = marginThreshold;
	}
	
	public int getMarginThreshold() {
		return mMarginThreshold;
	}

	/**
	 * 设置Behind View宽度
	 * @return
	 */
	public int getBehindWidth() {
		return mContent.getWidth();
	}

	/**
	 * 设置主视图
	 * @param v
	 */
	public void setContent(View v) {
		if (mContent != null)
			removeView(mContent);
		mContent = v;
		addView(mContent);
	}

	public View getContent() {
		return mContent;
	}

	/**
	 * 设置右侧视图
	 * @param v
	 */
	public void setSecondaryContent(View v) {
		if (mSecondaryContent != null)
			removeView(mSecondaryContent);
		mSecondaryContent = v;
		addView(mSecondaryContent);
	}
	
	/**
	 * 获取右侧视图
	 * @return
	 */
	public View getSecondaryContent() {
		return mSecondaryContent;
	}

	/**
	 * 设置子视图是否响应touch 操作
	 * @param enabled
	 */
	public void setChildrenEnabled(boolean enabled) {
		mChildrenEnabled = enabled;
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		if (mTransformer != null)
			invalidate();
	}

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent e) {
//		return !mChildrenEnabled;
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent e) {
//		return !mChildrenEnabled;
//	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
	return mViewAbove.onInterceptTouchEvent(e);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
	return mViewAbove.onTouchEvent(e);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (mTransformer != null) {
			canvas.save();
			//执行侧滑操作子view transform
			mTransformer.transformCanvas(canvas, mViewAbove.getPercentOpen());
			super.dispatchDraw(canvas);
			canvas.restore();
		} else
			super.dispatchDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		final int height = b - t;
		mContent.layout(0, 0, width-mWidthOffset, height);
		if (mSecondaryContent != null)
			mSecondaryContent.layout(0, 0, width-mWidthOffset, height);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);
		final int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, width-mWidthOffset);
		final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0, height);
		mContent.measure(contentWidth, contentHeight);
		if (mSecondaryContent != null)
			mSecondaryContent.measure(contentWidth, contentHeight);
	}

	private int mMode;
	private boolean mFadeEnabled;
	private final Paint mFadePaint = new Paint();
	private float mScrollScale;
	private Drawable mShadowDrawable;
	private Drawable mSecondaryShadowDrawable;
	private int mShadowWidth;
	private float mFadeDegree;

	/**
	 * 设置滑动模式，left or right 
	 * @param mode
	 */
	public void setMode(int mode) {
		if (mode == SlidingMenu.LEFT || mode == SlidingMenu.RIGHT) {
			if (mContent != null)
				mContent.setVisibility(View.VISIBLE);
			if (mSecondaryContent != null)
				mSecondaryContent.setVisibility(View.INVISIBLE);
		}
		mMode = mode;
	}

	public int getMode() {
		return mMode;
	}

	/**
	 * 设置视差滚动 比例
	 * @param scrollScale
	 */
	public void setScrollScale(float scrollScale) {
		mScrollScale = scrollScale;
	}

	public float getScrollScale() {
		return mScrollScale;
	}
	
	/**
	 * 设置左侧阴影值
	 * @param shadow
	 */
	public void setShadowDrawable(Drawable shadow) {
		mShadowDrawable = shadow;
		invalidate();
	}

	/**
	 * 设置右侧阴影值
	 * @param shadow
	 */
	public void setSecondaryShadowDrawable(Drawable shadow) {
		mSecondaryShadowDrawable = shadow;
		invalidate();
	}

	/**
	 * 设置阴影值宽度
	 * @param width
	 */
	public void setShadowWidth(int width) {
		mShadowWidth = width;
		invalidate();
	}

	/**
	 * 设置侧边布局是否执行渐隐
	 * @param b
	 */
	public void setFadeEnabled(boolean b) {
		mFadeEnabled = b;
	}

	/**
	 * 设置渐隐value  0 - 1
	 * @param degree
	 */
	public void setFadeDegree(float degree) {
		if (degree > 1.0f || degree < 0.0f)
			throw new IllegalStateException("The BehindFadeDegree must be between 0.0f and 1.0f");
		mFadeDegree = degree;
	}

	public int getMenuPage(int page) {
		page = (page > 1) ? 2 : ((page < 1) ? 0 : page);
		if (mMode == SlidingMenu.LEFT && page > 1) {
			return 0;
		} else if (mMode == SlidingMenu.RIGHT && page < 1) {
			return 2;
		} else {
			return page;
		}
	}

	public void scrollBehindTo(View content, int x, int y) {
		int vis = View.VISIBLE;		
		if (mMode == SlidingMenu.LEFT) {
			if (x >= content.getLeft()) vis = View.INVISIBLE;
			scrollTo((int)((x + getBehindWidth())*mScrollScale), y);
		} else if (mMode == SlidingMenu.RIGHT) {
			if (x <= content.getLeft()) vis = View.INVISIBLE;
			scrollTo((int)(getBehindWidth() - getWidth() + 
					(x-getBehindWidth())*mScrollScale), y);
		} else if (mMode == SlidingMenu.LEFT_RIGHT) {
			mContent.setVisibility(x >= content.getLeft() ? View.INVISIBLE : View.VISIBLE);
			mSecondaryContent.setVisibility(x <= content.getLeft() ? View.INVISIBLE : View.VISIBLE);
			vis = x == 0 ? View.INVISIBLE : View.VISIBLE;
			if (x <= content.getLeft()) {
				scrollTo((int)((x + getBehindWidth())*mScrollScale), y);				
			} else {
				scrollTo((int)(getBehindWidth() - getWidth() + 
						(x-getBehindWidth())*mScrollScale), y);				
			}
		}
		if (vis == View.INVISIBLE)
			Log.v(TAG, "behind INVISIBLE");
		setVisibility(vis);
	}

	public int getMenuLeft(View content, int page) {
		if (mMode == SlidingMenu.LEFT) {
			switch (page) {
			case 0:
				return content.getLeft() - getBehindWidth();
			case 2:
				return content.getLeft();
			}
		} else if (mMode == SlidingMenu.RIGHT) {
			switch (page) {
			case 0:
				return content.getLeft();
			case 2:
				return content.getLeft() + getBehindWidth();	
			}
		} else if (mMode == SlidingMenu.LEFT_RIGHT) {
			switch (page) {
			case 0:
				return content.getLeft() - getBehindWidth();
			case 2:
				return content.getLeft() + getBehindWidth();
			}
		}
		return content.getLeft();
	}

	public int getAbsLeftBound(View content) {
		if (mMode == SlidingMenu.LEFT || mMode == SlidingMenu.LEFT_RIGHT) {
			return content.getLeft() - getBehindWidth();
		} else if (mMode == SlidingMenu.RIGHT) {
			return content.getLeft();
		}
		return 0;
	}

	public int getAbsRightBound(View content) {
		if (mMode == SlidingMenu.LEFT) {
			return content.getLeft();
		} else if (mMode == SlidingMenu.RIGHT || mMode == SlidingMenu.LEFT_RIGHT) {
			return content.getLeft() + getBehindWidth();
		}
		return 0;
	}

	/**
	 * 当前区域是否响应  model is margin touch
	 * @param content
	 * @param x
	 * @return
	 */
	public boolean marginTouchAllowed(View content, int x) {
		int left = content.getLeft();
		int right = content.getRight();
		if (mMode == SlidingMenu.LEFT) {
			return (x >= left && x <= mMarginThreshold + left);
		} else if (mMode == SlidingMenu.RIGHT) {
			return (x <= right && x >= right - mMarginThreshold);
		} else if (mMode == SlidingMenu.LEFT_RIGHT) {
			return (x >= left && x <= mMarginThreshold + left) || 
					(x <= right && x >= right - mMarginThreshold);
		}
		return false;
	}

	public void setTouchMode(int i) {
		mTouchMode = i;
	}

	/**
	 * 当前是否允许打开
	 * @param content
	 * @param currPage
	 * @param x
	 * @return
	 */
	public boolean menuOpenTouchAllowed(View content, int currPage, float x) {
		switch (mTouchMode) {
		case SlidingMenu.TOUCHMODE_FULLSCREEN:
			return true;
		case SlidingMenu.TOUCHMODE_MARGIN:
			return menuTouchInQuickReturn(content, currPage, x);
		}
		return false;
	}

	/**
	 * 当前是否响应快速滑动
	 * @param content
	 * @param currPage
	 * @param x
	 * @return
	 */
	public boolean menuTouchInQuickReturn(View content, int currPage, float x) {
		if (mMode == SlidingMenu.LEFT || (mMode == SlidingMenu.LEFT_RIGHT && currPage == 0)) {
			return x >= content.getLeft();
		} else if (mMode == SlidingMenu.RIGHT || (mMode == SlidingMenu.LEFT_RIGHT && currPage == 2)) {
			return x <= content.getRight();
		}
		return false;
	}

	/**
	 * 当前关闭状态是否允许滑动 
	 * @param dx
	 * @return
	 */
	public boolean menuClosedSlideAllowed(float dx) {
		if (mMode == SlidingMenu.LEFT) {
			return dx > 0;
		} else if (mMode == SlidingMenu.RIGHT) {
			return dx < 0;
		} else if (mMode == SlidingMenu.LEFT_RIGHT) {
			return true;
		}
		return false;
	}

	/**
	 * 当前打开状态是否允许滑动 
	 * @param dx
	 * @return
	 */
	public boolean menuOpenSlideAllowed(float dx) {
		if (mMode == SlidingMenu.LEFT) {
			return dx < 0;
		} else if (mMode == SlidingMenu.RIGHT) {
			return dx > 0;
		} else if (mMode == SlidingMenu.LEFT_RIGHT) {
			return true;
		}
		return false;
	}

	/**
	 * 绘制阴影
	 * @param content
	 * @param canvas
	 */
	public void drawShadow(View content, Canvas canvas) {
		if (mShadowDrawable == null || mShadowWidth <= 0) return;
		int left = 0;
		if (mMode == SlidingMenu.LEFT) {
			left = content.getLeft() - mShadowWidth;
		} else if (mMode == SlidingMenu.RIGHT) {
			left = content.getRight();
		} else if (mMode == SlidingMenu.LEFT_RIGHT) {
			if (mSecondaryShadowDrawable != null) {
				left = content.getRight();
				mSecondaryShadowDrawable.setBounds(left, 0, left + mShadowWidth, getHeight());
				mSecondaryShadowDrawable.draw(canvas);
			}
			left = content.getLeft() - mShadowWidth;
		}
		mShadowDrawable.setBounds(left, 0, left + mShadowWidth, getHeight());
		mShadowDrawable.draw(canvas);
	}

	/**
	 * 绘制渐隐程度
	 * @param content
	 * @param canvas
	 * @param openPercent
	 */
	public void drawFade(View content, Canvas canvas, float openPercent) {
		if (!mFadeEnabled) return;
		final int alpha = (int) (mFadeDegree * 255 * Math.abs(1-openPercent));
		mFadePaint.setColor(Color.argb(alpha, 0, 0, 0));
		int left = 0;
		int right = 0;
		if (mMode == SlidingMenu.LEFT) {
			left = content.getLeft() - getBehindWidth();
			right = content.getLeft();
		} else if (mMode == SlidingMenu.RIGHT) {
			left = content.getRight();
			right = content.getRight() + getBehindWidth();			
		} else if (mMode == SlidingMenu.LEFT_RIGHT) {
			left = content.getLeft() - getBehindWidth();
			right = content.getLeft();
			canvas.drawRect(left, 0, right, getHeight(), mFadePaint);
			left = content.getRight();
			right = content.getRight() + getBehindWidth();			
		}
		canvas.drawRect(left, 0, right, getHeight(), mFadePaint);
	}
	
	private boolean mSelectorEnabled = true;
	private Bitmap mSelectorDrawable;
	private View mSelectedView;
	
	/**
	 * 绘制选中效果
	 * @param content
	 * @param canvas
	 * @param openPercent
	 */
	public void drawSelector(View content, Canvas canvas, float openPercent) {
		if (!mSelectorEnabled) return;
		if (mSelectorDrawable != null && mSelectedView != null) {
			String tag = (String) mSelectedView.getTag(R.id.selected_view);
			if (tag.equals(TAG+"SelectedView")) {
				canvas.save();
				int left, right, offset;
				offset = (int) (mSelectorDrawable.getWidth() * openPercent);
				if (mMode == SlidingMenu.LEFT) {
					right = content.getLeft();
					left = right - offset;
					canvas.clipRect(left, 0, right, getHeight());
					canvas.drawBitmap(mSelectorDrawable, left, getSelectorTop(), null);		
				} else if (mMode == SlidingMenu.RIGHT) {
					left = content.getRight();
					right = left + offset;
					canvas.clipRect(left, 0, right, getHeight());
					canvas.drawBitmap(mSelectorDrawable, right - mSelectorDrawable.getWidth(), getSelectorTop(), null);
				}
				canvas.restore();
			}
		}
	}
	
	/**
	 * 是否允许选中效果
	 * @param b
	 */
	public void setSelectorEnabled(boolean b) {
		mSelectorEnabled = b;
	}

	public void setSelectedView(View v) {
		if (mSelectedView != null) {
			mSelectedView.setTag(R.id.selected_view, null);
			mSelectedView = null;
		}
		if (v != null && v.getParent() != null) {
			mSelectedView = v;
			mSelectedView.setTag(R.id.selected_view, TAG+"SelectedView");
			invalidate();
		}
	}

	private int getSelectorTop() {
		int y = mSelectedView.getTop();
		y += (mSelectedView.getHeight() - mSelectorDrawable.getHeight()) / 2;
		return y;
	}

	public void setSelectorBitmap(Bitmap b) {
		mSelectorDrawable = b;
		refreshDrawableState();
	}

}
