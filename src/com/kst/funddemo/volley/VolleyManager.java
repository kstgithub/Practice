package com.kst.funddemo.volley;

import com.kst.funddemo.common.application.ApplicationData;
import com.kst.funddemo.volley.toolbox.Volley;

/**
 * Volley请求网络的单例类
 */
public class VolleyManager {
    private static VolleyManager volleyManager;
    private RequestQueue mRequestQueue;

    public VolleyManager() {
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyManager getInstance() {
        if (volleyManager == null) {
            volleyManager = new VolleyManager();
        }
        return volleyManager;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(ApplicationData.globalContext);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}