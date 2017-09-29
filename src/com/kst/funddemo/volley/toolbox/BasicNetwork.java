/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kst.funddemo.volley.toolbox;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.protocol.HTTP;

import android.os.SystemClock;
import android.util.Log;

import com.kst.funddemo.common.util.Tools;
import com.kst.funddemo.volley.AuthFailureError;
import com.kst.funddemo.volley.Cache.Entry;
import com.kst.funddemo.volley.Network;
import com.kst.funddemo.volley.NetworkError;
import com.kst.funddemo.volley.NetworkResponse;
import com.kst.funddemo.volley.NoConnectionError;
import com.kst.funddemo.volley.Request;
import com.kst.funddemo.volley.ResponseDelivery;
import com.kst.funddemo.volley.RetryPolicy;
import com.kst.funddemo.volley.ServerError;
import com.kst.funddemo.volley.TimeoutError;
import com.kst.funddemo.volley.VolleyError;
import com.kst.funddemo.volley.VolleyLog;

/**
 * A network performing Volley requests over an {@link HttpStack}.
 */
public class BasicNetwork implements Network {
    protected static final boolean DEBUG = VolleyLog.DEBUG;

    private static int SLOW_REQUEST_THRESHOLD_MS = 3000;

    private static int DEFAULT_POOL_SIZE = 4096;

    protected final HttpStack mHttpStack;

    protected final ByteArrayPool mPool;

    /**
     * Request delivery mechanism.
     */
    private static ResponseDelivery mDelivery;

    /**
     * @param httpStack HTTP stack to be used
     */
    public BasicNetwork(HttpStack httpStack) {
        // If a pool isn't passed in, then build a small default pool that will give us a lot of
        // benefit and not use too much memory.
        this(httpStack, new ByteArrayPool(DEFAULT_POOL_SIZE));
    }

    /**
     * @param httpStack HTTP stack to be used
     * @param pool      a buffer pool that improves GC performance in copy operations
     */
    public BasicNetwork(HttpStack httpStack, ByteArrayPool pool) {
        mHttpStack = httpStack;
        mPool = pool;
    }

    @Override
    public void setDelivery(ResponseDelivery delivery) {
        mDelivery = delivery;
    }

    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        long requestStart = SystemClock.elapsedRealtime();
        while (true) {
            HttpResponse httpResponse = null;
            byte[] responseContents = null;
            Map<String, String> responseHeaders = Collections.emptyMap();
            try {
                // Gather headers.
                Map<String, String> headers = new HashMap<String, String>();
                addCacheHeaders(headers, request);
                httpResponse = mHttpStack.performRequest(request, headers);
                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                //显示post请求的参数
                Map<String, String> map = request.getParams();
                String params ="";
                if (map != null) {
                    Set<String> keys = map.keySet();
                    for (String key : keys) {
                        params=params+key + "=" + map.get(key)+"#";
                    }
                }
                Log.e("volley_request_url=",request.getUrl()+params);
                responseHeaders = convertHeaders(httpResponse.getAllHeaders());
                // Handle cache validation.
                if (statusCode == HttpStatus.SC_NOT_MODIFIED) {

                    Entry entry = request.getCacheEntry();
                    if (entry == null) {
                        return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, null,
                                responseHeaders, true,
                                SystemClock.elapsedRealtime() - requestStart);
                    }
                    // A HTTP 304 response does not have all header fields. We
                    // have to use the header fields from the cache entry plus
                    // the new ones from the response.
                    // http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
                    entry.responseHeaders.putAll(responseHeaders);
                    return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, entry.data,
                            entry.responseHeaders, true,
                            SystemClock.elapsedRealtime() - requestStart);
                }

                // Some responses such as 204s do not have content.  We must check.
                if (httpResponse.getEntity() != null) {
                    if (request instanceof FileDownloadRequest)
                        responseContents = request.handleResponse(httpResponse, mDelivery);
                    else
                        responseContents = entityToBytes(httpResponse.getEntity());
                } else {
                    // Add 0 byte response as a way of honestly representing a
                    // no-content request.
                    responseContents = new byte[0];
                }

