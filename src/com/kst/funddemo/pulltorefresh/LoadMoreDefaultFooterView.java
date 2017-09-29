package com.kst.funddemo.pulltorefresh;

import com.kst.funddemo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoadMoreDefaultFooterView extends RelativeLayout implements LoadMoreUIHandler {

    private TextView mTextView;

    private boolean isHideNoMoreText = false;

    private ProgressBar mProgress;

    public void hideNoMoreText(boolean isHide) {
        isHideNoMoreText = isHide;
    }

    public LoadMoreDefaultFooterView(Context context) {
        this(context, null);
    }

    public LoadMoreDefaultFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreDefaultFooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupViews();
    }

    private void setupViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.load_more_default_footer, this);
        mTextView = (TextView) findViewById(R.id.cube_views_load_more_default_footer_text_view);
        mProgress = (ProgressBar)findViewById(R.id.progressBar);
    }

    @Override
    public void onLoading(LoadMoreContainer container) {
        setVisibility(VISIBLE);
        mTextView.setText(R.string.cube_views_load_more_loading);
        mProgress.setVisibility(VISIBLE);
    }

    @Override
    public void onLoadFinish(LoadMoreContainer container, boolean empty, boolean hasMore) {
        if (!hasMore) {
            setVisibility(VISIBLE);
            if (empty) {
                if (isHideNoMoreText) {
                    mTextView.setText("");
                } else {
                    mTextView.setText(R.string.cube_views_load_more_loaded_empty);
//                    mTextView.setText("");
                }
            } else {
                if (isHideNoMoreText) {
                    mTextView.setText(R.string.cube_views_load_more_loaded_no_more);
                } else {
                    mTextView.setText(R.string.cube_views_load_more_loaded_no_more);
//                    mTextView.setText("");
                }

            }
        } else {
            setVisibility(INVISIBLE);
        }
        mProgress.setVisibility(GONE);
    }

    @Override
    public void onWaitToLoadMore(LoadMoreContainer container) {
        setVisibility(VISIBLE);
        mTextView.setText(R.string.cube_views_load_more_click_to_load_more);
    }
}
