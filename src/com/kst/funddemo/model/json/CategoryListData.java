package com.kst.funddemo.model.json;

import java.util.List;

/**
 * Created by litao on 2016/5/31.
 */
public class CategoryListData {
    /**
     * 页码
     */
    private int page;
    /**
     * 总页数
     */
    private int sumPage;
    /**
     * 书籍链表
     */
    private List<CategoryBookBean> bookList;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSumPage() {
        return sumPage;
    }

    public void setSumPage(int sumPage) {
        this.sumPage = sumPage;
    }

    public List<CategoryBookBean> getBookList() {
        return bookList;
    }

    public void setBookList(List<CategoryBookBean> bookList) {
        this.bookList = bookList;
    }
}
