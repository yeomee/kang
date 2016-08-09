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
import com.yeomee.kang.dto.AccountYmdVO;
import com.yeomee.kang.dto.ExpenseSubjectVO;
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
 * 회계 수납열람(수정/삭제) 화면
 */
public class AccountRmdExpenseFragment extends Fragment {

    private View mView;
    private AQuery mAq;
    private String sMethod;
    private String e_ymd_old;

    @Bind(R.id.menu1) Button mMenu1;//잔고
    @Bind(R.id.menu2) Button mMenu2;//전표열람
//    @Bind(R.id.menu3) Button mMenu3;//비용
    @Bind(R.id.menu4) Button mMenu4;//전표등록
    @Bind(R.id.menu5) Button mMenu5;//보고서

    @Bind(R.id.sc_help1) TextView sc_help1;//상단 도움말
//    @Bind(R.id.i_ymd) TextView i_ymd;
    @Bind(R.id.expen_cd) Spinner expen_cd;
    @Bind(R.id.mb_name) Spinner mb_name;
    @Bind(R.id.e_jukyo) EditText e_jukyo;

    @Bind(R.id.e_ymd) TextView e_ymd;
    @Bind(R.id.cash)    RadioButton cash;
    @Bind(R.id.deposit) RadioButton deposit;
    @Bind(R.id.e_amount) EditText e_amount;
    @Bind(R.id.e_memo) EditText e_memo;
    @Bind(R.id.layoutSubmit) View layoutSubmit;

    private String mbPosition;

    private ArrayList<ExpenseSubjectVO> mExpenseSubjectVOList = new ArrayList<ExpenseSubjectVO>();
    private ArrayAdapter<ExpenseSubjectVO> mAdaptor;

    private ArrayList<MemberNameVO> mMemberNameList = new ArrayList<MemberNameVO>();
    private ArrayAdapter<MemberNameVO> mAdaptor2;

    private MoimVO mMoimVo;
    private AccountYmdVO mAccountYmdVO;
    private  String mE_ymd;
    private  String mJunpyo_id;


    private Context mContext;
    public AccountRmdExpenseFragment() {
        // Required empty public constructor
    }


//    @SuppressLint("ValidFragment")  //모임관련 정보 얻기 위해.
//    public AccountRmdReceiptFragment(MoimVO moimVo) {
//        this.mMoimVo = moimVo;
//    }

