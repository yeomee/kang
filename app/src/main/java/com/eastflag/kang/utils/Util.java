package com.eastflag.kang.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by eastflag on 2016-01-05.
 */
public final class Util {
    public static String getMdn(Context context) {
        TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        //2015-06-29 전화번호가 +82로 시작하는 경우 보정
        String number = tMgr.getLine1Number();
        if(number.startsWith("+82")) {
            number = number.replace("+82", "0");
        }
        return number;
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }


    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    private static Toast m_toast = null;
    public static void showToast(Context context, String text) {
        if (m_toast == null) {
            m_toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        m_toast.setText(text);
        m_toast.setDuration(Toast.LENGTH_SHORT);

        m_toast.show();
    }
}
