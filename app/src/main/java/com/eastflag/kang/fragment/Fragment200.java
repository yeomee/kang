package com.eastflag.kang.fragment;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.kang.Constant;
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

/**
 * 모임 등록화면
 */
public class Fragment200 extends Fragment {

    private View mView;
    private AQuery mAq;

    @Bind(R.id.moim_name) EditText moim_name;
    @Bind(R.id.admin_name) EditText adm_name;
    @Bind(R.id.admin_email) EditText adm_email;
    @Bind(R.id.submit) Button submit;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.content) TextView content;

    public Fragment200() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity)getActivity()).setTitle("모임 등록");

        mView = inflater.inflate(R.layout.fragment_200, container, false);
        mAq = new AQuery(mView);
        ButterKnife.bind(this, mView);

        ((MainActivity)getActivity()).showMenu(2, 0);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        getScene();

        return mView;
    }

    private void getScene() {
        String url = Constant.HOST + Constant.API_200_SCENE;
        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
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
                       String scname_msg1 = object.getString("scname_msg1");
                        title.setText(scname_msg1);
                       String scname_msg2 = object.getString("scname_msg2");
                        content.setText(scname_msg2);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void submit() {
        String url = Constant.HOST + Constant.API_200;

        if(TextUtils.isEmpty(moim_name.getText())) {
            Util.showToast(getActivity(), "모임 제목을 입력하세요");
            return;
        }
        if(TextUtils.isEmpty(adm_name.getText())) {
            Util.showToast(getActivity(), "관리자 이름을 입력하세요");
            return;
        }
        if(TextUtils.isEmpty(adm_email.getText())) {
            Util.showToast(getActivity(), "관리자 이메일을 입력하세요");
            return;
        }

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_name", moim_name.getText().toString());
        params.put("adm_name", adm_name.getText().toString());
        params.put("adm_email", adm_email.getText().toString());
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
                        Util.showToast(getActivity(), "등록되었습니다");
                        //키보드 내리기
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
                        //퀵메뉴 홈아이콘 선택
                        ((MainActivity)getActivity()).showMenu(1, 0);
                        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new Fragment100())
                                .addToBackStack(null)
                                .commitAllowingStateLoss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
