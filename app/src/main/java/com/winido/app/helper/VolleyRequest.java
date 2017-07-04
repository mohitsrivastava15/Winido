package com.winido.app.helper;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by mohit on 12/6/17.
 */

public class VolleyRequest extends Application {
    public static final String TAG = VolleyRequest.class
            .getSimpleName();

    private RequestQueue mRequestQueue;

    private static VolleyRequest mInstance;

    private Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    private VolleyRequest(Context ctx) {
        this.appContext = ctx;
    }

    public static synchronized VolleyRequest getInstance(Context appContext) {
        if(mInstance == null)
            mInstance = new VolleyRequest(appContext);
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(appContext);
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
