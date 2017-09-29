package com.kst.funddemo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class SlidePagerAdapter extends PagerAdapter {

	private List<View> mViews = new ArrayList<View>();

	public SlidePagerAdapter() {

	}

	public void addViews(List<View> list) {
		mViews.clear();
		mViews.addAll(list);
		notifyDataSetChanged();
	}

	public void addView(View mView) {
		this.mViews.add(mView);
		notifyDataSetChanged();
	}

	public List<View> getViews() {
		return mViews;
	}

	public int getRealCount() {
		return mViews.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (mViews.size() > 0) {
			container.removeView(mViews.get(position % mViews.size()));
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (mViews.size() == 0)
			return null;
		View view = mViews.get(position % mViews.size());
		container.addView(view, 0);
		return view;

	}

	@Override
	public int getCount() {
		return mViews.size() > 0 ? Integer.MAX_VALUE : mViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

}
