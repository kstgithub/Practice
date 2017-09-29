package com.kst.funddemo.volley.model;

import org.json.JSONObject;

/**
 * @author dongchenwanyan
 * @description 网络请求数据返回的bean
 * @date 16/04/13
 */
public class ResultData {
    public int code;
    public String data;

    /**
     * 返回异常信息时候的状态码  {"msg":"您的登录状态已经过期，请重新登录","stat":152}
     */
    public int stat;

    public void parseJson(JSONObject jsonObject) {
        code = jsonObject.optInt("code");
        stat = jsonObject.optInt("stat");
        data = jsonObject.optString("data");
    }
}