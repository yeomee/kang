package com.eastflag.kang.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.kang.Constant;
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
 * 회원 등록 화면
 */
public class Fragment111 extends DialogFragment {

    private View mView;
    private AQuery mAq;

//    @Bind(R.id.admin_name) EditText moim_name;
//    @Bind(R.id.admin_name) EditText adm_name;
//    @Bind(R.id.admin_email) EditText adm_email;
//    @Bind(R.id.submit)

    @Bind(R.id.mb_name) EditText mb_name;
    @Bind(R.id.mb_position) Spinner mb_position;
    @Bind(R.id.mb_pn) EditText mb_pn;
    @Bind(R.id.mb_add) EditText mb_add;
    @Bind(R.id.mb_enter_ymd) EditText mb_enter_ymd;
    @Bind(R.id.mb_actions) EditText mb_actions;
    @Bind(R.id.submit) Button submit;

    private String m_id;

    public Fragment111() {
        // Required empty public constructor
    }

    public static Fragment111 newInstance(String m_id) {
        Fragment111 frag = new Fragment111();
        Bundle args = new Bundle();
        args.putString("m_id", m_id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        m_id = getArguments().getString("m_id");

        mView = View.inflate(getActivity(), R.layout.fragment_111, null);
        mAq = new AQuery(mView);
        ButterKnife.bind(this, mView);

        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("회원 등록")
                .setView(mView)
                .create();
        return dialog;
    }

    @OnClick(R.id.submit)
    public void submit() {
        String url = Constant.HOST + Constant.API_111;

        if(TextUtils.isEmpty(mb_name.getText())) {
            Util.showToast(getActivity(), "회원 이름을 입력하세요");
            return;
        }

        if(TextUtils.isEmpty(mb_position.getSelectedItem().toString())) {
            Util.showToast(getActivity(), "회원 직책을 선택하세요");
            return;
        }

        if(TextUtils.isEmpty(mb_pn.getText())) {
            Util.showToast(getActivity(), "회원 전화번호를 입력하세요");
            return;
        }

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_id", m_id);
        params.put("mb_name", mb_name.getText().toString());
        params.put("mb_position", mb_position.getSelectedItem().toString());
        params.put("mb_pn", mb_pn.getText().toString());
        params.put("mb_add", mb_add.getText().toString());
        params.put("mb_enter_ymd", mb_enter_ymd.getText().toString());
        params.put("mb_actions", mb_actions.getText().toString());
        Log.d("LDK", params.toString());

        mAq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try {
                    if (status.getCode() != 200) {
                        Log.d("LDK", "status:" + status.getCode());
                        return;
                    }
                    Log.d("LDK", object.toString(1));
                    //데이터 존재하지 않음
                    if (object.getInt("result") == 0) {
                        Util.showToast(getActivity(), "등록되었습니다");
                        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new Fragment110()).commitAllowingStateLoss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
