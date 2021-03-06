package com.kst.funddemo.fragments;

import com.kst.funddemo.MainActivity;
import com.kst.funddemo.R;
import com.kst.funddemo.MainActivity.TabTag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Tv2Fragment extends Fragment{
	
	public static Tv2Fragment getTv2Instance(TabTag tag) {
		Bundle args = new Bundle();
		args.putSerializable(MainActivity.ARGUMENTS_TAB__NAME, tag);
		Tv2Fragment tv2Fragment = new Tv2Fragment();
		tv2Fragment.setArguments(args);
		return tv2Fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tv2_fragment, container, false);
		return view;
	}
}
