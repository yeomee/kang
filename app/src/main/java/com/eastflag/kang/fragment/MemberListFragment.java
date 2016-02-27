package com.eastflag.kang.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.kang.Constant;
import com.eastflag.kang.KangApplication;
import com.eastflag.kang.MainActivity;
import com.eastflag.kang.R;
import com.eastflag.kang.adapter.Adaptor110;
import com.eastflag.kang.dto.MemberVO;
import com.eastflag.kang.dto.MoimVO;
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
public class MemberListFragment extends Fragment {

    private View mView;
    private AQuery mAq;
    private ArrayList<MemberVO> mMemberList = new ArrayList<MemberVO>();

    @Bind(R.id.menu1) Button mMenu1;
    @Bind(R.id.menu2) Button mMenu2;
    //@Bind(R.id.menu3) Button mMenu3;

    @Bind(R.id.title) TextView title;
    @Bind(R.id.listView) ListView mListView;

    private Adaptor110 mAdaptor;
    private MoimVO mMoimVO;

    public MemberListFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public MemberListFragment(MoimVO moimVO) {
        mMoimVO = moimVO;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_member_list, container, false);
        mAq = new AQuery(mView);
        ButterKnife.bind(this, mView);

        mMemberList.clear();
        mAdaptor = new Adaptor110(getActivity(), mMemberList);
        mListView.setAdapter(mAdaptor);

        ((MainActivity)getActivity()).showSubMenu();

        mMenu1.setSelected(true);
        //mMenu1.setOnClickListener(mMenuClick);
        mMenu2.setOnClickListener(mMenuClick);
        //mMenu3.setOnClickListener(mMenuClick);

        //reg_member.setOnClickListener(mClick);

        ((MainActivity)getActivity()).showMenu(1, 1);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                MemberAddModifyFragment dialog = MemberAddModifyFragment.newInstance(mMoimVO.getM_id(), mMemberList.get(position).getMb_name(),
//                        mMemberList.get(position).getMy_position(), mMemberList.get(position).getMb_pn(),
//                        mMemberList.get(position).getMb_add(), mMemberList.get(position).getMb_action());
//                dialog.show(getFragmentManager(), "회원수정");
                getFragmentManager().beginTransaction().replace(R.id.container, new MemberAddModifyFragment(mMoimVO, mMemberList.get(position)))
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });

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
        params.put("m_id", mMoimVO.getM_id());
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
                        ((MainActivity)getActivity()).setTitle(object.getString("m_name"));

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

//    View.OnClickListener mClick = new View.OnClickListener(){
//        @Override
//        public void onClick(View v) {
//            switch(v.getId()) {
//                case R.id.reg_member :
//                    //DialogFragment dialog = MemberAddModifyFragment.newInstance(mMoimVO.getM_id(), null, null, null, null, null);
//                    //dialog.show(getFragmentManager(), "회원등록");
//                    getFragmentManager().beginTransaction().replace(R.id.container, new MemberAddModifyFragment(mMoimVO)).commitAllowingStateLoss();
//                    break;
//            }
//        }
//    };

    View.OnClickListener mMenuClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if("n".equals(mMoimVO.getAdm_yn())) {
                Util.showToast(getActivity(), "모임의 관리자만 등록이 가능합니다.");
                return;
            }

            KangApplication.sApp.soundButton();
            mMenu1.setSelected(false);
            mMenu2.setSelected(false);
            //mMenu3.setSelected(false);

            Fragment mFragment;

            switch(v.getId()) {
//                case R.id.menu1:
//                    mMenu1.setSelected(true);
//                    mFragment = new MoimListFragment();
//                    mFm.beginTransaction().replace(R.id.container, mFragment).commitAllowingStateLoss();
//                    break;
                case R.id.menu2:
                    mMenu2.setSelected(true);
                    mFragment = new MemberAddModifyFragment(mMoimVO);
                    getFragmentManager().beginTransaction().replace(R.id.container, mFragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    break;
//                case R.id.menu3:
//                    mMenu3.setSelected(true);
//                    break;
                default:
                    break;
            }
        }
    };
}
