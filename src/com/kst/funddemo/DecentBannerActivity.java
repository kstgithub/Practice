package com.kst.funddemo;

import java.util.ArrayList;
import java.util.List;

import com.kst.funddemo.customControls.DecentBanner;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;

/***
 * 广告位方式二
 * @author dell
 *
 */
public class DecentBannerActivity extends Activity {
	private DecentBanner decentBanner;
	private List<View> views;
    private List<String> titles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_decent_banner);
		
		decentBanner = (DecentBanner) findViewById(R.id.decent_banner);
		
		View view1 = getLayoutInflater().inflate(R.layout.decent_banner1, null);
		View view2 = getLayoutInflater().inflate(R.layout.decent_banner2, null);
		View view3 = getLayoutInflater().inflate(R.layout.decent_banner3, null);
		views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
		
		titles = new ArrayList<String>();
        titles.add("POPULAR");
        titles.add("IMAGE");
        titles.add("RECOMMEND");
        decentBanner.start(views, titles, 2, 500, null);
		
	}

}
