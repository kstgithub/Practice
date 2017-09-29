package com.kst.funddemo.presenter;

public interface BasePresenter {
	public void loadPullUpFinish(Object object);
    public void loadPullDownFinish(Object object);
    public void loadFailed(int code);
}
