package com.kst.funddemo.dialog;

import com.kst.funddemo.R;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 自定义纯文本显示对话框
 * 
 * @author wangyue
 * 
 */
public class CustomTextViewDialog extends CustomDialog {

	private ImageView imageview_icon;
	private TextView textview_title;
	private TextView textview_content;
	private Button certainButton;
	private Button cancelButton;

	public CustomTextViewDialog(Context context) {
		super(context, R.style.TANUNCStyle);
		setCancelable(false);
		setCanceledOnTouchOutside(true);
		show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_textview_layout);
		textview_title = (TextView) this
				.findViewById(R.id.dialog_textview_layout_tv_title);
		textview_content = (TextView) this
				.findViewById(R.id.dialog_textview_layout_tv_content);
		textview_content.setMovementMethod(ScrollingMovementMethod
				.getInstance());
		certainButton = (Button) this
				.findViewById(R.id.dialog_textview_layout_btn_1);
		cancelButton = (Button) this
				.findViewById(R.id.dialog_textview_layout_btn_2);
	}

	/**
	 * 得到内容TextView
	 * 
	 * @return
	 */
	public TextView getContentTextview() {
		return textview_content;
	}

	/**
	 * 设置标题图标
	 * 
	 * @param id
	 */
	public void setIcon(int id) {
		if (imageview_icon != null) {
			imageview_icon.setImageResource(id);
		}
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		if (textview_title != null) {
			textview_title.setText(title);
		}
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(int titleId) {
		if (textview_title != null) {
			textview_title.setText(titleId);
		}
	}
	/**
	 * 设置标题
	 *
	 * @param color
	 */
	public void setTitleColor(int color) {
		if (textview_title != null) {
			textview_title.setTextColor(color);
		}
	}

	/**
	 * 设置内容
	 * 
	 * @param content
	 */
	public void setMessage(CharSequence message) {
		if (textview_content != null) {
			textview_content.setText(message);
		}
	}

	/**
	 * 设置内容
	 * 
	 * @param content
	 */
	public void setMessage(int messageId) {
		if (textview_content != null) {
			textview_content.setText(messageId);
		}
	}

	public void showCertainButton(boolean flag) {
		if (flag) {
			if (certainButton != null) {
				certainButton.setVisibility(View.VISIBLE);
			}
		} else {
			if (certainButton != null) {
				certainButton.setVisibility(View.GONE);
			}
		}
	}

	public void showButtonVisable() {
		certainButton.setVisibility(View.INVISIBLE);
		cancelButton.setVisibility(View.INVISIBLE);
	}

	/**
	 * 设置确定按键
	 * 
	 * @param text
	 * @param onClickListener
	 */
	public void setCertainButton(int id, View.OnClickListener onClickListener) {
		if (certainButton != null) {
			certainButton.setText(id);
			certainButton.setOnClickListener(onClickListener);
		}
	}

	/**
	 * 设置确定按键
	 * 
	 * @param text
	 * @param onClickListener
	 */
	public void setCertainButton(String id, View.OnClickListener onClickListener) {
		if (certainButton != null) {
			certainButton.setText(id);
			certainButton.setOnClickListener(onClickListener);
		}
	}

	/**
	 * 设置取消按键
	 * 
	 * @param text
	 * @param onClickListener
	 */
	public void setCancelButton(int id, View.OnClickListener onClickListener) {
		if (cancelButton != null) {
			cancelButton.setText(id);
			cancelButton.setOnClickListener(onClickListener);
		}
	}

	/**
	 * 设置取消按键
	 * 
	 * @param text
	 * @param onClickListener
	 */
	public void setCancelButton(String id, View.OnClickListener onClickListener) {
		if (cancelButton != null) {
			cancelButton.setText(id);
			cancelButton.setOnClickListener(onClickListener);
		}
	}
}
