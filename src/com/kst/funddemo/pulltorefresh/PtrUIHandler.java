package com.kst.funddemo.pulltorefresh;

import com.kst.funddemo.pulltorefresh.indicator.PtrIndicator;

public interface PtrUIHandler {

    public void onUIReset(PtrFrameLayout frame);

    public void onUIRefreshPrepare(PtrFrameLayout frame);

    public void onUIRefreshBegin(PtrFrameLayout frame);

    public void onUIRefreshComplete(PtrFrameLayout frame);

    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator);
}
