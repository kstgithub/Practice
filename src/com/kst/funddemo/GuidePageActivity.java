package com.kst.funddemo;

import java.util.ArrayList;

import com.kst.funddemo.fragments.ImageFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/***
 * 引导页
 * @author dell
 *
 */
public class GuidePageActivity extends FragmentActivity {
	private ViewPager viewPager;
	private ArrayList<ImageFragment> fragments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide_page);
		
		fragments = new ArrayList<ImageFragment>();
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		
		initFragments();
		viewPager.setAdapter(new GuidePagerAdapter(getSupportFragmentManager(), fragments));
	}

	private void initFragments() {
		fragments.clear();
		fragments.add(ImageFragment.newInstance("http://media.tadu.com/2016/05/10/8/2/9/8/82986a0b9c5c4e0d926f68878b58e750.jpg",false));
		fragments.add(ImageFragment.newInstance("http://media.tadu.com/2016/06/23/2/2/e/2/22e2382ac0ae4905a4805fc65a12538b.jpg",false));
		fragments.add(ImageFragment.newInstance("http://media.tadu.com/2016/05/19/f/3/1/a/f31ae84bd353414eb4360a2590196c69.jpg",true));
	}
	
	class GuidePagerAdapter extends FragmentPagerAdapter {
		private ArrayList<ImageFragment> fragmentLists;

		public GuidePagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		public GuidePagerAdapter(FragmentManager fm,
				ArrayList<ImageFragment> fragmentLists) {
			super(fm);
			this.fragmentLists = fragmentLists;
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragmentLists.get(arg0);
		}

		@Override
		public int getCount() {
			return fragmentLists.size();
		}
		
	}

}
