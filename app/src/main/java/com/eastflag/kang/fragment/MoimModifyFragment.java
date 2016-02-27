package com.eastflag.kang.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.eastflag.kang.listener.OnDismiss;
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
 * 모임 수정
 */
public class MoimModifyFragment extends DialogFragment {

    private View mView;
    private AQuery mAq;

    @Bind(R.id.moim_name) EditText moim_name;
    @Bind(R.id.admin_name) EditText adm_name;
    @Bind(R.id.admin_email) EditText adm_email;
    @Bind(R.id.submit) Button submit;
    @Bind(R.id.delete) Button delete;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.content) TextView content;

    private String m_id;
    private String mMoimName;
    private String mAdmName;
    private String mAdmEmail;

    private OnDismiss mListener;

    public MoimModifyFragment() {
        // Required empty public constructor
    }

    public void setListner(OnDismiss listener) {
        mListener = listener;
    }

    public static MoimModifyFragment newInstance(String m_id, String moim_name, String adm_name, String adm_email) {
        MoimModifyFragment frag = new MoimModifyFragment();
        Bundle args = new Bundle();
        args.putString("m_id", m_id);
        args.putString("moim_name", moim_name);
        args.putString("adm_name", adm_name);
        args.putString("adm_email", adm_email);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ((MainActivity)getActivity()).setTitle("나의 모임 리스트");

        m_id = getArguments().getString("m_id");
        mMoimName = getArguments().getString("moim_name");
        mAdmName = getArguments().getString("adm_name");
        mAdmEmail = getArguments().getString("adm_email");

        mView = View.inflate(getActivity(), R.layout.fragment_moim_add, null);
        mAq = new AQuery(mView);
        ButterKnife.bind(this, mView);

        submit.setText("모임 수정");
        delete.setVisibility(View.VISIBLE);
        moim_name.setText(mMoimName);
        adm_name.setText(mAdmName);
        adm_email.setText(mAdmEmail);

        getScene();

        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("회원 수정")
                .setView(mView)
                .create();
        return dialog;
    }

    private void getScene() {
        String url = Constant.HOST + Constant.API_201_SCENE;
        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_id", m_id);
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

    @OnClick(R.id.submit)
    public void submit() {
        String url = Constant.HOST + Constant.API_201;

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
        params.put("m_id", m_id);
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
                        Util.showToast(getActivity(), "수정되었습니다");
                        //키보드 내리기
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
                        mListener.onDismiss();
                        dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.delete) void delete() {
        String url = Constant.HOST + Constant.API_202;

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_id", m_id);
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
                        Util.showToast(getActivity(), "삭제되었습니다");
                        mListener.onDismiss();
                        dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
