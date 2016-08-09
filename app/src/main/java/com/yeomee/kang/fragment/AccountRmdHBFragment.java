package com.yeomee.kang.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.yeomee.kang.Constant;
import com.yeomee.kang.KangApplication;
import com.yeomee.kang.MainActivity;
import com.yeomee.kang.R;
import com.yeomee.kang.dto.AccountTotalymVO;
import com.yeomee.kang.dto.AccountHbYmdVO;
import com.yeomee.kang.dto.IncomeSubjectVO;
import com.yeomee.kang.dto.MemberNameVO;
import com.yeomee.kang.dto.MoimVO;
import com.yeomee.kang.utils.PreferenceUtil;
import com.yeomee.kang.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 회계 한 장의 회비발생된 전표 열람화면.
 * 화면의 모양은 수납전표를 처리하기 위한 입력화면.
 * 버튼은 회비수납처리와 회비발생전표 삭제.
 * 관리자가 아니면 수정|삭제 버튼 안보임.2016.7.28.
 */
public class AccountRmdHBFragment extends Fragment {

    private View mView;
    private AQuery mAq;
    private String sMethod;
    private String r_ymd_old;//회비발생일(서버로부터 받은 금액)
    private String r_amount_old;//회비발생액(서버로부터 받은 금액)
    private String r_able_amount;//수납가능금액


    @Bind(R.id.menu1)
    Button mMenu1;//잔고
    @Bind(R.id.menu2)
    Button mMenu2;//수입
    //    @Bind(R.id.menu3) Button mMenu3;//비용
    @Bind(R.id.menu4)
    Button mMenu4;//전표입력
    @Bind(R.id.menu5)
    Button mMenu5;//보고서

    @Bind(R.id.sc_help1)
    TextView sc_help1;//상단 도움말
    //    @Bind(R.id.i_ymd) TextView i_ymd;
//    @Bind(R.id.inco_cd) Spinner inco_cd;
    @Bind(R.id.mb_name)
    TextView mb_name;
    @Bind(R.id.r_jukyo)
    TextView r_jukyo;

    @Bind(R.id.i_ymd)
    TextView i_ymd;
    @Bind(R.id.i_amount)
    TextView i_amount;
    @Bind(R.id.enter_ymd)
    TextView enter_ymd;
    @Bind(R.id.r_ymd)
    TextView r_ymd;
    @Bind(R.id.soonap_amount)
    TextView soonap_amount;
    @Bind(R.id.misoonap)
    TextView misoonap;
    @Bind(R.id.cash)
    RadioButton cash;
    @Bind(R.id.deposit)
    RadioButton deposit;
    @Bind(R.id.r_amount)
    EditText r_amount;
    @Bind(R.id.r_memo)
    EditText r_memo;
    @Bind(R.id.layout_submit)
    EditText layout_submit;


//    private String mbPosition;

    private ArrayList<IncomeSubjectVO> mIncomeSubjectVOList = new ArrayList<IncomeSubjectVO>();
    private ArrayAdapter<IncomeSubjectVO> mAdaptor;

    private ArrayList<MemberNameVO> mMemberNameList = new ArrayList<MemberNameVO>();
    private ArrayAdapter<MemberNameVO> mAdaptor2;

    private MoimVO mMoimVo;
    private AccountHbYmdVO mAccountHbYmdVO;

    private String mR_ymd;
    private String mJunpyo_id;
    private String mhb_ym;


    private Context mContext;

    public AccountRmdHBFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public AccountRmdHBFragment(MoimVO moimVo, AccountHbYmdVO accountYmdVO) {
        this.mMoimVo = moimVo;
        this.mAccountHbYmdVO = accountYmdVO;
    }

//    @SuppressLint("ValidFragment")
//    public AccountRmdHBFragment(MoimVO moimVo, AccountHbYmdVO accountHbYmdVO, String r_ymd, String junpyo_id) {
//        this.mMoimVo = moimVo;
//        this.mAccountHbYmdVO = accountHbYmdVO;
//        this.mR_ymd = r_ymd;
//        this.mJunpyo_id = junpyo_id;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity) getActivity()).setTitle(mMoimVo.getMn());

        mView = inflater.inflate(R.layout.fragment_account_read_hb, container, false);
        mAq = new AQuery(mView);
        AQUtility.setDebug(true);
        ((MainActivity) getActivity()).showMenu(1, 3);
        ((MainActivity) getActivity()).showSubMenu();


        getScene();
        ButterKnife.bind(this, mView);
        if ("n".equals(mMoimVo.getAdm_yn())) {
            layout_submit.setVisibility(View.GONE);
        }

