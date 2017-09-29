package com.kst.funddemo.fragments;

import com.kst.funddemo.MainActivity;
import com.kst.funddemo.MainActivity.TabTag;
import com.kst.funddemo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Tv1Fragment extends Fragment{
	
	public static Tv1Fragment getTv1Instance(TabTag tag) {
		Bundle args = new Bundle();
		args.putSerializable(MainActivity.ARGUMENTS_TAB__NAME, tag);
		Tv1Fragment tv1Fragment = new Tv1Fragment();
		tv1Fragment.setArguments(args);
		return tv1Fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tv1_fragment, container, false);
		return view;
	}
}
