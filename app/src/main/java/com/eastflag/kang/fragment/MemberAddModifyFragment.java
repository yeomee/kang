package com.eastflag.kang.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.eastflag.kang.Constant;
import com.eastflag.kang.KangApplication;
import com.eastflag.kang.MainActivity;
import com.eastflag.kang.R;
import com.eastflag.kang.dto.MemberVO;
import com.eastflag.kang.dto.MoimVO;
import com.eastflag.kang.dto.PositionVo;
import com.eastflag.kang.utils.PreferenceUtil;
import com.eastflag.kang.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 회원 등록 화면
 */
public class MemberAddModifyFragment extends Fragment {
    public static final int MAX_IMAGE_SIZE = 1000;
    final int REQ_CODE_SELECT_IMAGE=100;

    private final int MODE_REG = 0;
    private final int MODE_MODIFY = 1;

    private View mView;
    private AQuery mAq;

    @Bind(R.id.menu1) Button mMenu1;
    @Bind(R.id.menu2) Button mMenu2;
    @Bind(R.id.menu3) Button mMenu3;

    @Bind(R.id.title) TextView title;
    @Bind(R.id.photo) ImageView mb_photo;
    @Bind(R.id.layoutPhoto) View layoutPhoto;
    @Bind(R.id.mb_name) EditText mb_name;
    @Bind(R.id.mb_position) Spinner mb_position;
    @Bind(R.id.mb_pn) EditText mb_pn;
    @Bind(R.id.mb_add) EditText mb_add;
    @Bind(R.id.mb_enter_ymd) TextView mb_enter_ymd;
    @Bind(R.id.mb_actions) EditText mb_actions;
    @Bind(R.id.submit) Button submit;
    @Bind(R.id.delete) Button delete;
    @Bind(R.id.layoutSubmit) View layoutSubmit;

    private ArrayList<PositionVo> mPositionList = new ArrayList<PositionVo>();
    private ArrayAdapter<PositionVo> mAdaptor;

    private String mTitle;

    private MoimVO mMoimVo;
    private MemberVO mMemberVo;
    private String mbName;
    private String mbPosition;
    private String mbPn;
    private String mbAddr;
    private String mbActions;

    private int screenMode;
    private Bitmap mBitmapPhoto;

    public MemberAddModifyFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public MemberAddModifyFragment(MoimVO moimVo) {
        this.mMoimVo = moimVo;
        screenMode = MODE_REG;
    }

