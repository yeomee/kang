package com.eastflag.kang.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.eastflag.kang.Constant;
import com.eastflag.kang.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by eastflag on 2016-01-05.
 */
public final class Util {
    public static final int MAX_IMAGE_SIZE = 1000;

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

    public static void showNetworkError(Activity activity) {
        if (m_toast == null) {
            m_toast = Toast.makeText(activity, activity.getString(R.string.network_error), Toast.LENGTH_SHORT);
        }
        m_toast.setText(activity.getString(R.string.network_error));
        m_toast.setDuration(Toast.LENGTH_LONG);

        m_toast.show();
        activity.finish();
    }

    public static int calculateInSampleSize(int reqWidth, int reqHeight) {
        int inSampleSize = 1;

        //가로가 큰 경우
        if (reqWidth >= reqHeight) {
            while(MAX_IMAGE_SIZE < reqWidth/inSampleSize) {
                inSampleSize *= 2;
            }
        } else { //세로가 큰경우
            while(MAX_IMAGE_SIZE < reqHeight/inSampleSize) {
                inSampleSize *= 2;
            }
        }
        Log.d("LDK", "reqWidth:" + reqWidth + ", reqHeight:" + reqHeight + ", sampleSize:" + inSampleSize);

        return inSampleSize;
    }

    public static File getTempFile() {
        boolean isSdCardMounted = false;
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            isSdCardMounted = true;
        }

        if (isSdCardMounted) {
            File f = new File(Environment.getExternalStorageDirectory(), // 외장메모리 경로
                    Constant.TEMP_PHOTO_FILE);
            try {
                f.createNewFile();      // 외장메모리에 temp.jpg 파일 생성
            } catch (IOException e) {
            }

            return f;
        } else
            return null;
    }
}
