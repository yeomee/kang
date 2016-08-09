package com.yeomee.kang.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.yeomee.kang.Constant;
import com.yeomee.kang.KangApplication;
import com.yeomee.kang.MainActivity;
import com.yeomee.kang.R;
import com.yeomee.kang.dto.MoimVO;
import com.yeomee.kang.dto.FrboardVO;
import com.yeomee.kang.utils.PreferenceUtil;
import com.yeomee.kang.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 자유게시내용 수정
 * 20160802
 */
public class FrboardModiFragment extends Fragment {

    private View mView;
    private AQuery mAq;

    @Bind(R.id.menu1)
    Button mMenu1;
    @Bind(R.id.menu2)
    Button mMenu2;
    @Bind(R.id.menu3)
    Button mMenu3;
    @Bind(R.id.menu4)
    Button mMenu4;

    @Bind(R.id.sc_help1)
    TextView help1;
    @Bind(R.id.tv_name)
    TextView name;
    @Bind(R.id.et_cont)
    EditText cont;
    @Bind(R.id.modi_submit)
    View modi_submit;
    @Bind(R.id.delete_submit)
    View delete_submit;
    @Bind(R.id.tv_wrt_day)
    TextView board_ymd;//2016. 3. 31 현재
    @Bind(R.id.tv_modi_day)
    TextView modi_ymd;//2016. 3. 31 현재
    @Bind(R.id.pre_submit)
    View pre_submit;
    @Bind(R.id.next_submit)
    View next_submit;



    private String mTitle;

    private MoimVO mMoimVo;
    private FrboardVO mFrboardVo;
    private String btn_select;
    private String bof;
    private String eof;
    private String wrter_yn;

    private int screenMode;
    private Bitmap mBitmapPhoto;
    private Context mContext;

    public FrboardModiFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public FrboardModiFragment(MoimVO moimVo) {
        this.mMoimVo = moimVo;
    }

