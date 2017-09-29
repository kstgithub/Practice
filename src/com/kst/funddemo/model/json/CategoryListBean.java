package com.kst.funddemo.model.json;

import com.kst.funddemo.model.BaseBeen;

public class CategoryListBean extends BaseBeen {
    public CategoryListBean() {
        setUrl("/ci/categories/secondLevel/");
    }

    private String categoryid;

    private String thirdcategory;

    private String activitytype;

    private String bookstatus;

    private String sorttype;

    private String page;

    private CategoryListData data;

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getThirdcategory() {
        return thirdcategory;
    }

    public void setThirdcategory(String thirdcategory) {
        this.thirdcategory = thirdcategory;
    }

    public String getActivitytype() {
        return activitytype;
    }

    public void setActivitytype(String activitytype) {
        this.activitytype = activitytype;
    }

    public String getBookstatus() {
        return bookstatus;
    }

    public void setBookstatus(String bookstatus) {
        this.bookstatus = bookstatus;
    }

    public String getSorttype() {
        return sorttype;
    }

    public void setSorttype(String sorttype) {
        this.sorttype = sorttype;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public CategoryListData getData() {
        return data;
    }

    public void setData(CategoryListData data) {
        this.data = data;
    }
}
