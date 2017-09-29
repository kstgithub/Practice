package com.kst.funddemo.dialog;

import com.kst.funddemo.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * 自定义对话框
 * 
 * @author wangyue
 * 
 */
public class CustomDialog extends Dialog {

	/** 显示的视图 */
	private View view = null;

	/***
	 * 屏幕变暗
	 * 
	 * @param context
	 */
	public CustomDialog(Context context) {
		super(getParent(context));
		getWindow().setBackgroundDrawableResource(R.drawable.blank);
	}

	/***
	 * 屏幕不变暗
	 * 
	 * @param context
	 * @param style
	 */
	public CustomDialog(Context context, int style) {
		// 设置R.style.TANCStyle是为了让弹出的Dialog能否不使屏幕变暗
		super(getParent(context), style);
		getWindow().setBackgroundDrawableResource(R.drawable.blank);
	}

	private static Context getParent(Context context) {
		Context re = context;
		if (context instanceof Activity) {
			Activity a = ((Activity) context);
			if (a.isChild()) {
				a = (Activity) getParent(a.getParent());
			}
			re = a;
		}
		return re;
	}

	/**
	 * 设置显示的VIew
	 * 
	 * @param view
	 */
	public void setView(View view) {
		this.view = view;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (view != null) {
			setContentView(view);			
		}
		setCanceledOnTouchOutside(true);
	}

}
