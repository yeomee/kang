package com.eastflag.kang.utils;

import android.content.Context;

/**
 * Created by eastflag on 2016-01-05.
 */
public class PreferenceUtil extends BasePreferenceUtil {
    private static PreferenceUtil sInstance = null;

    public static synchronized PreferenceUtil getInstance(Context context) {
        if (sInstance == null)
            sInstance = new PreferenceUtil(context);
        return sInstance;
    }

    private PreferenceUtil(Context context) {
        super(context);
    }

    public void putToken(String token) {
        put("token", token);
    }

    public String getToken() {
        return get("token");
    }
}
