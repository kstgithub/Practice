package com.kst.funddemo;

import com.kst.funddemo.customControls.ProgressButton;
import com.kst.funddemo.customControls.ViewPractice;

import android.app.Activity;
import android.os.Bundle;

/***
 * 带有进度文字的进度条
 * @author dell
 *
 */
public class ProgressActivity extends Activity{
	private ProgressButton pb;
	private ViewPractice vp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress);
		
		pb = (ProgressButton) findViewById(R.id.progressbar);
		pb.setProgress(80);
		pb.setText("进度");
		
		vp = (ViewPractice) findViewById(R.id.viewpractice);
		vp.setProgress(50);
		vp.setText("进度二");
		
	}
	
}
