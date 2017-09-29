package com.kst.funddemo.customview;

import android.view.View;

import com.kst.funddemo.R;
import com.kst.funddemo.SlidingActivity;

/**
 * Created by dell on 2017/1/20.
 */

public class BookBarView {
    /** 主界面Activity */
    private SlidingActivity mainActivity;
    /** 书城主视图view */
    private View bookBarView = null;

    public BookBarView(SlidingActivity mainActivity) {
        this.mainActivity = mainActivity;
        setUpBookShelfView();
    }

    private void setUpBookShelfView() {
        bookBarView = View.inflate(mainActivity, R.layout.bookbar_layout,null);
    }

    /**
     * 获取书城view视图
     *
     * @return
     */
    public View getBookBarView() {
        return bookBarView;
    }
}
