package com.kst.funddemo.customControls;

import android.view.ViewGroup;

public interface Scrollable {
	@Deprecated
    void setScrollViewCallbacks(ObservableListView.ObservableScrollViewCallbacks listener);

    void addScrollViewCallbacks(ObservableListView.ObservableScrollViewCallbacks listener);

    void removeScrollViewCallbacks(ObservableListView.ObservableScrollViewCallbacks listener);

    void clearScrollViewCallbacks();

    void scrollVerticallyTo(int y);

    int getCurrentScrollY();

    void setTouchInterceptionViewGroup(ViewGroup viewGroup);
}
