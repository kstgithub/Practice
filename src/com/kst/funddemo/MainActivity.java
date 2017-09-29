package com.kst.funddemo;

import java.util.ArrayList;
import java.util.List;

import com.kst.funddemo.customControls.SlidingTabLayout;
import com.kst.funddemo.fragments.Tv1Fragment;
import com.kst.funddemo.fragments.Tv2Fragment;
import com.kst.funddemo.fragments.Tv3Fragment;
import com.kst.funddemo.fragments.Tv4Fragment;
import com.kst.funddemo.fragments.Tv5Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

/***
 * TAB导航
 * @author dell
 *
 */
public class MainActivity extends FragmentActivity implements OnClickListener,
		OnPageChangeListener {
	private ViewPager viewpager;
	private ArrayList<Fragment> fragmentLists;
	/** 页面TAB视图 */
	private SlidingTabLayout slidingTabLayout;

	/** Tab的标签 */
	public enum TabTag {
		TV1, TV2
	}

	/**
	 * tab名称
	 */
	public final static String ARGUMENTS_TAB__NAME = "tabname";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		viewpager = (ViewPager) findViewById(R.id.viewpager);
		fragmentLists = new ArrayList<Fragment>();
		addFragment2List();
		FundPagerAdapter adapter = new FundPagerAdapter(
				getSupportFragmentManager(), fragmentLists);
		viewpager.setAdapter(adapter);

		slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		slidingTabLayout.setCustomTabView(R.layout.tab_indicator,
				android.R.id.text1);
		slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(
				R.color.title_text_color_selcted));
		slidingTabLayout.setDistributeEvenly(true);
		slidingTabLayout.setOnPageChangeListener(this);
		slidingTabLayout.setViewPager(viewpager);
	}

	private void addFragment2List() {
		fragmentLists.clear();
		fragmentLists.add(Tv1Fragment.getTv1Instance(TabTag.TV1));
		fragmentLists.add(Tv2Fragment.getTv2Instance(TabTag.TV2));
	}

	/***
	 * ViewPager的适配器
	 * 
	 * @author dell
	 * 
	 */
	class FundPagerAdapter extends FragmentPagerAdapter {
		/** ViewPager要显示的Fragment列表 */
		private ArrayList<Fragment> fragmentLists;

		public FundPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public FundPagerAdapter(FragmentManager fm,
				ArrayList<Fragment> fragmentLists) {
			super(fm);
			this.fragmentLists = fragmentLists;
		}

		// void setFragments(List<Fragment> fragments) {
		// fragmentLists.clear();
		// fragmentLists.addAll(fragments);
		// notifyDataSetChanged();
		// }

		@Override
		public Fragment getItem(int arg0) {
			return fragmentLists.get(arg0);
		}

		@Override
		public int getCount() {
			return fragmentLists.size();
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return getTabTitle(position);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		CharSequence getTabTitle(int position) {
			CharSequence mTabName = "";
			Bundle mTabBundle = fragmentLists.get(position).getArguments();
			if (null != mTabBundle) {
				mTabName = mTabBundle.getSerializable(ARGUMENTS_TAB__NAME)
						.toString();
			}
			return mTabName;
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
//		有三种状态（0，1，2）arg0 ==1的时候表示正在滑动，arg0==2的时候表示滑动完毕了，arg0==0的时候表示什么都没做
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
//		arg0 :当前页面，及你点击滑动的页面

//		arg1:当前页面偏移的百分比

//		arg2:当前页面偏移的像素位置
	}

	@Override
	public void onPageSelected(int arg0) {
		if (arg0 == 1) {

		}
	}

	@Override
	public void onClick(View v) {

	}

}
