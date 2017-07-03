package com.android.wwh.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

/**
 * Created by we-win on 2017/7/3.
 */

public class GsonRequest<T> extends Request<T> {
    private Response.Listener<T> mListener;
    private Gson mGson;
    private Class<T> mClass;

    public GsonRequest(String url,Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.GET,url,clazz, listener,errorListener);
    }

    public GsonRequest(int method, String url,Class<T> clazz, Response.Listener<T> listener,Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        mGson = new Gson();
        mClass = clazz;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        mListener = null;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String jsonString;
        try {
            jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(mGson.fromJson(jsonString,mClass), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }

    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }
}
