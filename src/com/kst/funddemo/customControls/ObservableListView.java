package com.kst.funddemo.customControls;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于监听上下滑动的列表控件
 * 这个控件主要作用是根据滑动来连贯的去操作其他控件， 比如比较常用的向上滑动 标题根据手势逐渐消失   向下滑逐渐显示， 
 * 应用这个控件能很容易拿到 他的滑动状态 向上-向下-停止  滑动的距离， 所以在滑动的时候很容易控制其他布局控件
 */
public class ObservableListView extends ListView implements Scrollable {

    private int mPrevFirstVisiblePosition;
    private int mPrevFirstVisibleChildHeight = -1;
    private int mPrevScrolledChildrenHeight;
    private int mPrevScrollY;
    private int mScrollY;
    private SparseIntArray mChildrenHeights;

    private ObservableScrollViewCallbacks mCallbacks;
    private List<ObservableScrollViewCallbacks> mCallbackCollection;
    private ScrollState mScrollState;
    private boolean mFirstScroll;
    private boolean mDragging;
    private boolean mIntercepted;
    private MotionEvent mPrevMoveEvent;
    private ViewGroup mTouchInterceptionViewGroup;

    public enum ScrollState {
        STOP,

        UP,

        DOWN,
    }

    public interface ObservableScrollViewCallbacks {
        void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging,ScrollState scrollState);

        void onDownMotionEvent();