        cash.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sMethod = "cash";
                //Util.showToast(getActivity(), "이용비번 설정을 선택했습니다.");
                return;
            }
        });
        deposit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sMethod = "deposit";
                //Util.showToast(getActivity(), "이용비번 설정을 선택했습니다.");
                return;
            }
        });


        //getIncomeList();


        mMenu4.setSelected(true);//전표입력


        mMenu1.setOnClickListener(mMenuClick);
        mMenu2.setOnClickListener(mMenuClick);
//        mMenu3.setOnClickListener(mMenuClick);
        mMenu4.setOnClickListener(mMenuClick);
        mMenu5.setOnClickListener(mMenuClick);

        return mView;
    }

    public void getScene() {
        String url;
        url = Constant.HOST + Constant.API_SC_ACC_IC_HB;

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_id", mMoimVo.getM_id());
//        params.put("r_ymd", mAccountHbYmdVO.getYmd());
        if (!TextUtils.isEmpty(mR_ymd)) {
            params.put("r_ymd", mR_ymd);
        } else {
            params.put("r_ymd", mAccountHbYmdVO.getYmd());
        }
//        params.put("junpyo_id", mAccountHbYmdVO.getJunpyo_id());
        if (!TextUtils.isEmpty(mJunpyo_id)) {
            params.put("junpyo_id", mJunpyo_id);
        } else {
            params.put("junpyo_id", mAccountHbYmdVO.getJunpyo_id());
        }

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
                        sc_help1.setText(scname_msg);

                        if (object.isNull("ymd") || TextUtils.isEmpty(object.getString("ymd"))) {
                            i_ymd.setText("");
                        } else {
                            i_ymd.setText(object.getString("ymd"));
                        }
                        if (object.isNull("iamount") || TextUtils.isEmpty(object.getString("iamount"))) {
                            i_amount.setText("");
                        } else {
                            i_amount.setText(object.getString("iamount")+"원");
                        }
                        if (object.isNull("enter_ymd") || TextUtils.isEmpty(object.getString("enter_ymd"))) {
                            enter_ymd.setText("");
                        } else {
                            enter_ymd.setText(object.getString("enter_ymd"));
                        }

                        r_ymd_old = object.getString("ymd");
                        mb_name.setText(object.getString("mb_name"));
                        soonap_amount.setText(object.getString("tot_soonap")+"원");


                        if (object.isNull("jukyo") || TextUtils.isEmpty(object.getString("jukyo"))) {
                            r_jukyo.setText("");
                        } else {
                            r_jukyo.setText(object.getString("jukyo"));
                        }
                        if (object.isNull("r_able_amount") || TextUtils.isEmpty(object.getString("r_able_amount"))) {
                            misoonap.setText("");
                        } else {
                            misoonap.setText(object.getString("r_able_amount")+"원");
                        }

                        r_amount.setText(object.getString("r_able_amount"));//회비발생액(새로 입력받으면 변함),수납가능금액.
                        r_amount_old = (object.getString("iamount"));//회비발생액
                        r_able_amount= (object.getString("r_able_amount"));//수납가능금액

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private DatePickerDialog.OnDateSetListener dateSetListener2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            r_ymd.setText(String.format("%d%02d%02d", year, monthOfYear + 1, dayOfMonth));
        }
    };

    @OnClick(R.id.r_ymd)
    void receipt_ymd() {
        final GregorianCalendar calendar = new GregorianCalendar();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener2,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    @OnClick(R.id.modi_submit)
    public void submit() {
        if (TextUtils.isEmpty(r_ymd.getText())) {
            Util.showToast(getActivity(), "수납일을 입력하세요");
            return;
        }

        if (TextUtils.isEmpty(sMethod)) {
            Util.showToast(getActivity(), "수납수단을 선택하세요");
            return;
        }
        if (TextUtils.isEmpty(r_amount.getText())) {
            Util.showToast(getActivity(), "수납금액를 입력하세요");
            return;
        }

//        Log.d("LMG", "new:" + r_amount.getText().toString());
//        Log.d("LMG", "old:" + r_able_amount.toString());
        int new_ramount = Integer.parseInt(r_amount.getText().toString().replace(",",""));
        int old_ramount = Integer.parseInt(r_able_amount.toString().replace(",",""));

        if (new_ramount > old_ramount) {
            Util.showToast(getActivity(), "수납금액이 "+Integer.toString(old_ramount) + "원 보다 클 수 없습니다.");
            return;
        }


        String url;
        url = Constant.HOST + Constant.API_ACC_HB_I;


        Log.d("LDK", "url:" + url);
        // 처리중 화면 호출.
        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new WaitingFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_id", mMoimVo.getM_id());
        params.put("mb_name", mb_name.getText().toString());

        params.put("r_ymd_new", r_ymd.getText().toString());
        params.put("r_ymd_old", r_ymd_old.toString());
//        params.put("inco_cd", ((IncomeSubjectVO)inco_cd.getSelectedItem()).getInco_cd());

        if (!TextUtils.isEmpty(r_jukyo.getText())) {
            params.put("r_jukyo", r_jukyo.getText().toString());
        }

        params.put("smethod", sMethod.toString());
        params.put("r_amount", r_amount.getText().toString());
        if (!TextUtils.isEmpty(r_memo.getText().toString())) {
            params.put("r_memo", r_memo.getText().toString());
        }
        params.put("junpyo_id", mAccountHbYmdVO.getJunpyo_id());


        Log.d("LDK:params", params.toString());

        mAq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try {
                    if (status.getCode() != 200) {
                        Log.d("LDK", "status:" + status.getCode());
                        Util.showToast(getActivity(), "서버 오류가 발생하였습니다.");
                        return;
                    }
                    Log.d("LDK:object", object.toString(1));
                    //데이터 존재하지 않음
                    if (object.getInt("result") == 0) {
                        Util.showToast(getActivity(), "수납처리 되었습니다");
                        mhb_ym = object.getString("hb_ym");

                        getActivity().getFragmentManager().beginTransaction()
                                .replace(R.id.container, new AccountReadHBYmdFragment(mMoimVo, mAccountHbYmdVO, mhb_ym))
                                .addToBackStack(null)
                                .commitAllowingStateLoss();
                    } else {
                        Util.showToast(getActivity(), object.getString("Err_msg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.del_submit)
    void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("강총무")
                .setMessage("발생된 회비를 삭제하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    //한번더 확인.시작
                    //AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                    //builder2.setTitle("강총무")
                    //.setMessage("한번더 묻습니다. 정말 삭제하시겠습니까?")
                    //.setPositiveButton("예",new DialogInterface.OnClickListener() {
                    //    @Override
                    //    public void onClick(DialogInterface dialog, int arg1) {
                    //        deleteMoim();
                    //    }
                    //}
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        deleteReceipt();
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

    public void deleteReceipt() {

//        Log.d("LMG", "sMethod:" + sMethod);
        String url;
        url = Constant.HOST + Constant.API_ACC_HB_D_MI;


        Log.d("LDK", "url:" + url);
        // 처리중 화면 호출.
        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new WaitingFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_id", mMoimVo.getM_id());
        params.put("i_ymd", i_ymd.getText().toString());
        params.put("junpyo_id", mAccountHbYmdVO.getJunpyo_id());

        Log.d("LDK:params", params.toString());

        mAq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try {
                    if (status.getCode() != 200) {
                        Log.d("LDK", "status:" + status.getCode());
                        Util.showToast(getActivity(), "서버 오류가 발생하였습니다.");
                        return;
                    }
                    Log.d("LDK:object", object.toString(1));
                    //데이터 존재하지 않음
                    if (object.getInt("result") == 0) {
                        Util.showToast(getActivity(), "삭제 되었습니다");
                        getActivity().onBackPressed();
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

    public void showMenu(int selected) {
        mMenu1.setSelected(false);
        mMenu2.setSelected(false);
        //mMenu3.setSelected(false);
        switch (selected) {
            case 1:
                mMenu1.setSelected(true);
                break;
            case 2:
                mMenu2.setSelected(true);
                break;
        }
    }

    View.OnClickListener mMenuClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            KangApplication.sApp.soundButton();
            mMenu1.setSelected(false);
            mMenu2.setSelected(false);
            //mMenu3.setSelected(false);

            Fragment mFragment;

            switch (v.getId()) {
                case R.id.menu1:
                    mMenu1.setSelected(true);
                    mFragment = new AccountJangoFragment(mMoimVo);
                    getFragmentManager().beginTransaction().replace(R.id.container, mFragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    break;
                case R.id.menu2:
                    mMenu2.setSelected(true);
                    mFragment = new AccountReadTotalYmFragment(mMoimVo);
                    getFragmentManager().beginTransaction().replace(R.id.container, mFragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    break;
                case R.id.menu4:
                    mMenu4.setSelected(true);
                    mFragment = new AccountInputSelFragment(mMoimVo);
                    getFragmentManager().beginTransaction().replace(R.id.container, mFragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    break;

                default:
                    break;
            }
        }
    };


}
