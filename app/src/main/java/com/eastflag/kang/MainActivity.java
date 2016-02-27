package com.eastflag.kang;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.kang.dto.MoimVO;
import com.eastflag.kang.fragment.MoimAddFragment;
import com.eastflag.kang.fragment.MoimMainFragment;
import com.eastflag.kang.fragment.MoimViewFragment;
import com.eastflag.kang.fragment.Fragment120;
import com.eastflag.kang.fragment.Fragment400;
import com.eastflag.kang.fragment.IntroFragment;
import com.eastflag.kang.fragment.PasswordFragment;
import com.eastflag.kang.fragment.ServerWorkingFragment;
import com.eastflag.kang.utils.PreferenceUtil;
import com.eastflag.kang.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    private AQuery mAq;
    private Fragment mFragment;
    private FragmentManager mFm;

    @Bind(R.id.title) TextView mTitle;

    @Bind(R.id.menu) LinearLayout mMenu;
    @Bind(R.id.menu1) Button mMenu1;
    @Bind(R.id.menu2) Button mMenu2;
    @Bind(R.id.menu3) Button mMenu3;
    @Bind(R.id.menu5) Button mMenu5;

    @Bind(R.id.submenu) View mSubmenu;
    @Bind(R.id.submenu1) Button submenu1;
    @Bind(R.id.submenu2) Button submenu2;
    @Bind(R.id.submenu3) Button submenu3;
    @Bind(R.id.submenu4) Button submenu4;
    @Bind(R.id.submenu5) Button submenu5;
    @Bind(R.id.submenu6) Button submenu6;
    @Bind(R.id.submenu7) Button submenu7;

    private MoimVO selectedMoim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mAq = new AQuery(this);
        mFm = getFragmentManager();

        mMenu1.setOnClickListener(mMenuClick);
        mMenu2.setOnClickListener(mMenuClick);
        mMenu3.setOnClickListener(mMenuClick);
        mMenu5.setOnClickListener(mMenuClick);

        submenu1.setOnClickListener(mSubMenuClick);
        submenu2.setOnClickListener(mSubMenuClick);
        submenu3.setOnClickListener(mSubMenuClick);
        submenu4.setOnClickListener(mSubMenuClick);
        submenu5.setOnClickListener(mSubMenuClick);
        submenu6.setOnClickListener(mSubMenuClick);
        submenu7.setOnClickListener(mSubMenuClick);

        //메인 프래그먼트 노출
        mFragment = new IntroFragment();
        mFm.beginTransaction().replace(R.id.container, mFragment).commitAllowingStateLoss();

        getIntro();
    }

    @Override
    public void onBackPressed() {
        if(mFm.getBackStackEntryCount() > 1) {
            mFm.popBackStack();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("강총무")
                    .setMessage("종료하시겠습니까?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {

                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void getIntro() {
        String url = Constant.HOST + Constant.API_INTRO;

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(this)); //전화번호
        params.put("paid", Util.getAndroidId(this)); //안드로이드 아이디
        params.put("pm", Util.getDeviceName()); //폰모델
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
                        String value = object.getString("value");
                        if ("000".equals(value)) {
                            //토큰 등록
                            PreferenceUtil.getInstance(MainActivity.this).putToken(object.getString("token"));
                            //메인메뉴 display & 홈 탭 선택
                            showMenu(1, 0);
                            //010 모임 리스트 화면 이동
                            mFragment = new MoimMainFragment();
                            mFm.beginTransaction().replace(R.id.container, mFragment).addToBackStack(null).commitAllowingStateLoss();
                        } else if ("001".equals(value)) {
                            //이용 비번 등록 화면
                        } else if ("002".equals(value)) {
                            //이용 비번 확인 화면
                            mFragment = new PasswordFragment();
                            mFm.beginTransaction().replace(R.id.container, mFragment).addToBackStack(null).commitAllowingStateLoss();
                        } else if ("099".equals(value)) {
                            //서버 작업중
                            mFragment = new ServerWorkingFragment();
                            mFm.beginTransaction().replace(R.id.container, mFragment).addToBackStack(null).commitAllowingStateLoss();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showMenu(int selected, int subSelected) {
        if(selected > 0) {
            mMenu.setVisibility(View.VISIBLE);
        } else {
            mMenu.setVisibility(View.GONE);
        }

        mMenu1.setSelected(false);
        mMenu2.setSelected(false);
        mMenu3.setSelected(false);
        mMenu5.setSelected(false);
        switch(selected) {
            case 1:
                mMenu1.setSelected(true);
                break;
            case 2:
                mMenu2.setSelected(true);
                break;
            case 3:
                mMenu3.setSelected(true);
                break;
            case 5:
                mMenu5.setSelected(true);
                break;
        }

        if(subSelected>0) {
            submenu1.setVisibility(View.VISIBLE);
            showSubMenu(subSelected);
        } else {
            submenu1.setVisibility(View.GONE);
        }
    }

    public void showSubMenu(int selected) {
        submenu1.setSelected(false);
        submenu2.setSelected(false);
        submenu3.setSelected(false);
        submenu4.setSelected(false);
        submenu5.setSelected(false);
        switch (selected) {
            case 1:
                submenu1.setSelected(true);
                break;
            case 2:
                submenu2.setSelected(true);
                break;
            case 3:
                submenu3.setSelected(true);
                break;
        }
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void showSubMenu() {
        mSubmenu.setVisibility(View.VISIBLE);
    }

    public void hideSubMenu() {
        mSubmenu.setVisibility(View.GONE);
    }

    public void setSelectedMoim(MoimVO selectedMoim) {
        this.selectedMoim = selectedMoim;
    }

    @OnClick(R.id.menu4) void onClickMenu4() {
        DialogFragment frag = Fragment400.newInstance(R.string.help_title);
        frag.show(getFragmentManager(), "show");
    }

    View.OnClickListener mMenuClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            KangApplication.sApp.soundButton();
            hideSubMenu();
            mMenu1.setSelected(false);
            mMenu2.setSelected(false);
            mMenu3.setSelected(false);
            mMenu5.setSelected(false);
            switch(v.getId()) {
                case R.id.menu1:
                    mMenu1.setSelected(true);
                    mFragment = new MoimMainFragment();
                    mFm.beginTransaction().replace(R.id.container, mFragment).addToBackStack(null).commitAllowingStateLoss();
                    break;
                case R.id.menu2:
                    mMenu2.setSelected(true);
                    mFragment = new MoimAddFragment();
                    mFm.beginTransaction().replace(R.id.container, mFragment).addToBackStack(null).commitAllowingStateLoss();
                    break;
                case R.id.menu3:
                    mMenu3.setSelected(true);
                    break;
                case R.id.menu5:
//                    mMenu5.setSelected(true);
//                    mFragment = new MoimViewFragment();
//                    mFm.beginTransaction().replace(R.id.container, mFragment).commitAllowingStateLoss();
                    break;
                default:
                    break;
            }
        }
    };

    View.OnClickListener mSubMenuClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            KangApplication.sApp.soundButton();
            switch(v.getId()) {
                case R.id.submenu1:
                    showSubMenu(1);
                    mFragment = new MoimViewFragment(selectedMoim);
                    mFm.beginTransaction().replace(R.id.container, mFragment).addToBackStack(null).commitAllowingStateLoss();
                    break;
                case R.id.submenu2:
                    showSubMenu(2);
                    mFragment = new Fragment120();
                    mFm.beginTransaction().replace(R.id.container, mFragment).addToBackStack(null).commitAllowingStateLoss();
                    break;
            }
        }
    };

}
