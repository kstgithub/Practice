package com.kst.funddemo.model;

import com.kst.funddemo.common.util.Tools;

/**
 * Created by dell on 2016/5/12.
 */
public class BannerBean {

    private String url;
    private String img;

    public String getUrl() {
        return Tools.getValidUrl(url);
    }

    public void setUrl(String url) {

        this.url = url;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
