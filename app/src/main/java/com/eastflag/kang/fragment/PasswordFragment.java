package com.eastflag.kang.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.kang.Constant;
import com.eastflag.kang.KangApplication;
import com.eastflag.kang.MainActivity;
import com.eastflag.kang.R;
import com.eastflag.kang.utils.PreferenceUtil;
import com.eastflag.kang.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 비밀번호 입력화면
 */
public class PasswordFragment extends Fragment {

    private View mView;
    @Bind(R.id.input11) TextView input11;
    @Bind(R.id.input12) TextView input12;
    @Bind(R.id.input13) TextView input13;
    @Bind(R.id.input14) TextView input14;

    private AQuery mAq;

    private int mCount;
    private StringBuffer mInput = new StringBuffer();

    public PasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_password, container, false);
        mView.findViewById(R.id.reInput).setVisibility(View.GONE);

        ButterKnife.bind(this, mView);
        mAq = new AQuery(mView);

        return mView;
    }

    private void getPassword() {
        String url = Constant.HOST + Constant.API_020;

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("passwd", mInput); //폰모델
        Log.d("LDK", params.toString());

        mAq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try {
                    if (status.getCode() != 200) {
                        Log.d("LDK", "status:" + status.getCode());
                        Util.showNetworkError(getActivity());
                        return;
                    }
                    Log.d("LDK", object.toString(1));

                    if(object.getInt("result") == 0) {
                        //토큰값 저장
                        PreferenceUtil.getInstance(getActivity()).putToken(object.getString("token"));
                        //탭메뉴 display
                        ((MainActivity)getActivity()).showMenu(1, 0);
                        //모임리스트 화면 이동
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, new MoimListFragment())
                                .commitAllowingStateLoss();
                    } else {
                        Util.showToast(getActivity(), "비밀번호가 맞지않습니다");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn0, R.id.btnCL, R.id.btnBS})
    void onClick(View v) {
        KangApplication.sApp.soundButton();
        switch(v.getId()) {
            case R.id.btn1:
                mInput.append("1");
                break;
            case R.id.btn2:
                mInput.append("2");
                break;
            case R.id.btn3:
                mInput.append("3");
                break;
            case R.id.btn4:
                mInput.append("4");
                break;
            case R.id.btn5:
                mInput.append("5");
                break;
            case R.id.btn6:
                mInput.append("6");
                break;
            case R.id.btn7:
                mInput.append("7");
                break;
            case R.id.btn8:
                mInput.append("8");
                break;
            case R.id.btn9:
                mInput.append("9");
                break;
            case R.id.btn0:
                mInput.append("0");
                break;
            case R.id.btnCL:
                mInput = new StringBuffer();
                break;
            case R.id.btnBS:
                if(mInput.length() > 0) {
                    String str = mInput.substring(0, mInput.length() - 1);
                    mInput = new StringBuffer();
                    mInput.append(str);
                }
                break;
            default:
                break;
        }

        Log.d("LDK", mInput.length() + "," + mInput.toString());

        input11.setBackgroundColor(mInput.length() >= 1 ? Color.BLACK : Color.YELLOW);
        input12.setBackgroundColor(mInput.length() >= 2 ? Color.BLACK : Color.YELLOW);
        input13.setBackgroundColor(mInput.length() >= 3 ? Color.BLACK : Color.YELLOW);
        input14.setBackgroundColor(mInput.length() >= 4 ? Color.BLACK : Color.YELLOW);

        if(mInput.length() == 4) {
            getPassword();
        }
    }

}
