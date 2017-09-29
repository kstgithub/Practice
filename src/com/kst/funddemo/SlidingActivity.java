package com.kst.funddemo;

import com.kst.funddemo.adapter.MainPagerAdapter;
import com.kst.funddemo.common.util.Tools;
import com.kst.funddemo.customControls.MainRootViewPager;
import com.kst.funddemo.customControls.slidingLayer.CustomViewAbove.OnMenuSlidingListener;
import com.kst.funddemo.customControls.slidingLayer.SlidingMenu;
import com.kst.funddemo.customControls.slidingLayer.SlidingMenu.OnClosedListener;
import com.kst.funddemo.customControls.slidingLayer.SlidingMenu.OnOpenedListener;
import com.kst.funddemo.customview.BookBarView;
import com.kst.funddemo.customview.BookShelfView;
import com.kst.funddemo.customview.BookStoreView;
import com.nineoldandroids.view.ViewHelper;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/***
 * 抽屉式布局
 * @author dell
 *
 */
public class SlidingActivity extends SlidingFragmentActivity implements ViewPager.OnPageChangeListener
		, MainRootViewPager.OnPageOffsetListener, OnClickListener {
	/** 侧滑布局 */
	private SlidingMenu menu;
	/** 侧滑布局高度 */
	private final float SLIDING_MENU_WIDTH = Tools.dipToPixel(295);
	/** 控制抽屉式拖动的主界面 */
	private RelativeLayout drawerMainView = null;
	
	private float mScreenHeight;

	private Button btn_menu;

	/** 导航栏 */
	private TextView[] mRadioViews;
	/** 顶部TAB当前是否已经获取到宽度 */
	private boolean radioWidthIsNone = true;

	/** 书城、书架、书吧页面容器 用于控制书城书架书吧滑动切换 */
	private MainRootViewPager mMainRootLayout = null;
	/** 书城页面视图 */
	private BookStoreView bookStoreView = null;
	/** 书架页面视图 */
	private BookShelfView bookShelfView = null;
	/** 书吧页面视图 */
	private BookBarView bookBarView = null;

	private int mPageScrollState = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sliding);

		mScreenHeight = Tools.getScreenHeight();
		initView();
		initMenu();
		initRadio();
		
		setBehindContentView(R.layout.td_main_user_info_layout);

		bookShelfView = new BookShelfView(this);
		bookStoreView = new BookStoreView(this);
		bookBarView = new BookBarView(this);
		MainPagerAdapter mAdapter = new MainPagerAdapter();
		mMainRootLayout.setAdapter(mAdapter);
		List<View> mViews = new ArrayList<View>();
		mViews.add(bookShelfView.getBookShelfView());
		mViews.add(bookStoreView.getBookStoreView());
		mViews.add(bookBarView.getBookBarView());
		mAdapter.addViews(mViews);

		mMainRootLayout.setOnPageChangeListener(this);
		mMainRootLayout.setOnPageOffsetListener(this);
		mRadioViews[0].setOnClickListener(this);
		mRadioViews[1].setOnClickListener(this);
		mRadioViews[2].setOnClickListener(this);
		
		btn_menu = (Button) findViewById(R.id.btn_menu);
		btn_menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(SlidingActivity.this, "MENU", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		drawerMainView = (RelativeLayout) findViewById(R.id.main_layout);
		mRadioViews = new TextView[3];
		mRadioViews[0] = (TextView) findViewById(R.id.checkBox1);
		mRadioViews[1] = (TextView) findViewById(R.id.checkBox2);
		mRadioViews[2] = (TextView) findViewById(R.id.checkBox3);
		mMainRootLayout = (MainRootViewPager) findViewById(R.id.root_layout);
	}

	/***
	 * 初始化顶部Tab按钮
	 */
	private void initRadio() {
		// 顶部TAB 透明度在 0.3f-1f 区间内切换
		ViewHelper.setAlpha(mRadioViews[0], 1f);
		ViewHelper.setAlpha(mRadioViews[1], 0.3f);
		ViewHelper.setAlpha(mRadioViews[2], 0.3f);
		// 初始化需要获取view宽高，需在radio onMeasure之后获取宽高
		mRadioViews[0].getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if (radioWidthIsNone) {
							radioWidthIsNone = false;
							for (int i = 0; i < mRadioViews.length; i++) {
								ViewHelper.setPivotX(mRadioViews[i], mRadioViews[i].getWidth() / 2f);
								ViewHelper.setPivotY(mRadioViews[i],
										mRadioViews[i].getHeight() / 2f);
								ViewHelper.setScaleX(mRadioViews[i], i == 0 ? 1.0f : 0.8f);
								ViewHelper.setScaleY(mRadioViews[i], i == 0 ? 1.0f : 0.8f);
							}
						}
					}
				});
	}

	/**
	 * 初始化Menu菜单
	 */
	private void initMenu() {

		menu = getSlidingMenu();
		menu.setBehindWidth((int) SLIDING_MENU_WIDTH);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setBehindScrollScale(0.8f);
		menu.setOnMenuSlidingListener(new OnMenuSlidingListener() {

			@Override
			public void onMenuSliding(float x) {
				menu.setBackgroundResource(x == 0 ? R.color.titlebar_login_bg
						: R.drawable.user_info_background);
				float v = x / SLIDING_MENU_WIDTH;
				//value 0.75-1
				float scale = (1 - v) * 0.25f + 0.75f;
				//menu background切换后 prvot会丢失
				ViewHelper.setPivotX(drawerMainView, 0);
				ViewHelper.setPivotY(drawerMainView,
						mScreenHeight / 2f+mScreenHeight/20f);
//				ViewHelper.setTranslationY(drawerMainView, mScreenHeight * v
//						/ 40);
				ViewHelper.setScaleX(drawerMainView, scale);
				ViewHelper.setScaleY(drawerMainView, scale);
			}
		});
		menu.setOnOpenedListener(new OnOpenedListener() {

			@Override
			public void onOpened() {
				setSlidingEnabled(true);
			}
		});
		menu.setOnClosedListener(new OnClosedListener() {

			@Override
			public void onClosed() {
				ViewHelper.setScaleX(drawerMainView, 1);
				ViewHelper.setScaleY(drawerMainView, 1);
				
			}
		});
	}

	public void onChanged(int index) {
		if (index == MainRootViewPager.BOOKSHELF_INDEX) {
			setSlidingEnabled(true);
		} else if (index == MainRootViewPager.BOOKSTORE_INDEX) {
			setSlidingEnabled(false);
		} else if (index == MainRootViewPager.BOOKBAR_INDEX) {
			setSlidingEnabled(false);
		}
	}

	@Override
	public void onPageScrolled(int i, float v, int i1) {

	}

	@Override
	public void onPageSelected(int i) {
		onChanged(i);
	}

	@Override
	public void onPageScrollStateChanged(int i) {
		mPageScrollState = i;
		if (i == 0){
			mMainRootLayout.setCurrentViewIndex(mMainRootLayout.getCurrentItem());
			mMainRootLayout.setSlideDirectionToDefault();

//			mOldPosition = mCurrentPositon;
//			mCurrentPositon = mMainRootLayout.getCurrentItem();

			//在viewpager 首末页 任然会调用此方法，过滤错误调用
//			if (mOldPosition != mCurrentPositon) {
//				mBaseViewList.get(mCurrentPositon).viewShow();
//				mBaseViewList.get(mOldPosition).viewHide();
//				mMainPresenter.fixSlideOffset(mMainRootLayout.getCurrentViewIndex(),mRadioViews);
//			}
		}
	}

	/**
	 * 顶部TAB 过渡动画
	 * v3.60
	 * @param widthPixels 屏幕宽度
	 * @param x 页面偏移量
	 */
	@Override
	public void onSlideOffset(float widthPixels, float x) {
		setSlidingEnabled(x == 0);
		tabSlide(widthPixels,x,mRadioViews);
	}

	/**
	 * 主页切换，联动TAB动画效果
	 * @param widthPixels
	 * @param x
	 * @param mRadioViews
	 */
	public void tabSlide(float widthPixels, float x, TextView[] mRadioViews) {
		// 快速滑动可能导致X归0
		if (x <= 0.01f)
			return;

		View currentView = null, targetView = null, toDefaultView = null;

		//根据偏移量 设定当前选中 和 目标选中 VIEW
		if (x <= widthPixels) {
			currentView = mRadioViews[0];
			targetView = mRadioViews[1];
			toDefaultView = mRadioViews[2];
		} else if (x > widthPixels) {
			x -= widthPixels;
			currentView = mRadioViews[1];
			targetView = mRadioViews[2];
			toDefaultView = mRadioViews[0];
		}

		//执行过渡动画
		if (currentView != null && targetView != null) {
			float alphaValue1 = (1 - x / widthPixels) * 0.7f + 0.3f;
			float alphaValue2 = (x / widthPixels) * 0.7f + 0.3f;
			// scale interval value 0.9f-1.1f;
			float scaleValue1 = (1 - x / widthPixels) * 0.2f + 0.8f;
			float scaleValue2 = (x / widthPixels) * 0.2f + 0.8f;

			ViewHelper.setAlpha(currentView, alphaValue1);
			ViewHelper.setAlpha(targetView, alphaValue2);

			ViewHelper.setScaleX(currentView, scaleValue1);
			ViewHelper.setScaleY(currentView, scaleValue1);

			ViewHelper.setScaleX(targetView, scaleValue2);
			ViewHelper.setScaleY(targetView, scaleValue2);

			ViewHelper.setAlpha(toDefaultView, 0.3f);
			ViewHelper.setScaleX(toDefaultView, 0.8f);
			ViewHelper.setScaleY(toDefaultView, 0.8f);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.checkBox1:
				switchView(MainRootViewPager.BOOKSHELF_INDEX);
				break;
			case R.id.checkBox2:
				switchView(MainRootViewPager.BOOKSTORE_INDEX);
				break;
			case R.id.checkBox3:
				switchView(MainRootViewPager.BOOKBAR_INDEX,true);
				break;
			default:
				break;
		}
	}

	public synchronized void switchView(int index) {
		switchView(index,false);
	}

	/**
	 * 切换书城、书架、书吧view视图
	 */
	public synchronized void switchView(int index , boolean isClick) {
		if (index == MainRootViewPager.BOOKSHELF_INDEX) {
			if (!mMainRootLayout.isSelectedBookShelf()) {
				mMainRootLayout.setCurrentItem(index);
			}
		} else if (index == MainRootViewPager.BOOKSTORE_INDEX) {
			if (!mMainRootLayout.isSelectedBookStore()) {
				mMainRootLayout.setCurrentItem(index);
			}
		}else if (index == MainRootViewPager.BOOKBAR_INDEX) {

			if (!mMainRootLayout.isSelectedBookBar()) {
				mMainRootLayout.setCurrentItem(index);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (mPageScrollState != 0){
			return;
		}
		if (mMainRootLayout.isSelectedBookBar()) {
			switchView(MainRootViewPager.BOOKSTORE_INDEX);
			return;
		}

		if (bookStoreView != null && mMainRootLayout.isSelectedBookStore()) {
			switchView(MainRootViewPager.BOOKSHELF_INDEX);
			return;
		}
		super.onBackPressed();
	}
}
