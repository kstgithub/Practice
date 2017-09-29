package com.kst.funddemo.fragments;

import com.kst.funddemo.MainActivity;
import com.kst.funddemo.R;
import com.kst.funddemo.MainActivity.TabTag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Tv5Fragment extends Fragment{
	
	public static Tv5Fragment getTv5Instance(TabTag tag) {
		Bundle args = new Bundle();
		args.putSerializable(MainActivity.ARGUMENTS_TAB__NAME, tag);
		Tv5Fragment tv5Fragment = new Tv5Fragment();
		tv5Fragment.setArguments(args);
		return tv5Fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tv5_fragment, container, false);
		return view;
	}
}