        void onUpOrCancelMotionEvent(ScrollState scrollState);
    }

    private OnScrollListener mOriginalScrollListener;
    private OnScrollListener mScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mOriginalScrollListener != null) {
                mOriginalScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mOriginalScrollListener != null) {
                mOriginalScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
            onScrollChanged();
        }
    };

    public ObservableListView(Context context) {
        super(context);
        init();
    }

    public ObservableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ObservableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        mPrevFirstVisiblePosition = ss.prevFirstVisiblePosition;
        mPrevFirstVisibleChildHeight = ss.prevFirstVisibleChildHeight;
        mPrevScrolledChildrenHeight = ss.prevScrolledChildrenHeight;
        mPrevScrollY = ss.prevScrollY;
        mScrollY = ss.scrollY;
        mChildrenHeights = ss.childrenHeights;
        super.onRestoreInstanceState(ss.getSuperState());
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.prevFirstVisiblePosition = mPrevFirstVisiblePosition;
        ss.prevFirstVisibleChildHeight = mPrevFirstVisibleChildHeight;
        ss.prevScrolledChildrenHeight = mPrevScrolledChildrenHeight;
        ss.prevScrollY = mPrevScrollY;
        ss.scrollY = mScrollY;
        ss.childrenHeights = mChildrenHeights;
        return ss;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (hasNoCallbacks()) {
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mFirstScroll = mDragging = true;
                dispatchOnDownMotionEvent();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (hasNoCallbacks()) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIntercepted = false;
                mDragging = false;
                dispatchOnUpOrCancelMotionEvent(mScrollState);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mPrevMoveEvent == null) {
                    mPrevMoveEvent = ev;
                }
                float diffY = ev.getY() - mPrevMoveEvent.getY();
                mPrevMoveEvent = MotionEvent.obtainNoHistory(ev);
                if (getCurrentScrollY() - diffY <= 0) {

                    if (mIntercepted) {
                        return false;
                    }

                    final ViewGroup parent;
                    if (mTouchInterceptionViewGroup == null) {
                        parent = (ViewGroup) getParent();
                    } else {
                        parent = mTouchInterceptionViewGroup;
                    }

                    float offsetX = 0;
                    float offsetY = 0;
                    for (View v = this; v != null && v != parent; ) {
                        offsetX += v.getLeft() - v.getScrollX();
                        offsetY += v.getTop() - v.getScrollY();
                        try {
                            v = (View) v.getParent();
                        } catch (ClassCastException ex) {
                            break;
                        }
                    }
                    final MotionEvent event = MotionEvent.obtainNoHistory(ev);
                    event.offsetLocation(offsetX, offsetY);

                    if (parent.onInterceptTouchEvent(event)) {
                        mIntercepted = true;

                        event.setAction(MotionEvent.ACTION_DOWN);

                        post(new Runnable() {
                            @Override
                            public void run() {
                                parent.dispatchTouchEvent(event);
                            }
                        });
                        return false;
                    }
                    return super.onTouchEvent(ev);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOriginalScrollListener = l;
    }

    @Override
    public void setScrollViewCallbacks(ObservableScrollViewCallbacks listener) {
        mCallbacks = listener;
    }

    @Override
    public void addScrollViewCallbacks(ObservableScrollViewCallbacks listener) {
        if (mCallbackCollection == null) {
            mCallbackCollection = new ArrayList<ObservableScrollViewCallbacks>();
        }
        mCallbackCollection.add(listener);
    }

    @Override
    public void removeScrollViewCallbacks(ObservableScrollViewCallbacks listener) {
        if (mCallbackCollection != null) {
            mCallbackCollection.remove(listener);
        }
    }

    @Override
    public void clearScrollViewCallbacks() {
        if (mCallbackCollection != null) {
            mCallbackCollection.clear();
        }
    }

    @Override
    public void setTouchInterceptionViewGroup(ViewGroup viewGroup) {
        mTouchInterceptionViewGroup = viewGroup;
    }

    @Override
    public void scrollVerticallyTo(int y) {
        View firstVisibleChild = getChildAt(0);
        if (firstVisibleChild != null) {
            int baseHeight = firstVisibleChild.getHeight();
            int position = y / baseHeight;
            setSelection(position);
        }
    }

    @Override
    public int getCurrentScrollY() {
        return mScrollY;
    }

    private void init() {
        mChildrenHeights = new SparseIntArray();
        super.setOnScrollListener(mScrollListener);
    }

    private void onScrollChanged() {
        if (hasNoCallbacks()) {
            return;
        }
        if (getChildCount() > 0) {
            int firstVisiblePosition = getFirstVisiblePosition();
            for (int i = getFirstVisiblePosition(), j = 0; i <= getLastVisiblePosition(); i++, j++) {
                if (mChildrenHeights.indexOfKey(i) < 0 || getChildAt(j).getHeight() != mChildrenHeights.get(i)) {
                    mChildrenHeights.put(i, getChildAt(j).getHeight());
                }
            }

            View firstVisibleChild = getChildAt(0);
            if (firstVisibleChild != null) {
                if (mPrevFirstVisiblePosition < firstVisiblePosition) {
                    int skippedChildrenHeight = 0;
                    if (firstVisiblePosition - mPrevFirstVisiblePosition != 1) {
                        for (int i = firstVisiblePosition - 1; i > mPrevFirstVisiblePosition; i--) {
                            if (0 < mChildrenHeights.indexOfKey(i)) {
                                skippedChildrenHeight += mChildrenHeights.get(i);
                            } else {
                                skippedChildrenHeight += firstVisibleChild.getHeight();
                            }
                        }
                    }
                    mPrevScrolledChildrenHeight += mPrevFirstVisibleChildHeight + skippedChildrenHeight;
                    mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
                } else if (firstVisiblePosition < mPrevFirstVisiblePosition) {
                    int skippedChildrenHeight = 0;
                    if (mPrevFirstVisiblePosition - firstVisiblePosition != 1) {
                        for (int i = mPrevFirstVisiblePosition - 1; i > firstVisiblePosition; i--) {
                            if (0 < mChildrenHeights.indexOfKey(i)) {
                                skippedChildrenHeight += mChildrenHeights.get(i);
                            } else {
                                skippedChildrenHeight += firstVisibleChild.getHeight();
                            }
                        }
                    }
                    mPrevScrolledChildrenHeight -= firstVisibleChild.getHeight() + skippedChildrenHeight;
                    mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
                } else if (firstVisiblePosition == 0) {
                    mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
                    mPrevScrolledChildrenHeight = 0;
                }
                if (mPrevFirstVisibleChildHeight < 0) {
                    mPrevFirstVisibleChildHeight = 0;
                }
                mScrollY = mPrevScrolledChildrenHeight - firstVisibleChild.getTop() +
                        firstVisiblePosition * getDividerHeight() + getPaddingTop();
                mPrevFirstVisiblePosition = firstVisiblePosition;



                if (mPrevScrollY < mScrollY) {
                    mScrollState = ScrollState.UP;
                } else if (mScrollY < mPrevScrollY) {
                    mScrollState = ScrollState.DOWN;
                } else {
                    mScrollState = ScrollState.STOP;
                }

                dispatchOnScrollChanged(mScrollY, mFirstScroll, mDragging,mScrollState);
                if (mFirstScroll) {
                    mFirstScroll = false;
                }
                mPrevScrollY = mScrollY;
            }
        }
    }

    private void dispatchOnDownMotionEvent() {
        if (mCallbacks != null) {
            mCallbacks.onDownMotionEvent();
        }
        if (mCallbackCollection != null) {
            for (int i = 0; i < mCallbackCollection.size(); i++) {
                ObservableScrollViewCallbacks callbacks = mCallbackCollection.get(i);
                callbacks.onDownMotionEvent();
            }
        }
    }

    private void dispatchOnScrollChanged(int scrollY, boolean firstScroll, boolean dragging,ScrollState scrollState) {
        if (mCallbacks != null) {
            mCallbacks.onScrollChanged(scrollY, firstScroll, dragging,scrollState);
        }
        if (mCallbackCollection != null) {
            for (int i = 0; i < mCallbackCollection.size(); i++) {
                ObservableScrollViewCallbacks callbacks = mCallbackCollection.get(i);
                callbacks.onScrollChanged(scrollY, firstScroll, dragging,scrollState);
            }
        }
    }

    private void dispatchOnUpOrCancelMotionEvent(ScrollState scrollState) {
        if (mCallbacks != null) {
            mCallbacks.onUpOrCancelMotionEvent(scrollState);
        }
        if (mCallbackCollection != null) {
            for (int i = 0; i < mCallbackCollection.size(); i++) {
                ObservableScrollViewCallbacks callbacks = mCallbackCollection.get(i);
                callbacks.onUpOrCancelMotionEvent(scrollState);
            }
        }
    }

    private boolean hasNoCallbacks() {
        return mCallbacks == null && mCallbackCollection == null;
    }

    static class SavedState extends BaseSavedState {
        int prevFirstVisiblePosition;
        int prevFirstVisibleChildHeight = -1;
        int prevScrolledChildrenHeight;
        int prevScrollY;
        int scrollY;
        SparseIntArray childrenHeights;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            prevFirstVisiblePosition = in.readInt();
            prevFirstVisibleChildHeight = in.readInt();
            prevScrolledChildrenHeight = in.readInt();
            prevScrollY = in.readInt();
            scrollY = in.readInt();
            childrenHeights = new SparseIntArray();
            final int numOfChildren = in.readInt();
            if (0 < numOfChildren) {
                for (int i = 0; i < numOfChildren; i++) {
                    final int key = in.readInt();
                    final int value = in.readInt();
                    childrenHeights.put(key, value);
                }
            }
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(prevFirstVisiblePosition);
            out.writeInt(prevFirstVisibleChildHeight);
            out.writeInt(prevScrolledChildrenHeight);
            out.writeInt(prevScrollY);
            out.writeInt(scrollY);
            final int numOfChildren = childrenHeights == null ? 0 : childrenHeights.size();
            out.writeInt(numOfChildren);
            if (0 < numOfChildren) {
                for (int i = 0; i < numOfChildren; i++) {
                    out.writeInt(childrenHeights.keyAt(i));
                    out.writeInt(childrenHeights.valueAt(i));
                }
            }
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}

