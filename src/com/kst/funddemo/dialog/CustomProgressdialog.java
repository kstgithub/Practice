package com.kst.funddemo.dialog;

import com.kst.funddemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

/**
 * 自定义滚动框
 *
 */
public class CustomProgressdialog extends CustomDialog {
	
	/**显示的信息*/
	private String message = null;
	/**是否取消*/
	private boolean cancelable = false;
	
	
	public CustomProgressdialog(Activity activity,String message,boolean cancelable,boolean isShow) {
		super(activity,R.style.TANCStyle);
		this.message = message;
		this.cancelable = cancelable;
		if(isShow){
			//显示弹窗之前，首先判断该Activity是否被销毁
			if (null != activity && !activity.isFinishing()) {
				show();
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCanceledOnTouchOutside(false);
		setCancelable(cancelable);
		setContentView(R.layout.dialog_progress_layout);
		TextView textView = (TextView) this.findViewById(R.id.dialog_progress_layout_tv_message);
		if(null!= message||""!=message){
			textView.setText(message);
		}
	}
	
	
}
