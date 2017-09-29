package com.kst.funddemo.fragments;

import com.bumptech.glide.Glide;
import com.kst.funddemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageFragment extends Fragment{
	/** URL */
    final static String ARGUMENTS_URL = "url";
    /** 是否是最后一张图 */
    final static String ARGUMENTS_LAST = "islast";
    private String url = null;
    private boolean isLast;
    
	/**
	 * 获取实例 BY url
	 *
	 * @param url 地址路径
	 * @return
	 */
	public static ImageFragment newInstance(String url,boolean isLast) {
		Bundle args = new Bundle();
		args.putString(ARGUMENTS_URL, url);
		args.putBoolean(ARGUMENTS_LAST, isLast);
		ImageFragment fragment = new ImageFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.guide_image_view, container, false);
		Bundle data = getArguments();
		if (null != data) {
			if (data.getString(ARGUMENTS_URL) != null) {
				url = data.getString(ARGUMENTS_URL);
			}
			isLast = data.getBoolean(ARGUMENTS_LAST);
		} 
		ImageView iv = (ImageView) view.findViewById(R.id.iv_guide);
		Glide.with(getActivity()).load(url).into(iv);
		Button btn = (Button) view.findViewById(R.id.btn_guide);
		if(isLast) {
			btn.setVisibility(View.VISIBLE);
		} else {
			btn.setVisibility(View.GONE);
		}
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "点击进入", Toast.LENGTH_SHORT).show();
			}
		});
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

}
