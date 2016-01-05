package com.eastflag.kang;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.kang.fragment.Fragment010;
import com.eastflag.kang.fragment.MainFragment;
import com.eastflag.kang.fragment.Fragment001;
import com.eastflag.kang.utils.PreferenceUtil;
import com.eastflag.kang.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {

    private AQuery mAq;
    private Fragment mFragment;
    private FragmentManager mFm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAq = new AQuery(this);
        mFm = getFragmentManager();

        //메인 프래그먼트 노출
        mFragment = new MainFragment();
        mFm.beginTransaction().replace(R.id.container, mFragment).commitAllowingStateLoss();

        getIntro();

    }

    @Override
    public void onBackPressed() {
        if(mFm.getBackStackEntryCount() > 0) {
            mFm.popBackStack();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("강총무")
                    .setMessage("종료하시겠습니까?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {

                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void getIntro() {
        String url = Constant.HOST + Constant.API_INTRO;

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(this)); //전화번호
        params.put("paid", Util.getAndroidId(this)); //안드로이드 아이디
        params.put("pm", Util.getDeviceName()); //폰모델
        Log.d("LDK", params.toString());

        mAq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try {
                    if (status.getCode() != 200) {
                        Log.d("LDK", "status:" + status.getCode());
                        return;
                    }
                    Log.d("LDK", object.toString(1));
                    //데이터 존재하지 않음
                    if(object.getInt("result") == 0) {
                        String value = object.getString("value");
                        if("000".equals(value)) {
                            PreferenceUtil.getInstance(MainActivity.this).putToken(object.getString("token"));
                            //010 모임 리스트 화면 이동
                            mFragment = new Fragment010();
                            mFm.beginTransaction().replace(R.id.container, mFragment).commitAllowingStateLoss();
                        } else if ("001".equals(value)) {
                            //이용 비번 등록 화면
                        } else if ("002".equals(value)) {
                            //이용 비번 확인 화면
                            mFragment = new Fragment001();
                            mFm.beginTransaction().replace(R.id.container, mFragment).addToBackStack(null).commitAllowingStateLoss();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
