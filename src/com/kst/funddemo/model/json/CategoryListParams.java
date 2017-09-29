package com.kst.funddemo.model.json;

/**
 * Created by litao on 2016/5/31.
 */
public class CategoryListParams {

    public String id;
    public String thirdcategory;
    public String activitytype;
    public String bookstatus;
    public String sorttype;

    public CategoryListParams(String id){
        this.id = id;
        this.thirdcategory = "0";
        this.activitytype = "0";
        this.bookstatus = "0";
        this.sorttype = "0";

    }

    public CategoryListParams(String id,String thirdcategory,String activitytype,String bookstatus,String sorttype){
        this.id = id;
        this.thirdcategory = thirdcategory;
        this.activitytype = activitytype;
        this.bookstatus = bookstatus;
        this.sorttype = sorttype;
    }

}
