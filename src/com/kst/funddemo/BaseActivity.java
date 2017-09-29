package com.kst.funddemo;

import android.os.Bundle;
import android.view.Window;
import android.app.Activity;
import android.content.Intent;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		this.overridePendingTransition(R.anim.anim_popup_down_enter, R.anim.slide_out_left);
	}
	
	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.slide_out_left, R.anim.anim_popup_down_exit);
	}
	
}