    @SuppressLint("ValidFragment")    //수납전표번호 얻기 위해
    public AccountRmdExpenseFragment(MoimVO moimVo, AccountYmdVO accountYmdVO) {
        this.mMoimVo = moimVo;
        this.mAccountYmdVO = accountYmdVO;
    }
    @SuppressLint("ValidFragment")
    public AccountRmdExpenseFragment(MoimVO moimVo,AccountYmdVO accountYmdVO, String e_ymd, String junpyo_id) {
        this.mMoimVo = moimVo;
        this.mAccountYmdVO = accountYmdVO;
        this.mE_ymd = e_ymd;
        this.mJunpyo_id=junpyo_id;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity)getActivity()).setTitle(mMoimVo.getMn());

        mView = inflater.inflate(R.layout.fragment_account_read_expens, container, false);
        mAq = new AQuery(mView);
        AQUtility.setDebug(true);
        ((MainActivity)getActivity()).showMenu(1, 3);
        ((MainActivity)getActivity()).showSubMenu();
        ButterKnife.bind(this, mView);

        getScene();
        //관리자가 아니면 수정,삭제 버튼 비활성화
        Log.d("LMG", "mMoimVo.getAdm_yn():" + mMoimVo.getAdm_yn());
        if ("n".equals(mMoimVo.getAdm_yn())) {
            layoutSubmit.setVisibility(View.GONE);
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


        mAdaptor = new ArrayAdapter<ExpenseSubjectVO>(getActivity(), R.layout.spinner, mExpenseSubjectVOList);
        expen_cd.setAdapter(mAdaptor);

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
        url = Constant.HOST + Constant.API_SC_ACC_EP_R;

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_id", mMoimVo.getM_id());
        params.put("adm_yn", mMoimVo.getAdm_yn());
//        params.put("e_ymd", mAccountYmdVO.getYmd());
//        params.put("junpyo_id", mAccountYmdVO.getJunpyo_id());

        if (!TextUtils.isEmpty(mE_ymd)) {
            params.put("e_ymd", mE_ymd);
        }else{
            params.put("e_ymd", mAccountYmdVO.getYmd());
        }

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


                    ExpenseSubjectVO expense_cd = new ExpenseSubjectVO();
                    expense_cd.setExpense_cd("-1");
                    expense_cd.setExpense_name("선택");
                    mExpenseSubjectVOList.add(expense_cd);

                    MemberNameVO member_name = new MemberNameVO();
                    member_name.setMb_tel("-1");
                    member_name.setMb_name("선택");
                    mMemberNameList.add(member_name);



                    if (object.getInt("result") == 0) {
                        String scname_msg = "☞ " + object.getString("scname_msg");
                        sc_help1.setText(scname_msg);

                        if (object.isNull("ymd") || TextUtils.isEmpty(object.getString("ymd"))) {
                            e_ymd.setText("");
                        } else {
                            e_ymd.setText(object.getString("ymd"));
                        }
                        e_ymd_old=object.getString("ymd");

                        JSONArray array = object.getJSONArray("expense_cd");
                        for (int i = 0; i < array.length(); ++i) {

                            JSONObject json = array.getJSONObject(i);
                            expense_cd = new ExpenseSubjectVO();
                            expense_cd.setExpense_cd(json.getString("expns_cd"));
                            expense_cd.setExpense_name(json.getString("expns_name"));

                            mExpenseSubjectVOList.add(expense_cd);
                        }

                        String expnSubject = object.getString("subject");
                        for (int i = 0; i < mAdaptor.getCount(); i++) {
                            ExpenseSubjectVO expenseSubjectVO = mAdaptor.getItem(i);
                            if (expenseSubjectVO.getExpense_name().equals(expnSubject)) {
                                expen_cd.setSelection(i);
                                break;
                            }
                        }
                        mAdaptor.notifyDataSetChanged();


                        JSONArray array1 = object.getJSONArray("member_name");
                        for (int i = 0; i < array1.length(); ++i) {
                            JSONObject json = array1.getJSONObject(i);
                            member_name = new MemberNameVO();

                            member_name.setMb_tel(json.getString("mb_pn"));
                            member_name.setMb_name(json.getString("mb_name"));

                            mMemberNameList.add(member_name);
                        }

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
                            e_jukyo.setText("");
                        } else {
                            e_jukyo.setText(object.getString("jukyo"));
                        }

                        String e_gubun = object.getString("gubun");
                        if(e_gubun.equals("1")){
                            cash.setChecked(true);
                            sMethod = "cash";
                        }else  {
                            deposit.setChecked(true);
                            sMethod = "deposit";
                        }

                        e_amount.setText(object.getString("eamount"));

                        if (object.isNull("memo") || TextUtils.isEmpty(object.getString("memo"))) {
                            e_memo.setText("");
                        } else {
                            e_memo.setText(object.getString("memo"));
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
            e_ymd.setText(String.format("%d%02d%02d", year, monthOfYear + 1, dayOfMonth));
        }
    };

    @OnClick(R.id.e_ymd) void receipt_ymd() {
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
        if(TextUtils.isEmpty(e_ymd.getText())) {
            Util.showToast(getActivity(), "지출일을 입력하세요");
            return;
        }


        Log.d("LMG", "mb_name.getSelectedItem():" + mb_name.getSelectedItem());
        //if(TextUtils.isEmpty(mb_position.getSelectedItem().toString())) {
        if (expen_cd.getSelectedItem().toString()=="선택") {  //LMG Modify.20160312
            Util.showToast(getActivity(), "지출과목을 선택하세요");
            return;
        }
//        if (mb_name.getSelectedItem().toString()=="선택"){
        Log.d("LMG", "expen_cd.getSelectedItem().toString():" + expen_cd.getSelectedItem().toString());
        if (expen_cd.getSelectedItem().toString().equals("회원경사비")&&(mb_name.getSelectedItem().toString()=="선택")){
            Util.showToast(getActivity(), "회원경사비를 선택하였으면 회원을 입력하세요");
            return;
        }
        if (expen_cd.getSelectedItem().toString().equals("회원애사비")&&(mb_name.getSelectedItem().toString()=="선택")){
            Util.showToast(getActivity(), "회원애사비를 선택하였으면 회원을 입력하세요");
            return;
        }
        if (expen_cd.getSelectedItem().toString().equals("회원지원비")&&(mb_name.getSelectedItem().toString()=="선택")){
            Util.showToast(getActivity(), "회원지원비를 선택하였으면 회원을 입력하세요");
            return;
        }
        if (expen_cd.getSelectedItem().toString().equals("회원활동비")&&(mb_name.getSelectedItem().toString()=="선택")){
            Util.showToast(getActivity(), "회원활동비를 선택하였으면 회원을 입력하세요");
            return;
        }
        if (TextUtils.isEmpty(sMethod)) {
            Util.showToast(getActivity(), "지출수단을 선택하세요");
            return;
        }
        if(TextUtils.isEmpty(e_amount.getText())) {
            Util.showToast(getActivity(), "지출금액를 입력하세요");
            return;
        }
//        Log.d("LMG", "sMethod:" + sMethod);



        String url;
        url = Constant.HOST + Constant.API_ACC_EXPENSE_U;


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
//        params.put("e_ymd", e_ymd.getText().toString());
        params.put("e_ymd_new", e_ymd.getText().toString());
        params.put("e_ymd_old", e_ymd_old.toString());
        params.put("expen_cd", ((ExpenseSubjectVO)expen_cd.getSelectedItem()).getExpense_cd());
        String m_name;
        if (mb_name.getSelectedItem().toString()=="선택") {  //LMG Modify.20160312
            m_name = "";
        }
        else {
            m_name = ((MemberNameVO)mb_name.getSelectedItem()).getMb_name();
        }
        params.put("mb_name",m_name );

        if (!TextUtils.isEmpty(e_jukyo.getText())) {
            params.put("e_jukyo", e_jukyo.getText().toString());
        }

        params.put("smethod", sMethod.toString());
        params.put("e_amount", e_amount.getText().toString());
        if (!TextUtils.isEmpty(e_memo.getText().toString())) {
            params.put("e_memo", e_memo.getText().toString());
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
                        mE_ymd=object.getString("e_ymd");
                        mJunpyo_id=object.getString("junpyo_id");

                        getActivity().getFragmentManager().beginTransaction()
                                .replace(R.id.container, new AccountRmdExpenseFragment(mMoimVo, mAccountYmdVO, mE_ymd, mJunpyo_id))
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
                .setMessage("지출전표를 삭제하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        deleteExpense();
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

    public void deleteExpense() {

//        Log.d("LMG", "sMethod:" + sMethod);
        String url;
        url = Constant.HOST + Constant.API_ACC_EXPENSE_D;


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
        params.put("e_ymd", e_ymd.getText().toString());
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
