package com.kst.funddemo.common.communication;

import com.kst.funddemo.R;
import com.kst.funddemo.common.application.ApplicationData;
import com.kst.funddemo.common.exception.NetworkConnectException;
import com.kst.funddemo.common.exception.NetworkForceCloseException;
import com.kst.funddemo.common.exception.NetworkNotException;
import com.kst.funddemo.common.exception.NetworkTimeoutException;
import com.kst.funddemo.common.exception.ParseException;
import com.kst.funddemo.common.exception.SdcardException;
import com.kst.funddemo.common.util.AppConstant;
import com.kst.funddemo.common.util.Tools;
import com.kst.funddemo.dialog.CustomProgressdialog;
import com.kst.funddemo.dialog.CustomTextViewDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 请求任务类
 */
public abstract class RequestTask<T> {

	private Activity activity;
	/** 是否显示dialog */
	private boolean isShowDialog = false;
	/** 是否可以取消dialog */
	private boolean isCancelDialog = true;
	/** 出现错误时是否关闭当前界面 */
	private boolean isFinishActivity = false;
	/** dialog的显示信息 */
	private String dialogMessage = "";
	/** 联网中间层 */
	private RequestBean requestBean;
	/** 取消标记 */
	private boolean cancel = false;

	/**
	 * 初始化,不显示loading条
	 * 
	 * @param activity
	 */
	public RequestTask(Activity activity) {
		this.activity = activity;
	}

	/**
	 * 初始化,不显示loading条
	 * 
	 * @param activity
	 * @param isFinishActivity
	 *            是否结束activity
	 */
	public RequestTask(Activity activity, boolean isFinishActivity) {
		this.activity = activity;
		this.isFinishActivity = isFinishActivity;
	}

	/**
	 * 初始化，显示loading条
	 * 
	 * @param activity
	 * @param requestBean
	 *            请求bean
	 * @param dialogMessage
	 *            loading对话框显示内容
	 * @param isShowDialog
	 *            是否显示loading对话框
	 * @param isCancelDialog
	 *            是否可以取消
	 * @param isFinishActivity
	 *            出现错误时是否关闭当前界面
	 */
	public RequestTask(Activity activity, RequestBean requestBean,
			String dialogMessage, boolean isShowDialog, boolean isCancelDialog,
			boolean isFinishActivity) {
		this.activity = activity;
		this.requestBean = requestBean;
		this.isShowDialog = isShowDialog;
		this.isCancelDialog = isCancelDialog;
		this.dialogMessage = dialogMessage;
		this.isFinishActivity = isFinishActivity;
	}

	/**
	 * 初始化，显示loading条
	 * 
	 * @param requestBean
	 * @param dialogMessage
	 *            loading对话框显示内容
	 * @param isShowDialog
	 *            是否显示loading对话框
	 * @param isCancelDialog
	 *            是否可以取消
	 * @param isFinishActivity
	 *            出现错误时是否关闭当前界面
	 */
	public RequestTask(RequestBean requestBean, boolean isCancelDialog,
			boolean isFinishActivity) {
		this.requestBean = requestBean;
		this.isCancelDialog = isCancelDialog;
		this.isFinishActivity = isFinishActivity;
	}

	/**
	 * 开始联网
	 */
	@SuppressLint("NewApi")
	public void start() {
		if (android.os.Build.VERSION.SDK_INT < 11) {
			new MyAsyncTask().execute();
		} else {
			new MyAsyncTask().executeOnExecutor(
					ApplicationData.globalContext.pool);
		}
	}
	
	/**
	 * 开始联网
	 */
	@SuppressLint("NewApi")
	public void startSingleTask() {
		if (android.os.Build.VERSION.SDK_INT < 11) {
			new MyAsyncTask().execute();
		} else {
			new MyAsyncTask().executeOnExecutor(
					ApplicationData.globalContext.poolSingle);
		}
	}

	class MyAsyncTask extends AsyncTask<String, Integer, T> {
		private Exception myException = null;
		// 设置联网dialog
		private CustomProgressdialog pd;