    @SuppressLint("ValidFragment")
    public MemberAddModifyFragment(MoimVO moimVo, MemberVO memberVo) {
        this.mMoimVo = moimVo;
        this.mMemberVo = memberVo;
        screenMode = MODE_MODIFY;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity)getActivity()).setTitle(mMoimVo.getMn());

        mView = inflater.inflate(R.layout.fragment_member_add_modify, container, false);
        mAq = new AQuery(mView);
        AQUtility.setDebug(true);
        ButterKnife.bind(this, mView);

        mAdaptor = new ArrayAdapter<PositionVo>(getActivity(), R.layout.spinner, mPositionList);
        mb_position.setAdapter(mAdaptor);

        getPositionList();

        //관리자가 아니면 수정,삭제 버튼 비활성화
        if ("n".equals(mMoimVo.getAdm_yn())) {
            layoutSubmit.setVisibility(View.GONE);
        }

        if(screenMode == MODE_REG) { // 등록 모드
            mMenu2.setSelected(true);
            ((MainActivity)getActivity()).showMenu(1, 1);
            layoutPhoto.setVisibility(View.GONE);
        }
        else { // 수정 모드
            mMenu3.setSelected(true);
            ((MainActivity) getActivity()).showMenu(1, 1);
            layoutPhoto.setVisibility(View.VISIBLE);

            submit.setText("회원 수정");
            delete.setVisibility(View.VISIBLE);

            mb_name.setText(mMemberVo.getMb_name());
            for(int i = 0; i < mAdaptor.getCount(); i++) {
                PositionVo positionVo = mAdaptor.getItem(i);
                if(positionVo.getPo_name().equals(mMemberVo.getMy_position())) {
                    mb_position.setSelection(i);
                    break;
                }
            }
            mb_pn.setText(mMemberVo.getMb_pn());
            mb_add.setText(mMemberVo.getMb_add());
            mb_enter_ymd.setText(mMemberVo.getMb_enter_ymd());
            mb_actions.setText(mMemberVo.getMb_action());
        }

        mMenu1.setOnClickListener(mMenuClick);

        return mView;
    }

    public void getPositionList() {
        String url;
        if(screenMode == MODE_REG) {
            url = Constant.HOST + Constant.API_111_REG_SCENE;
        }
        else {
            url = Constant.HOST + Constant.API_111_MOD_SCENE;
        }

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_id", mMoimVo.getM_id());
        if(screenMode == MODE_MODIFY) {
            params.put("mb_id", mMemberVo.getMb_id());
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

                    //데이터 존재하지 않음
                    PositionVo position = new PositionVo();
                    position.setPo_cd("-1");
                    position.setPo_name("직책을 선택하세요.");
                    mPositionList.add(position);

                    if (object.getInt("result") == 0) {
                        if (screenMode == MODE_REG) {
                            String scname_msg = object.getString("scname_msg1");
                            title.setText(scname_msg);

                            JSONArray array = object.getJSONArray("position");
                            for (int i = 0; i < array.length(); ++i) {
                                JSONObject json = array.getJSONObject(i);
                                position = new PositionVo();

                                position.setPo_cd(json.getString("po_cd"));
                                position.setPo_name(json.getString("po_name"));

                                mPositionList.add(position);
                            }

                            mb_position.setSelection(0);
                        } else {
                            String scname_msg = object.getString("scname_msg");
                            title.setText(scname_msg);

                            if (object.isNull("mb_actions") || TextUtils.isEmpty(object.getString("mb_actions"))) {
                                mb_actions.setText("");
                            } else {
                                mb_actions.setText(object.getString("mb_actions"));
                            }

                            if (object.isNull("mb_add") || TextUtils.isEmpty(object.getString("mb_add"))) {
                                mb_add.setText("");
                            } else {
                                mb_add.setText(object.getString("mb_add"));
                            }

                            if (object.isNull("mb_enter_ymd") || TextUtils.isEmpty(object.getString("mb_enter_ymd"))) {
                                mb_enter_ymd.setText("");
                            } else {
                                mb_enter_ymd.setText(object.getString("mb_enter_ymd"));
                            }

                            JSONArray array = object.getJSONArray("position_list");
                            for (int i = 0; i < array.length(); ++i) {
                                JSONObject json = array.getJSONObject(i);
                                position = new PositionVo();

                                position.setPo_cd(json.getString("po_cd"));
                                position.setPo_name(json.getString("po_name"));

                                mPositionList.add(position);
                            }

                            String mbPosition = object.getString("mb_position");
                            for (int i = 0; i < mAdaptor.getCount(); i++) {
                                PositionVo positionVo = mAdaptor.getItem(i);
                                if (positionVo.getPo_name().equals(mbPosition)) {
                                    mb_position.setSelection(i);
                                    break;
                                }
                            }

                            mb_name.setText(object.getString("mb_name"));
                            mb_pn.setText(object.getString("mb_pn"));

                            if (!TextUtils.isEmpty(object.getString("mb_img"))) {
                                mAq.id(mb_photo).image(object.getString("mb_img"));
                            }
                        }

                        mAdaptor.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.submit)
    public void submit() {
        if(TextUtils.isEmpty(mb_name.getText())) {
            Util.showToast(getActivity(), "회원 이름을 입력하세요");
            return;
        }

        if(TextUtils.isEmpty(mb_position.getSelectedItem().toString())) {
            Util.showToast(getActivity(), "회원 직책을 선택하세요");
            return;
        }

        if(TextUtils.isEmpty(mb_pn.getText())) {
            Util.showToast(getActivity(), "회원 전화번호를 입력하세요");
            return;
        }

        if(TextUtils.isEmpty(mb_enter_ymd.getText())) {
            Util.showToast(getActivity(), "회원 가입일을 입력하세요");
            return;
        }

        String url;
        if(screenMode == MODE_REG) {
            url = Constant.HOST + Constant.API_111;
        }
        else {
            url = Constant.HOST + Constant.API_112;
        }

        Log.d("LDK", "url:" + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pn", Util.getMdn(getActivity())); //전화번호
        params.put("paid", Util.getAndroidId(getActivity())); //안드로이드 아이디
        params.put("token", PreferenceUtil.getInstance(getActivity()).getToken()); //폰모델
        params.put("m_id", mMoimVo.getM_id());
        params.put("mb_name", mb_name.getText().toString());
        params.put("mb_position", ((PositionVo)mb_position.getSelectedItem()).getPo_cd());
        params.put("mb_pn", mb_pn.getText().toString());
        if (!TextUtils.isEmpty(mb_add.getText())) {
            params.put("mb_add", mb_add.getText().toString());
        }
        params.put("mb_enter_ymd", mb_enter_ymd.getText().toString());
        params.put("mb_actions", mb_actions.getText().toString());
        if(screenMode == MODE_MODIFY) {
            params.put("mb_id", mMemberVo.getMb_id());
            if(mBitmapPhoto != null) {
                params.put("file", bitmapToByteArray(mBitmapPhoto));
            }
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
                    if(screenMode == MODE_REG) {
                        Util.showToast(getActivity(), "등록 되었습니다");
                    }
                    else {
                        Util.showToast(getActivity(), "수정 되었습니다");
                    }
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new MemberListFragment(mMoimVo))
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                } else {
                    Util.showToast(getActivity(), object.getString("scname_msg"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        });
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @OnClick(R.id.mb_enter_ymd) void enter_ymd() {
        final GregorianCalendar calendar = new GregorianCalendar();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    @OnClick(R.id.photo) void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");              // 모든 이미지
        intent.putExtra("crop", "true");        // Crop기능 활성화
        intent.putExtra("scale", true);
        intent.putExtra("outputX",  800);
        intent.putExtra("outputY",  800);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY",  1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Util.getTempFile()));     // 임시파일 생성
        intent.putExtra("outputFormat",         // 포맷방식
                Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }

    @OnClick(R.id.btnZoom) void imageZoom() {
        Bitmap bitmap = ((BitmapDrawable)mb_photo.getDrawable()).getBitmap();
        if(bitmap == null) {
            return;
        }

        View view = View.inflate(getActivity(), R.layout.image_zoom, null);
        ImageView img_zoom = (ImageView) view.findViewById(R.id.img_zoom);
        img_zoom.setImageBitmap(bitmap);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE_SELECT_IMAGE) {

            if(resultCode == getActivity().RESULT_OK) {
                String filePath = Util.getTempFile().getAbsolutePath();

                // 이미지 메타정보 얻어오기
                // First decode with inJustDecodeBounds=true to check dimensions
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(filePath, options);
                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;
                Log.d("LDK", "width:" + imageWidth + ", height:" + imageHeight);
                options.inSampleSize = Util.calculateInSampleSize(imageHeight, imageWidth);

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                mBitmapPhoto = BitmapFactory.decodeFile(filePath, options);

                //배치해놓은 ImageView에 set
                mb_photo.setImageBitmap(mBitmapPhoto);
            }
        }
    }

    public String getImageNameToUri(Uri data) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mb_enter_ymd.setText(String.format("%d%02d%02d", year, monthOfYear + 1, dayOfMonth));
        }
    };

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
                    mFragment = new MemberListFragment(mMoimVo);
                    getFragmentManager().beginTransaction().replace(R.id.container, mFragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    break;
//                case R.id.menu2:
//                    mMenu2.setSelected(true);
//                    mFragment = new MoimAddFragment();
//                    mFm.beginTransaction().replace(R.id.container, mFragment).commitAllowingStateLoss();
//                    break;
//                case R.id.menu3:
//                    mMenu3.setSelected(true);
//                    break;
                default:
                    break;
            }
        }
    };


}