                // if the request is slow, log it.
                long requestLifetime = SystemClock.elapsedRealtime() - requestStart;
                logSlowRequests(requestLifetime, request, responseContents, statusLine);
                if (statusCode < 200 || statusCode > 299) {
                    throw new IOException();
                }
                return new NetworkResponse(statusCode, responseContents, responseHeaders, false,
                        SystemClock.elapsedRealtime() - requestStart);
            } catch (SocketTimeoutException e) {
                attemptRetryOnException("socket", request, new TimeoutError());
            } catch (ConnectTimeoutException e) {
                attemptRetryOnException("connection", request, new TimeoutError());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Bad URL " + request.getUrl(), e);
            } catch (IOException e) {
                int statusCode = 0;
                NetworkResponse networkResponse = null;
                if (httpResponse != null) {
                    statusCode = httpResponse.getStatusLine().getStatusCode();
                } else {
                    throw new NoConnectionError(e);
                }
                VolleyLog.e("Unexpected response code %d for %s", statusCode, request.getUrl());
                if (responseContents != null) {
                    networkResponse = new NetworkResponse(statusCode, responseContents,
                            responseHeaders, false, SystemClock.elapsedRealtime() - requestStart);
                    if (statusCode == HttpStatus.SC_UNAUTHORIZED ||
                            statusCode == HttpStatus.SC_FORBIDDEN) {
                        attemptRetryOnException("auth",
                                request, new AuthFailureError(networkResponse));
                    } else {
                        // TODO: Only throw ServerError for 5xx status codes.
                        throw new ServerError(networkResponse);
                    }
                } else {
                    throw new NetworkError(networkResponse);
                }
            }
        }
    }

    /**
     * Logs requests that took over SLOW_REQUEST_THRESHOLD_MS to complete.
     */
    private void logSlowRequests(long requestLifetime, Request<?> request,
                                 byte[] responseContents, StatusLine statusLine) {
        if (DEBUG || requestLifetime > SLOW_REQUEST_THRESHOLD_MS) {
            VolleyLog.d("HTTP response for request=<%s> [lifetime=%d], [size=%s], " +
                            "[rc=%d], [retryCount=%s]", request, requestLifetime,
                    responseContents != null ? responseContents.length : "null",
                    statusLine.getStatusCode(), request.getRetryPolicy().getCurrentRetryCount());
        }
    }

    /**
     * Attempts to prepare the request for a retry. If there are no more attempts remaining in the
     * request's retry policy, a timeout exception is thrown.
     *
     * @param request The request to use.
     */
    private static void attemptRetryOnException(String logPrefix, Request<?> request,
                                                VolleyError exception) throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int oldTimeout = request.getTimeoutMs();

        try {
            retryPolicy.retry(exception);
        } catch (VolleyError e) {
            request.addMarker(
                    String.format("%s-timeout-giveup [timeout=%s]", logPrefix, oldTimeout));
            throw e;
        }
        request.addMarker(String.format("%s-retry [timeout=%s]", logPrefix, oldTimeout));
        mDelivery.postRetry(request);
    }

    private void addCacheHeaders(Map<String, String> headers, Request request) throws AuthFailureError {
        // If there's no cache entry, we're done.
        if (request == null) {
            return;
        }

        if (request.getCacheEntry() != null) {
            if (request.getCacheEntry().etag != null) {
                headers.put("If-None-Match", request.getCacheEntry().etag);
            }

            if (request.getCacheEntry().lastModified > 0) {
                Date refTime = new Date(request.getCacheEntry().lastModified);
                headers.put("If-Modified-Since", DateUtils.formatDate(refTime));
            }
        }
        //TODO 模拟添加请求头
        headers.put("X-Client", getXClient(request.getParams()));
        headers.putAll(addApplicationHeaders());
    }

    //TODO 添加自己应用的请求头
    public Map<String, String> addApplicationHeaders() {
        Map<String, String> appHeader = new HashMap<String, String>();
        appHeader.put("COOKIE", "sessionid=86f6124e0ee6426da63cf3ee3cc93d8c");
        return appHeader;
    }

    /**
     * 获得上传的X-Client信息
     *
     * @return xClient信息
     */
    public static String getXClient(Map<String, String> map) {

        String sdk = "";
        try {
            sdk = URLEncoder.encode(Tools.getSDK(), HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String type = "";
        try {
            type = URLEncoder.encode(Tools.getPhoneModel(), HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String rootPath = "";
        try {
            rootPath = URLEncoder.encode((Tools.getRootPath()), HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String screenSize = Tools.getScreenWidth() + "*"
                + Tools.getScreenHeight();
        String imei = Tools.getIMEI();
        String imsi = Tools.getIMSI();
        String mac = Tools.getMacAddress();
        // String cell_id = Tools.getNetWorkBaseStation();
        String version = "6.3.50.12";

        String rn = "";
        try {
            rn = URLEncoder.encode(Tools.getRandomNumber(), HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String ipAddress = "";
//        if(Tools.isUseVmIp()){
//            if(ToolsFile.isFileExit(AppConstant.vmIpFile)){
//                File ipfile = new File(AppConstant.vmIpFile);
//                try {
//                    InputStream in = new FileInputStream(ipfile);
//                    ipAddress = new String(ToolsFile.readBytes(in));
//                } catch (FileNotFoundException e) {
//                    ipAddress = "";
//                } catch (IOException e1) {
//                    ipAddress = "";
//                }
//            }
//        }

        String xClient = "sdk="
                + sdk
                + ";"
                + "screenSize="
                + screenSize
                + ";"
                + "type="
                + type
                + ";"
                + "imei="
                + imei
                + ";"
                + "imsi="
                + imsi
                + ";"
                + "version="
                + version
                + ";"
                + "mac="
                + mac
                + ";"
                + "rootPath="
                + rootPath
                + ";"
                + "rn="
                + rn
                + ";"
                + "tdcn="
                + "aeb56195628ff9b7e045e0efb0f801fd" + ";"
                + "hotfix="    //xclient中添加hotfix字段，内部版本号，用于区分一个版本多次灰度和正式包
                + "1"
                + ";"
                + "ip="
                + ipAddress
                + ";";

        return xClient;
    }

    protected void logError(String what, String url, long start) {
        long now = SystemClock.elapsedRealtime();
        VolleyLog.v("HTTP ERROR(%s) %d ms to fetch %s", what, (now - start), url);
    }

    /**
     * Reads the contents of HttpEntity into a byte[].
     */
    private byte[] entityToBytes(HttpEntity entity) throws IOException, ServerError {
        PoolingByteArrayOutputStream bytes =
                new PoolingByteArrayOutputStream(mPool, (int) entity.getContentLength());
        byte[] buffer = null;
        try {
            InputStream in = entity.getContent();
            if (in == null) {
                throw new ServerError();
            }
            buffer = mPool.getBuf(1024);
            int count;
            while ((count = in.read(buffer)) != -1) {
                bytes.write(buffer, 0, count);
            }
            return bytes.toByteArray();
        } finally {
            try {
                // Close the InputStream and release the resources by "consuming the content".
                entity.consumeContent();
            } catch (IOException e) {
                // This can happen if there was an exception above that left the entity in
                // an invalid state.
                VolleyLog.v("Error occured when calling consumingContent");
            }
            mPool.returnBuf(buffer);
            bytes.close();
        }
    }

    /**
     * Converts Headers[] to Map<String, String>.
     */
    protected static Map<String, String> convertHeaders(Header[] headers) {
        Map<String, String> result = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < headers.length; i++) {
            result.put(headers[i].getName(), headers[i].getValue());
        }
        return result;
    }
}
