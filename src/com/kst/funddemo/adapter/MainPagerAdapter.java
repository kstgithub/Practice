package com.kst.funddemo.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Tab适配器
 */

public class MainPagerAdapter extends PagerAdapter {
    private List<View> mViews = new ArrayList<View>();

    public MainPagerAdapter(){

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

    public List<View> getViews(){
        return mViews;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mViews.size() > 0) {
            container.removeView(mViews.get(position));
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mViews.size() == 0) return null;
        View view = mViews.get(position);
        container.addView(view, 0);
        return view;

    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }
}
