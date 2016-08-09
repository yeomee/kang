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
import com.yeomee.kang.dto.AccountYmdVO;
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
 * 회계 한 장의 수납전표 열람(수정/삭제) 화면
 * 본 화면을 호출하는 화면은 2가지로 분류할 수 있다.
 * 1. 전표열람(년월 전표집계) 화면-일자별 전표내역-본화면을 호출되는 경우
 *    해당년월일 및 전표번호를 DTO에서 가져온다.
 * 2. 본 화면에서 수정버튼을 누른 후 본 화면을 호출하는 경우
 *    해단년월일 및 전표번호를 수정 후 서버로 부터 받는다.
 *    왜냐면 수정전 일자와 수정후 일자가 다르므로 접근하는 테이블도 다를 수 있고
 *    수정후의 전표번호도 다르기 때문에 DTO 자료를 사용할 수 없다.
 *    아니면 DTO 자료를 수정해야 되는데 현재로서는 수준 미달.
 *    2015.5.3
 *    관리자가 아니면 수정|삭제 버튼 안보임.2016.7.28.
 */
public class AccountRmdReceiptFragment extends Fragment {

    private View mView;
    private AQuery mAq;
    private String sMethod;
    private String r_ymd_old;


    @Bind(R.id.menu1) Button mMenu1;//잔고
    @Bind(R.id.menu2) Button mMenu2;//수입
//    @Bind(R.id.menu3) Button mMenu3;//비용
    @Bind(R.id.menu4) Button mMenu4;//전표등록
    @Bind(R.id.menu5) Button mMenu5;//보고서

    @Bind(R.id.sc_help1) TextView sc_help1;//상단 도움말
//    @Bind(R.id.i_ymd) TextView i_ymd;
    @Bind(R.id.inco_cd) Spinner inco_cd;
    @Bind(R.id.mb_name) Spinner mb_name;
    @Bind(R.id.r_jukyo) EditText r_jukyo;

    @Bind(R.id.r_ymd) TextView r_ymd;
    @Bind(R.id.cash)    RadioButton cash;
    @Bind(R.id.deposit) RadioButton deposit;
    @Bind(R.id.r_amount) EditText r_amount;
    @Bind(R.id.r_memo) EditText r_memo;
    @Bind(R.id.layout_submit) View layout_submit;

    private String mbPosition;

    private ArrayList<IncomeSubjectVO> mIncomeSubjectVOList = new ArrayList<IncomeSubjectVO>();
    private ArrayAdapter<IncomeSubjectVO> mAdaptor;

    private ArrayList<MemberNameVO> mMemberNameList = new ArrayList<MemberNameVO>();
    private ArrayAdapter<MemberNameVO> mAdaptor2;

    private MoimVO mMoimVo;
    private AccountYmdVO mAccountYmdVO;
    private AccountTotalymVO mAccountTotalymVO;
    private  String mR_ymd;
    private  String mJunpyo_id;


    private Context mContext;
    public AccountRmdReceiptFragment() {
        // Required empty public constructor
    }


//    @SuppressLint("ValidFragment")  //모임관련 정보 얻기 위해.
//    public AccountRmdReceiptFragment(MoimVO moimVo) {
//        this.mMoimVo = moimVo;
//    }

    @SuppressLint("ValidFragment")    //수납전표번호 얻기 위해
    public AccountRmdReceiptFragment(MoimVO moimVo, AccountYmdVO accountYmdVO) {
        this.mMoimVo = moimVo;
        this.mAccountYmdVO = accountYmdVO;
    }
    @SuppressLint("ValidFragment")
    public AccountRmdReceiptFragment(MoimVO moimVo, AccountTotalymVO accountTotalymVO) {
        this.mMoimVo = moimVo;
        this.mAccountTotalymVO = accountTotalymVO;
    }
    @SuppressLint("ValidFragment")
    public AccountRmdReceiptFragment(MoimVO moimVo,AccountYmdVO accountYmdVO, String r_ymd, String junpyo_id) {
        this.mMoimVo = moimVo;
        this.mAccountYmdVO = accountYmdVO;
        this.mR_ymd = r_ymd;
        this.mJunpyo_id=junpyo_id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity)getActivity()).setTitle(mMoimVo.getMn());

        mView = inflater.inflate(R.layout.fragment_account_read_inco, container, false);
        mAq = new AQuery(mView);
        AQUtility.setDebug(true);
        ((MainActivity)getActivity()).showMenu(1, 3);
        ((MainActivity)getActivity()).showSubMenu();
        ButterKnife.bind(this, mView);


        getScene();
        //관리자가 아니면 수정,삭제 버튼 비활성화
        Log.d("LMG", "mMoimVo.getAdm_yn():" + mMoimVo.getAdm_yn());
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


        mAdaptor = new ArrayAdapter<IncomeSubjectVO>(getActivity(), R.layout.spinner, mIncomeSubjectVOList);
        inco_cd.setAdapter(mAdaptor);

        mAdaptor2 = new ArrayAdapter<MemberNameVO>(getActivity(), R.layout.spinner, mMemberNameList);
        mb_name.setAdapter(mAdaptor2);

        //getIncomeList();





        mMenu2.setSelected(true);//전표열람


        mMenu1.setOnClickListener(mMenuClick);
        mMenu2.setOnClickListener(mMenuClick);
