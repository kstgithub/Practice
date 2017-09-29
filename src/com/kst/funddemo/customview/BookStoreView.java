package com.kst.funddemo.customview;

import android.view.View;

import com.kst.funddemo.R;
import com.kst.funddemo.SlidingActivity;

/**
 * Created by dell on 2017/1/20.
 */

public class BookStoreView {
    /** 主界面Activity */
    private SlidingActivity mainActivity;
    /** 书城主视图view */
    private View bookStoreView = null;

    public BookStoreView(SlidingActivity mainActivity) {
        this.mainActivity = mainActivity;
        setUpBookShelfView();
    }

    private void setUpBookShelfView() {
        bookStoreView = View.inflate(mainActivity, R.layout.bookstore_layout,null);
    }

    /**
     * 获取书城view视图
     *
     * @return
     */
    public View getBookStoreView() {
        return bookStoreView;
    }
}
