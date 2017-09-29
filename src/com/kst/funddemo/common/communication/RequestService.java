package com.kst.funddemo.common.communication;

import com.kst.funddemo.common.util.AppConstant;
import com.kst.funddemo.model.BaseBeen;
import com.kst.funddemo.model.CallBackInterface;

import android.app.Activity;

public class RequestService {
	
	/** 下一层联网类 */
	private RequestBean requestBean = new RequestBean();
	
	/**
	 * 通用网络连接类，参考同名函数，此处最后一个参数默认为false
	 * 
	 * @param cb
	 * @param been
	 * @param activity
	 * @param dialogMessage
	 * @param isShowDialog
	 * @param isCancelDialog
	 * @param isFinishActivity
	 * @param isGet
	 */
	public void executeCommon(final CallBackInterface cb, final BaseBeen been,
			final Activity activity, String dialogMessage,
			boolean isShowDialog, boolean isCancelDialog,
			boolean isFinishActivity, final boolean isGet) {
		executeCommon(cb, been, activity, dialogMessage, isShowDialog,
				isCancelDialog, isFinishActivity, isGet, false);
	}

	/**
	 * 通用网络连接类
	 * 
	 * @param cb
	 *            回调
	 * @param been
	 *            请求实体
	 * @param activity
	 *            上下文
	 * @param dialogMessage
	 *            等待框显示文字
	 * @param isShowDialog
	 *            是否显示等待框
	 * @param isCancelDialog
	 *            是否可取消
	 * @param isFinishActivity
	 *            错误是否关闭页面
	 * @param isGet
	 *            是否用get方式请求： true-get false-post
	 * @param isIgnoreError
	 *            是否忽略错误，如果忽略则不会报告任何错误
	 */
	public void executeCommon(final CallBackInterface cb, final BaseBeen been,
			final Activity activity, String dialogMessage,
			boolean isShowDialog, boolean isCancelDialog,
			boolean isFinishActivity, final boolean isGet,
			final boolean isIgnoreError) {
		new RequestTask<BaseBeen>(activity, requestBean, dialogMessage,
				isShowDialog, isCancelDialog, isFinishActivity) {

			@Override
			public BaseBeen onStartTask() throws Exception {
				BaseBeen result = new BaseBeen();
				try {
					if (isGet) {
						result = requestBean.doGet(been);
					} else {
						result = requestBean.doPost(been);
					}
					if (isIgnoreError) {
						return result;
					} else if (result.getCode() == AppConstant.SUCCESS_OPERATION) {
						return result;
					}
				} catch (Exception e) {
					if (isIgnoreError) {

					} else {
						throw e;
					}
				}
				return result;
			}

			@Override
			public void onFinishTask(BaseBeen result) {
				cb.callBack(result);
			}
		}.start();
	}

}
