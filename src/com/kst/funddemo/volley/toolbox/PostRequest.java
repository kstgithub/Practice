package com.kst.funddemo.volley.toolbox;

import com.kst.funddemo.volley.*;
import com.kst.funddemo.volley.listener.Listener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class PostRequest extends Request<JSONObject> {
    private Map<String, String> mMap;
    private Listener<JSONObject> mListener;

    public PostRequest(String url, Listener<JSONObject> listener, Map<String, String> map) {
        super(Method.POST, url, listener);
        mListener = listener;
        mMap = map;
    }

    // mMap是已经按照前面的方式,设置了参数的实例
    @Override
    public Map<String, String> getParams() throws AuthFailureError {

        return mMap;
    }

    // 此处因为response返回值需要json数据,和JsonObjectRequest类一样即可
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onSuccess(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mListener.onError(error);
    }
}
