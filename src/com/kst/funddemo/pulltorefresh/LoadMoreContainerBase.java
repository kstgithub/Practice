package com.kst.funddemo.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;

public abstract class LoadMoreContainerBase extends LinearLayout implements LoadMoreContainer {

    private AbsListView.OnScrollListener mOnScrollListener;
    private LoadMoreUIHandler mLoadMoreUIHandler;
    private LoadMoreHandler mLoadMoreHandler;

    private boolean mIsLoading;
    private boolean mHasMore = true;
    private boolean mAutoLoadMore = true;
    private boolean mShowLoadingForFirstPage = true;
    private View mFooterView;

    private AbsListView mAbsListView;
    private boolean mListEmpty = true;

    private boolean isHideNoMoreText=true;


    public LoadMoreContainerBase(Context context) {
        super(context);
    }

    public LoadMoreContainerBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAbsListView = retrieveAbsListView();
        init();
    }

    public void hideNoMoreText(boolean isHide){
        isHideNoMoreText=isHide;
    }

    public void useDefaultHeader() {
        LoadMoreDefaultFooterView footerView = new LoadMoreDefaultFooterView(getContext());
        footerView.hideNoMoreText(isHideNoMoreText);
        footerView.setVisibility(GONE);
        setLoadMoreView(footerView);
        setLoadMoreUIHandler(footerView);
    }

    private void init() {

        if (mFooterView != null) {
            addFooterView(mFooterView);
        }

        mAbsListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private boolean mIsEnd = false;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        break;
                }
                if (null != mOnScrollListener) {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (mIsEnd) {
                        onReachBottom();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (null != mOnScrollListener) {
                    mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
                if (firstVisibleItem + visibleItemCount >= totalItemCount - 1) {
                    mIsEnd = true;
                } else {
                    mIsEnd = false;
                }
            }
        });

        useDefaultHeader();

    }

    private void performLoadMore() {
        if (mIsLoading || !mHasMore) {
            return;
        }

        mIsLoading = true;

        if (mLoadMoreUIHandler != null && (!mListEmpty || mShowLoadingForFirstPage)) {
            mLoadMoreUIHandler.onLoading(this);
        }
        if (null != mLoadMoreHandler) {
            mLoadMoreHandler.onLoadMore(this);
        }
    }

    private void onReachBottom() {
        if (mAutoLoadMore) {
            performLoadMore();
        } else {
            if (mHasMore) {
                mLoadMoreUIHandler.onWaitToLoadMore(this);
            }
        }
    }

    @Override
    public void setShowLoadingForFirstPage(boolean showLoading) {
        mShowLoadingForFirstPage = showLoading;
    }

    @Override
    public void setAutoLoadMore(boolean autoLoadMore) {
        mAutoLoadMore = autoLoadMore;
    }

    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        mOnScrollListener = l;
    }

    @Override
    public void setLoadMoreView(View view) {
        if (mAbsListView == null) {
            mFooterView = view;
            return;
        }
        if (mFooterView != null && mFooterView != view) {
            removeFooterView(view);
        }

        mFooterView = view;
        mFooterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                performLoadMore();
            }
        });

        addFooterView(view);
    }

    @Override
    public void setLoadMoreUIHandler(LoadMoreUIHandler handler) {
        mLoadMoreUIHandler = handler;
    }

    @Override
    public void setLoadMoreHandler(LoadMoreHandler handler) {
        mLoadMoreHandler = handler;
    }

    @Override
    public void loadMoreFinish(boolean emptyResult, boolean hasMore) {
        mListEmpty = emptyResult;
        mIsLoading = false;
        mHasMore = hasMore;

        if (mLoadMoreUIHandler != null) {
            mLoadMoreUIHandler.onLoadFinish(this, emptyResult, hasMore);
        }
    }

    protected abstract void addFooterView(View view);

    protected abstract void removeFooterView(View view);

    protected abstract AbsListView retrieveAbsListView();
}
