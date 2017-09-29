package com.kst.funddemo.fragments;

import com.kst.funddemo.MainActivity;
import com.kst.funddemo.R;
import com.kst.funddemo.MainActivity.TabTag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Tv3Fragment extends Fragment{
	
	public static Tv3Fragment getTv3Instance(TabTag tag) {
		Bundle args = new Bundle();
		args.putSerializable(MainActivity.ARGUMENTS_TAB__NAME, tag);
		Tv3Fragment tv3Fragment = new Tv3Fragment();
		tv3Fragment.setArguments(args);
		return tv3Fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tv3_fragment, container, false);
		return view;
	}
}
