package com.eastflag.kang.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.kang.Constant;
import com.eastflag.kang.MainActivity;
import com.eastflag.kang.R;
import com.eastflag.kang.adapter.MoimListAdaptor;
import com.eastflag.kang.dto.MoimVO;
import com.eastflag.kang.listener.OnDismiss;
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
 * 홈화면: 모임리스트
 */
public class MoimListFragment extends Fragment {

    private View mView;
    private AQuery mAq;
    private ArrayList<MoimVO> mMoimList = new ArrayList<MoimVO>();

    @Bind(R.id.title) TextView title;
    @Bind(R.id.listView) ListView mListView;

    private MoimListAdaptor mAdaptor;

    public MoimListFragment() {
        // Required empty public constructor
    }

    private OnDismiss mListener = new OnDismiss() {
        @Override
        public void onDismiss() {
            getMain();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity)getActivity()).setTitle("나의 모임 리스트");

        mView = inflater.inflate(R.layout.fragment_moim_list, container, false);
        mAq = new AQuery(mView);
        ButterKnife.bind(this, mView);

        mAdaptor = new MoimListAdaptor(getActivity(), mMoimList, mListener);
        mListView.setAdapter(mAdaptor);

        ((MainActivity)getActivity()).hideSubMenu();
        ((MainActivity)getActivity()).showMenu(1, 0);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if("0".equals(mMoimList.get(position).getM_status())) {
                    Util.showToast(getActivity(), "요금납부후 이용하세요.");
                } else {
                    ((MainActivity) getActivity()).showSubMenu(1);
                    ((MainActivity) getActivity()).setSelectedMoim(mMoimList.get(position));
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new MemberListFragment(mMoimList.get(position)))
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            }
        });

        getMain();

        return mView;
    }

    private void getMain() {
        mMoimList.clear();

        String url = Constant.HOST + Constant.API_010;

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
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
                            MoimVO moim = new MoimVO();
                            JSONObject json = array.getJSONObject(i);
                            moim.setM_id(json.getString("m_id"));
                            moim.setMn(json.getString("mn"));
                            moim.setAdm_mb(json.getString("adm_mb"));
                            moim.setMy_position(json.getString("my_position"));
                            moim.setAdm_yn(json.getString("adm_yn"));
                            moim.setM_status(json.getString("m_status"));
                            mMoimList.add(moim);
                        }
                        mAdaptor.setData(mMoimList);
                        mAdaptor.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