		@Override
		protected void onPreExecute() {
			if (isShowDialog) {
				try {
					pd = new CustomProgressdialog(activity, dialogMessage,
							isCancelDialog, true);
					pd.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							cancel = true;
							requestBean.cancelConn();
							if (isFinishActivity) {
								activity.finish();
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				pd = null;
			}
		}

		@Override
		protected T doInBackground(String... params) {
			try {
				return onStartTask();
			} catch (Exception e) {
				myException = e;
			}
			return null;
		}

		protected void onPostExecute(T result) {
			if (!cancel) {
				try {
					if (pd != null && pd.isShowing() && null != activity && !activity.isFinishing()) {
						pd.dismiss();
					}
					if (myException == null) {
						onFinishTask(result);
					} else {
						onException(myException);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	/**
	 * 开始联网,注意：此方法中严禁出现try catch,全部throws Exception抛向上层处理 可以try catch特例：自动更新
	 * 
	 * @return 指定的类实例
	 * @throws Exception
	 */
	public abstract T onStartTask() throws Exception;

	/**
	 * 联网完成，处理一些UI事件，严禁放入耗时操作，如联网，操作数据库，操作sdcard
	 * 
	 * @param result
	 *            异步处理返回的对象
	 */
	public abstract void onFinishTask(T result);

	/**
	 * 统一的异常的处理方式，实现此方法，如果还需要基类处理统一错误，请添加super.handleException(e);
	 * 
	 * @param e
	 *            异步处理抛出的异常
	 */
	public void onException(Exception e) {
		if (e instanceof NetworkForceCloseException) {
			// 主动取消联网返回异常，不做任何处理
		} else if (e instanceof NetworkNotException) {
			showNoNetWorkDialog();
		} else if (e instanceof NetworkTimeoutException) {
			showRetryDialog("网络连接超时！");
		} else if (e instanceof NetworkConnectException) {
			showRetryDialog("网络连接错误！" + e.getMessage());
		} else if (e instanceof ParseException) {
			showRetryDialog("目前网络不稳定，加载失败，请重新加载。");
		} else if (e instanceof SdcardException) {
			SdcardException ex = (SdcardException) e;
			String message = "存储卡错误！";
			if (ex.getErrorCode() == SdcardException.SDCARD_ERROR) {
				message = Tools.getString(R.string.loading_sdcard_error);
			} else if (ex.getErrorCode() == SdcardException.SDCARD_FULL) {
				message = Tools.getString(R.string.loading_sdcard_full);
			}
			sdCardErrorDialog(message);
		} else {
			showRetryDialog(Tools.getString(R.string.connect_failed));
		}
	}

	/**
	 * 无网络对话框
	 */
	private void showNoNetWorkDialog() {
		final CustomTextViewDialog dialog = new CustomTextViewDialog(activity);
		dialog.setTitle(R.string.connect_message);
		dialog.setMessage(R.string.no_network);
		dialog.setCertainButton(R.string.setting, new OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
				Tools.openSystemNetworkSetting(activity);
				if (isFinishActivity) {
					activity.finish();
				}
			}
		});
		dialog.setCancelButton(R.string.cancel, new OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
				if (isFinishActivity) {
					activity.finish();
				}
			}
		});
		dialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.cancel();
					if (isFinishActivity) {
						activity.finish();
					}

					return true;
				}
				return false;
			}
		});
	}

	/**
	 * 重试对话框
	 * 
	 * @param message
	 *            提示的内容
	 */
	private void showRetryDialog(final String message) {
		final CustomTextViewDialog dialog = new CustomTextViewDialog(activity);
		dialog.setTitle(R.string.connect_message);
		dialog.setMessage(message);
		dialog.setCertainButton(R.string.retry, new OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
				start();
			}
		});
		dialog.setCancelButton(R.string.cancel, new OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
				// activity.getClass();

				if (isFinishActivity) {
					activity.finish();
				}
			}
		});
		dialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.cancel();
					if (isFinishActivity) {
						activity.finish();
					}
					return true;
				}
				return false;
			}
		});
		dialog.show();
	}

	/**
	 * 存储卡错误提示框
	 * 
	 * @param message
	 *            提示的话术
	 */
	private void sdCardErrorDialog(final String message) {
		final CustomTextViewDialog dialog = new CustomTextViewDialog(activity);
		dialog.setTitle(R.string.connect_message);
		dialog.setMessage(message);
		dialog.setCertainButton(R.string.certain, new OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
				if (isFinishActivity) {
					activity.finish();
				}
			}
		});
		dialog.setCancelButton(R.string.cancel, new OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
				if (isFinishActivity) {
					activity.finish();
				}
			}
		});
		dialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.cancel();
					if (isFinishActivity) {
						activity.finish();
					}
					return true;
				}
				return false;
			}
		});
		dialog.show();
	}

	/**
	 * 无网络对话框
	 */
	private void showNoSessionDialog() {
		final CustomTextViewDialog dialog = new CustomTextViewDialog(activity);
		dialog.setTitle(R.string.connect_message);
		dialog.setMessage(R.string.no_session);
		dialog.setCertainButton("去登录", new OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
//				Intent intent = new Intent(activity, LoginActivity.class);
//				activity.startActivity(intent);
				if (isFinishActivity) {
					activity.finish();
				}
			}
		});
		dialog.setCancelButton(R.string.cancel, new OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
				if (isFinishActivity) {
					activity.finish();
				}
			}
		});
		dialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.cancel();
					if (isFinishActivity) {
						activity.finish();
					}

					return true;
				}
				return false;
			}
		});
	}

}
