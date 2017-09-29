package com.kst.funddemo.fragments;

import com.kst.funddemo.MainActivity;
import com.kst.funddemo.R;
import com.kst.funddemo.MainActivity.TabTag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Tv4Fragment extends Fragment{
	
	public static Tv4Fragment getTv4Instance(TabTag tag) {
		Bundle args = new Bundle();
		args.putSerializable(MainActivity.ARGUMENTS_TAB__NAME, tag);
		Tv4Fragment tv4Fragment = new Tv4Fragment();
		tv4Fragment.setArguments(args);
		return tv4Fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tv4_fragment, container, false);
		return view;
	}
}
