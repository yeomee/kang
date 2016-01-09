package com.eastflag.kang.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.kang.Constant;
import com.eastflag.kang.R;
import com.eastflag.kang.adapter.Adaptor110;
import com.eastflag.kang.dto.MemberVO;
import com.eastflag.kang.utils.PreferenceUtil;
import com.eastflag.kang.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 회원 : 회원 리스트 조회화면
 */
public class Fragment110 extends Fragment {

    private View mView;
    private AQuery mAq;
    private ArrayList<MemberVO> mMemberList = new ArrayList<MemberVO>();

    @Bind(R.id.title) TextView title;
    @Bind(R.id.listView) ListView mListView;
    @Bind(R.id.reg_moim) View reg_moim;

    private Adaptor110 mAdaptor;
    private String m_id;

    public Fragment110() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Fragment110(String m_id) {
        this.m_id = m_id;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_110, container, false);
        mAq = new AQuery(mView);
        ButterKnife.bind(this, mView);

        mAdaptor = new Adaptor110(getActivity(), mMemberList);
        mListView.setAdapter(mAdaptor);

        reg_moim.setOnClickListener(mClick);

        getMain();

        return mView;
    }

    private void getMain() {
        String url = Constant.HOST + Constant.API_110;

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델*/
        params.put("m_id", m_id);
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
                        String scname_msg = object.getString("scname_msg");
                        title.setText(scname_msg);
                        JSONArray array = object.getJSONArray("value");
                        for (int i = 0; i < array.length(); ++i) {
                            MemberVO member = new MemberVO();
                            JSONObject json = array.getJSONObject(i);

                            member.setMb_pn(json.getString("mb_pn"));
                            member.setMy_position(json.getString("my_position"));
                            member.setMb_id(json.getString("mb_id"));
                            member.setMb_name(json.getString("mb_name"));
                            member.setM_id(json.getString("m_id"));
                            member.setAdmin_yn(json.getString("adm_yn"));

                            mMemberList.add(member);
                        }

                        mAdaptor.setData(mMemberList);
                        mAdaptor.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    View.OnClickListener mClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.reg_moim:
                    getFragmentManager().beginTransaction().replace(R.id.container, new Fragment111()).commitAllowingStateLoss();
                    break;
            }
        }
    };
}