//        mMenu3.setOnClickListener(mMenuClick);
        mMenu4.setOnClickListener(mMenuClick);
        mMenu5.setOnClickListener(mMenuClick);

        return mView;
    }

    public void getScene() {
        String url;
        url = Constant.HOST + Constant.API_SC_ACC_IC_R;

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_id", mMoimVo.getM_id());
        params.put("adm_yn", mMoimVo.getAdm_yn());
//        params.put("r_ymd", mAccountYmdVO.getYmd());
        if (!TextUtils.isEmpty(mR_ymd)) {
            params.put("r_ymd", mR_ymd);
        }else{
            params.put("r_ymd", mAccountYmdVO.getYmd());
        }
//        params.put("junpyo_id", mAccountYmdVO.getJunpyo_id());
        if (!TextUtils.isEmpty(mJunpyo_id)) {
            params.put("junpyo_id", mJunpyo_id);
        }else{
            params.put("junpyo_id", mAccountYmdVO.getJunpyo_id());
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


                    IncomeSubjectVO income_cd = new IncomeSubjectVO();
                    income_cd.setInco_cd("-1");
                    income_cd.setInco_name("선택");
                    mIncomeSubjectVOList.add(income_cd);

                    MemberNameVO member_name = new MemberNameVO();
                    member_name.setMb_tel("-1");
                    member_name.setMb_name("선택");
                    mMemberNameList.add(member_name);



                    if (object.getInt("result") == 0) {
                        String scname_msg = "☞ " + object.getString("scname_msg");
                        sc_help1.setText(scname_msg);

                        if (object.isNull("ymd") || TextUtils.isEmpty(object.getString("ymd"))) {
                            r_ymd.setText("");
                        } else {
                            r_ymd.setText(object.getString("ymd"));
                        }

                        r_ymd_old=object.getString("ymd");

                        JSONArray array = object.getJSONArray("income_cd");
                        for (int i = 0; i < array.length(); ++i) {

                            JSONObject json = array.getJSONObject(i);
                            income_cd = new IncomeSubjectVO();
                            income_cd.setInco_cd(json.getString("inco_cd"));
                            income_cd.setInco_name(json.getString("inco_name"));

                            mIncomeSubjectVOList.add(income_cd);
                        }

                        String incoSubject = object.getString("subject");
                        for (int i = 0; i < mAdaptor.getCount(); i++) {
                            IncomeSubjectVO incomeSubjectVO = mAdaptor.getItem(i);
                            if (incomeSubjectVO.getInco_name().equals(incoSubject)) {
                                inco_cd.setSelection(i);
                                break;
                            }
                        }

                        JSONArray array1 = object.getJSONArray("member_name");
                        for (int i = 0; i < array1.length(); ++i) {
                            JSONObject json = array1.getJSONObject(i);
                            member_name = new MemberNameVO();

                            member_name.setMb_tel(json.getString("mb_pn"));
                            member_name.setMb_name(json.getString("mb_name"));

                            mMemberNameList.add(member_name);
                        }
                        mAdaptor.notifyDataSetChanged();

                        String mbName = object.getString("mb_name");
                        for (int i = 0; i < mAdaptor2.getCount(); i++) {
                            MemberNameVO memberNameVO = mAdaptor2.getItem(i);
                            if (memberNameVO.getMb_name().equals(mbName)) {
                                mb_name.setSelection(i);
                                break;
                            }
                        }
                        mAdaptor2.notifyDataSetChanged();

                        if (object.isNull("jukyo") || TextUtils.isEmpty(object.getString("jukyo"))) {
                            r_jukyo.setText("");
                        } else {
                            r_jukyo.setText(object.getString("jukyo"));
                        }

                        String r_gubun = object.getString("gubun");
                        if(r_gubun.equals("1")){
                            cash.setChecked(true);
                            sMethod = "cash";
                        }else  {
                            deposit.setChecked(true);
                            sMethod = "deposit";
                        }

                        r_amount.setText(object.getString("ramount"));

                        if (object.isNull("memo") || TextUtils.isEmpty(object.getString("memo"))) {
                            r_memo.setText("");
                        } else {
                            r_memo.setText(object.getString("memo"));
                        }
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

    @OnClick(R.id.r_ymd) void receipt_ymd() {
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
        if(TextUtils.isEmpty(r_ymd.getText())) {
            Util.showToast(getActivity(), "수납일을 입력하세요");
            return;
        }


        Log.d("LMG", "mb_name.getSelectedItem():" + mb_name.getSelectedItem());
        //if(TextUtils.isEmpty(mb_position.getSelectedItem().toString())) {
        if (inco_cd.getSelectedItem().toString()=="선택") {  //LMG Modify.20160312
            Util.showToast(getActivity(), "수납과목을 선택하세요");
            return;
        }
//        if (mb_name.getSelectedItem().toString()=="선택"){
        Log.d("LMG", "inco_cd.getSelectedItem().toString():" + inco_cd.getSelectedItem().toString());
        if (inco_cd.getSelectedItem().toString().equals("회원회비")&&(mb_name.getSelectedItem().toString()=="선택")){
            Util.showToast(getActivity(), "회원회비를 선택하였으면 회원을 입력하세요");
            return;
        }
        if (inco_cd.getSelectedItem().toString().equals("회원찬조금")&&(mb_name.getSelectedItem().toString()=="선택")){
            Util.showToast(getActivity(), "회원찬조금을 선택하였으면 회원을 입력하세요");
            return;
        }
        if (TextUtils.isEmpty(sMethod)) {
            Util.showToast(getActivity(), "수납수단을 선택하세요");
            return;
        }
        if(TextUtils.isEmpty(r_amount.getText())) {
            Util.showToast(getActivity(), "수납금액를 입력하세요");
            return;
        }
//        Log.d("LMG", "sMethod:" + sMethod);



        String url;
        url = Constant.HOST + Constant.API_ACC_INCOME_U;


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
        params.put("r_ymd_new", r_ymd.getText().toString());
        params.put("r_ymd_old", r_ymd_old.toString());
        params.put("inco_cd", ((IncomeSubjectVO)inco_cd.getSelectedItem()).getInco_cd());
        String m_name;
        if (mb_name.getSelectedItem().toString()=="선택") {  //LMG Modify.20160312
            m_name = "";
        }
        else {
            m_name = ((MemberNameVO)mb_name.getSelectedItem()).getMb_name();
        }
        params.put("mb_name",m_name );

        if (!TextUtils.isEmpty(r_jukyo.getText())) {
            params.put("r_jukyo", r_jukyo.getText().toString());
        }

        params.put("smethod", sMethod.toString());
        params.put("r_amount", r_amount.getText().toString());
        if (!TextUtils.isEmpty(r_memo.getText().toString())) {
            params.put("r_memo", r_memo.getText().toString());
        }
        params.put("junpyo_id", mAccountYmdVO.getJunpyo_id());




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
                        Util.showToast(getActivity(), "수정 되었습니다");
                        mR_ymd=object.getString("r_ymd");
                        mJunpyo_id=object.getString("junpyo_id");

//                        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new AccountReadYmdFragment(mMoimVo, mAccountYmdVO, io_ym))
//                                .addToBackStack(null)
//                                .commitAllowingStateLoss();
                        getActivity().getFragmentManager().beginTransaction()
                                .replace(R.id.container, new AccountRmdReceiptFragment(mMoimVo, mAccountYmdVO, mR_ymd, mJunpyo_id))
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

    @OnClick(R.id.del_submit) void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("강총무")
                .setMessage("수납전표를 삭제하시겠습니까?")
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
        url = Constant.HOST + Constant.API_ACC_INCOME_D;


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
        params.put("r_ymd", r_ymd.getText().toString());
        params.put("junpyo_id", mAccountYmdVO.getJunpyo_id());

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
                        Util.showToast(getActivity(), "전표가 삭제 되었습니다");
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
        switch(selected) {
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

            switch(v.getId()) {
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
                    if("n".equals(mMoimVo.getAdm_yn())) {
                        Util.showToast(getActivity(), "모임의 관리자만 등록이 가능합니다.");
                        mMenu2.setSelected(true);
                        return;
                    }
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
