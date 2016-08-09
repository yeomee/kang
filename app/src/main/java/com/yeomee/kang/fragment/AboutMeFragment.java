package com.yeomee.kang.fragment;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.yeomee.kang.Constant;
import com.yeomee.kang.MainActivity;
import com.yeomee.kang.R;
import com.yeomee.kang.dto.MemberNameVO;
import com.yeomee.kang.dto.MemberVO;
import com.yeomee.kang.dto.MoimVO;
import com.yeomee.kang.utils.PreferenceUtil;
import com.yeomee.kang.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 우리모임에서 나
 */
public class AboutMeFragment extends Fragment {

    private View mView;
    private AQuery mAq;
    private MoimVO mMoimVO;

    private ArrayList<MemberNameVO> mMemberNameList = new ArrayList<MemberNameVO>();
    private ArrayAdapter<MemberNameVO> mAdaptor;
//    private ArrayList<MemberVO> mMemberList = new ArrayList<MemberVO>();
//    private ArrayAdapter<MemberVO> mAdaptor;

    //    @Bind(R.id.moim_name) EditText moim_name;
//    @Bind(R.id.admin_name) EditText adm_name;
//    @Bind(R.id.admin_email) EditText adm_email;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.help_content)
    TextView help_content;
    @Bind(R.id.moim_my_name)
    TextView moim_myname;
    @Bind(R.id.submit)
    Button submit;
    @Bind(R.id.moim_reg_ymd)
    TextView moim_reg_ymd;
    @Bind(R.id.mo_position)
    TextView mo_position;
    @Bind(R.id.adm_yn_msg)
    TextView adm_yn_msg;
    @Bind(R.id.adm_giver)
    TextView adm_giver;
    @Bind(R.id.adm_taker)
    Spinner adm_taker;
    @Bind(R.id.adm_change) View adm_change;
    @Bind(R.id.adm_changing) View adm_changing;


    public AboutMeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public AboutMeFragment(MoimVO moimVO) {
        mMoimVO = moimVO;
    }

    public static AboutMeFragment newInstance(String m_id, String moim_name, String adm_name, String adm_email) {
        AboutMeFragment frag = new AboutMeFragment();
        Bundle args = new Bundle();
        args.putString("m_id", m_id);
        args.putString("moim_name", moim_name);
        args.putString("adm_name", adm_name);
        args.putString("adm_email", adm_email);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        ((MainActivity)getActivity()).setTitle("환경설정");

        mView = inflater.inflate(R.layout.fragment_aboutme, container, false);
        mAq = new AQuery(mView);
        ButterKnife.bind(this, mView);

        ((MainActivity) getActivity()).showMenu(1, 7);   //하단메뉴 포커스
        getScene();

        mAdaptor = new ArrayAdapter<MemberNameVO>(getActivity(), R.layout.spinner, mMemberNameList);
        adm_taker.setAdapter(mAdaptor);



        return mView;
    }

    private void getScene() {
        String url = Constant.HOST + Constant.API_SC_ABOUTME;
        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_id", mMoimVO.getM_id());
        Log.d("LDK", params.toString());

        mAq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try {
                    if (status.getCode() != 200) {
                        Log.d("LDK", "status:" + status.getCode());
                        Util.showNetworkError(getActivity());
                        return;
                    }
                    Log.d("LDK", object.toString(1));
                    //데이터 존재하지 않음
                    MemberNameVO member = new MemberNameVO();
                    member.setMb_id("-1");
                    member.setMb_name("회원을 선택하세요.");
                    mMemberNameList.add(member);
                    if (object.getInt("result") == 0) {
                        String scname_msg1 = "☞ " + object.getString("scname_msg");
                        title.setText(scname_msg1);
                        String scname_msg2 = "☞ " + object.getString("scname_msg2");
                        help_content.setText(scname_msg2);
                        moim_myname.setText( object.getString("moim_myname"));
                        mo_position.setText( object.getString("my_position"));
                        moim_reg_ymd.setText( object.getString("moim_reg_ymd"));
                        adm_yn_msg.setText(object.getString("adm_yn_msg"));
//                        Log.d("LMG", "adm_yn_msg:" + adm_yn_msg);
                        if ("(관리자)".equals(object.getString("adm_yn_msg"))) {
//                            adm_change.setVisibility(View.GONE);
                            adm_giver.setText( object.getString("adm_mb_name"));

                            Map<String, Object> params = new HashMap<String, Object>();
                            JSONArray array = object.getJSONArray("mb_list");
                            for (int i = 0; i < array.length(); ++i) {
                                JSONObject json = array.getJSONObject(i);
                                member = new MemberNameVO();

                                member.setMb_id(json.getString("mb_id"));
                                member.setMb_name(json.getString("mb_name"));
//                            member.setMb_name(json.getString("po_name"));

                                mMemberNameList.add(member);
                            }
                            adm_taker.setSelection(0);
                            mAdaptor.notifyDataSetChanged();
                        }
                        if ("(관리자 아님)".equals(object.getString("adm_yn_msg"))) {
                            adm_change.setVisibility(View.GONE);
                            adm_changing.setVisibility(View.VISIBLE);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void submit() {
        String url = Constant.HOST + Constant.API_APPSETTING_U;

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
//        params.put("adm_name", adm_name.getText().toString());
//        params.put("adm_email", adm_email.getText().toString());
        Log.d("LDK", params.toString());

        mAq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try {
                    if (status.getCode() != 200) {
                        Log.d("LDK", "status:" + status.getCode());
                        Util.showNetworkError(getActivity());
                        return;
                    }
                    Log.d("LDK", object.toString(1));
                    //데이터 존재하지 않음
                    if (object.getInt("result") == 0) {
//                        if (Pwd=="y") {
//                            //Util.showToast(getActivity(), "비번이용이 설정되었습니다");
//                            //키보드 내리기
//                            //InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            //imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
//                            //퀵메뉴 홈아이콘 선택
//                            ((MainActivity)getActivity()).showMenu(1, 0);
//                            getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new PasswordRegFragment())
//                                    .addToBackStack(null)
//                                    .commitAllowingStateLoss();
//                        }
//                        if (Pwd=="n") {
//                            Util.showToast(getActivity(), "비번이용이 해제되었습니다");
//
//
//                            //키보드 내리기
//                            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
//                            //퀵메뉴 홈아이콘 선택
//                            ((MainActivity)getActivity()).showMenu(1, 0);
//                            getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new AboutMeFragment())
//                                    .addToBackStack(null)
//                                    .commitAllowingStateLoss();
//
//                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
