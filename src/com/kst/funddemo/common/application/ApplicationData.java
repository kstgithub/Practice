package com.kst.funddemo.common.application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bumptech.glide.request.target.ViewTarget;
import com.kst.funddemo.R;

import android.app.Application;

public class ApplicationData extends Application{
	
	/** 全局上下文 */
	public static ApplicationData globalContext;
	
	public ExecutorService pool;
	public ExecutorService poolSingle;
	
	@Override
	public void onCreate() {
		super.onCreate();
		globalContext = this;
		/***
		 * 解决ViewBanner填充数据时的bug
		 * java.lang.IllegalArgumentException: You must not call setTag() on a view Glide is targeting
		 */
		ViewTarget.setTagId(R.id.glide_tag);
		pool = Executors.newCachedThreadPool();
		poolSingle = Executors.newSingleThreadScheduledExecutor();
	}
}
