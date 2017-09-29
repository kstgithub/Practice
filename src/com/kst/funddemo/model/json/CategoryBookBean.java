package com.kst.funddemo.model.json;

import android.text.TextUtils;
import java.util.Locale;

import com.kst.funddemo.common.util.Tools;

/**
 * Created by litao on 2016/5/17.
 */
public class CategoryBookBean {
    /**
     * 书籍ID
     */
    private String bookId;
    /**
     * 书名
     */
    private String title;
    /**
     * 作者
     */
    private String authors;
    /**
     * 简介
     */
    private String intro;
    /**
     * 书籍封面
     */
    private String coverImage;
    /**
     * 高清书籍封面
     */
    private String coverImageClear;
    /**
     * 二级分类
     */
    private String secondCategory ;
    /**
     * 三级分类
     */
    private String thirdCategory;
    /**
     * 人气
     */
    private String numOfPopularity;
    /**
     * 字数
     */
    private String numOfChars;
    /**
     * 跳转地址
     */
    private String url;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return TextUtils.isEmpty(authors)?"":"作者 : "+authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getCoverImageClear() {
        return coverImageClear;
    }

    public void setCoverImageClear(String coverImageClear) {
        this.coverImageClear = coverImageClear;
    }

    public String getSecondCategory() {
        return secondCategory;
    }

    public void setSecondCategory(String secondCategory) {
        this.secondCategory = secondCategory;
    }

    public String getThirdCategory() {
        return thirdCategory;
    }

    public void setThirdCategory(String thirdCategory) {
        this.thirdCategory = thirdCategory;
    }

    public String getNumOfPopularity() {
        return numOfPopularity;
    }

    public void setNumOfPopularity(String numOfPopularity) {
        this.numOfPopularity = numOfPopularity;
    }

    public String getNumOfChars() {
        return numOfChars;
    }

    public void setNumOfChars(String numOfChars) {
        this.numOfChars = numOfChars;
    }

    public String getUrl() {
        return Tools.getValidUrl(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
