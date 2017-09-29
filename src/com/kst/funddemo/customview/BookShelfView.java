package com.kst.funddemo.customview;

import android.view.View;

import com.kst.funddemo.R;
import com.kst.funddemo.SlidingActivity;

/**
 * Created by dell on 2017/1/20.
 */

public class BookShelfView {
    /** 主界面Activity */
    private SlidingActivity mainActivity;
    /** 书架主视图view */
    private View bookShelfView = null;

    public BookShelfView(SlidingActivity mainActivity) {
        this.mainActivity = mainActivity;
        setUpBookShelfView();
    }

    private void setUpBookShelfView() {
        bookShelfView = View.inflate(mainActivity, R.layout.bookshelf_layout,null);
    }

    /**
     * 获取书架view视图
     *
     * @return
     */
    public View getBookShelfView() {
        return bookShelfView;
    }
}