    @SuppressLint("ValidFragment")
    public FrboardModiFragment(MoimVO moimVo, FrboardVO frboardVo) {
        this.mMoimVo = moimVo;
        this.mFrboardVo = frboardVo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity) getActivity()).setTitle(mMoimVo.getMn());

        mView = inflater.inflate(R.layout.fragment_frboard_modi, container, false);
        mAq = new AQuery(mView);
        AQUtility.setDebug(true);
        ButterKnife.bind(this, mView);

        getScene();

        mMenu3.setSelected(true);

        mMenu1.setOnClickListener(mMenuClick);
        mMenu2.setOnClickListener(mMenuClick);
        mMenu3.setOnClickListener(mMenuClick);
        mMenu4.setOnClickListener(mMenuClick);

        return mView;
    }

    public void getScene() {
        String url;

        url = Constant.HOST + Constant.API_SC_FRBOARD_U;


        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_id", mMoimVo.getM_id());
        params.put("board_id", mFrboardVo.getBoard_id());
        params.put("btn_select", btn_select);
        params.put("adm_yn", mMoimVo.getAdm_yn());

        Log.d("LDK", params.toString());

        mAq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try {
                    if (status.getCode() != 200) {
                        Log.d("LDK", "status:" + status.getCode());
                        Util.showToast(getActivity(), "서버 오류가 발생하였습니다.");
                        return;
                    }
                    Log.d("LDK", object.toString(1));
                    //데이터 존재하지 않음
                    if (object.getInt("result") == 0) {

                        String scname_msg = "☞ " + object.getString("scname_msg");
                        help1.setText(scname_msg);

                        String s_ymd = object.getString("board_ymd");
                        board_ymd.setText(s_ymd);

                        String m_ymd = object.getString("modi_ymd");
                        if (m_ymd.isEmpty()) {
                            modi_ymd.setVisibility(View.GONE);
                        }else{
                            modi_ymd.setVisibility(View.VISIBLE);
                            modi_ymd.setText(m_ymd);
                        }

                        if (object.isNull("cont") || TextUtils.isEmpty(object.getString("cont"))) {
                            cont.setText("");
                        } else {
                            cont.setText(object.getString("cont"));
                        }

                        String board_id = object.getString("board_id");
                        mFrboardVo.setBoard_id(board_id);

                        if (object.isNull("name") || TextUtils.isEmpty(object.getString("name"))) {
                            name.setText("");
                        } else {
                            name.setText(object.getString("name"));
                        }
//                        String name = object.getString("name");
//                        name = name;

                        wrter_yn = object.getString("wrter_yn");
                        Log.d("LDK", "wrter_yn:" + wrter_yn);
                        //관리자 또는 글작성자가 아니면 수정,삭제 버튼 비활성화
                        if ("n".equals(wrter_yn))  {
                            if ("y".equals(mMoimVo.getAdm_yn())){
//                                modi_submit.setVisibility(View.GONE);
//                                delete_submit.setVisibility(View.GONE);
                            }else{
                                modi_submit.setVisibility(View.GONE);
                                delete_submit.setVisibility(View.GONE);
                            }

                        }



                        String bof_yn = object.getString("bof");
                        bof = bof_yn;

                        if ("y".equals(bof)) {
                            pre_submit.setVisibility(View.INVISIBLE);
                        } else {
                            pre_submit.setVisibility(View.VISIBLE);
                        }
                        ;

                        String eof_yn = object.getString("eof");
                        eof = eof_yn;

                        if ("y".equals(eof)) {
                            next_submit.setVisibility(View.INVISIBLE);
                        } else {
                            next_submit.setVisibility(View.VISIBLE);
                        }
                        ;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.pre_submit)
    void pre_bt_submit() {
        btn_select = "pre";
        getScene();

    }

    @OnClick(R.id.next_submit)
    void next_bt_submit() {
        btn_select = "next";
        getScene();

    }

    @OnClick(R.id.list_submit)
    void list_bt_submit() {
        mMenu2.setSelected(true);
        Fragment mFragment;
        mFragment = new FrboardListFragment(mMoimVo);
        getFragmentManager().beginTransaction().replace(R.id.container, mFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();

    }

    @OnClick(R.id.modi_submit)
    void modify() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("강총무")
                .setMessage("수정하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        modifyFrboard();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void modifyFrboard() {
        if (TextUtils.isEmpty(cont.getText())) {
            Util.showToast(getActivity(), "내용을 입력하세요");
            return;
        }

        String url;

        url = Constant.HOST + Constant.API_FRBOARD_U;


        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_id", mMoimVo.getM_id());
        params.put("board_id", mFrboardVo.getBoard_id());

        if (!TextUtils.isEmpty(cont.getText())) {
            params.put("cont", cont.getText().toString());
        }
//        if (!TextUtils.isEmpty(contitle.getText())) {
//            params.put("contitle", contitle.getText().toString());
//        }

//        params.put("mb_enter_ymd", mb_enter_ymd.getText().toString());
//        params.put("mb_actions", mb_actions.getText().toString());

        Log.d("LDK", params.toString());

        mAq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try {
                    if (status.getCode() != 200) {
                        Log.d("LDK", "status:" + status.getCode());
                        Util.showToast(getActivity(), "서버 오류가 발생하였습니다.");
                        return;
                    }
                    Log.d("LDK", object.toString(1));
                    Log.d("LMG123", object.toString());
                    //데이터 존재하지 않음
                    if (object.getInt("result") == 0) {

                        Util.showToast(getActivity(), "수정 되었습니다");

//                        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new FrboardListFragment(mMoimVo))
//                                .addToBackStack(null)
//                                .commitAllowingStateLoss();

//                        getScene();
                    } else {
                        Util.showToast(getActivity(), object.getString("Err_msg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //    @OnClick(R.id.delete_submit)
    @OnClick(R.id.delete_submit)
    void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("강총무")
                .setMessage("게시글을 삭제하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        deleteFrboard();
                    }
                    //)
                    //한번더 확인.끝.
                    //@Override
                    //public void onClick(DialogInterface dialog, int arg1) {
                    //    deleteMoim();
                    //}
                    //
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void deleteFrboard() {
        String url = Constant.HOST + Constant.API_FRBOARD_D;

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_id", mMoimVo.getM_id());
        params.put("board_id", mFrboardVo.getBoard_id());
//        params.put("mb_id", mMemberVo.getMb_id());

        Log.d("LDK", params.toString());

        mAq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try {
                    if (status.getCode() != 200) {
                        Log.d("LDK", "status:" + status.getCode());
                        Util.showToast(getActivity(), "서버 오류가 발생하였습니다.");
                        return;
                    }
                    Log.d("LDK", object.toString(1));
                    //데이터 존재하지 않음
                    if (object.getInt("result") == 0) {
                        Util.showToast(getActivity(), "게시글이 삭제 되었습니다");
                        getActivity().onBackPressed();
                    } else {
                        Util.showToast(getActivity(), object.getString("Err_msg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    View.OnClickListener mMenuClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            KangApplication.sApp.soundButton();
            mMenu1.setSelected(false);
            mMenu2.setSelected(false);
            mMenu3.setSelected(false);
            mMenu4.setSelected(false);

            Fragment mFragment;

            switch (v.getId()) {
                case R.id.menu1:
                    mMenu1.setSelected(true);
                    mFragment = new NewsListFragment(mMoimVo);
                    getFragmentManager().beginTransaction().replace(R.id.container, mFragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    break;
                case R.id.menu2:
                    if("n".equals(mMoimVo.getAdm_yn())) {
                        Util.showToast(getActivity(), "모임의 관리자만 등록이 가능합니다.");
                        mMenu1.setSelected(true);//현재 메뉴로 돌아감.
                        return;
                    }
                    mMenu2.setSelected(true);
//                    Util.showToast(getActivity(), "공지사항을 등록하는 화면입니다.");
                    mFragment = new NewsAddFragment(mMoimVo);
                    getFragmentManager().beginTransaction().replace(R.id.container, mFragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    break;
                case R.id.menu3:
                    mMenu3.setSelected(true);
                    mFragment = new FrboardListFragment(mMoimVo);
                    getFragmentManager().beginTransaction().replace(R.id.container, mFragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    break;
                case R.id.menu4:
                    mMenu4.setSelected(true);
                    Util.showToast(getActivity(), "자유롭게 글을 쓰는 화면입니다.");
                    mFragment = new FrboardAddFragment(mMoimVo);
                    getFragmentManager().beginTransaction().replace(R.id.container, mFragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                default:
                    break;
            }
        }
    };


}
