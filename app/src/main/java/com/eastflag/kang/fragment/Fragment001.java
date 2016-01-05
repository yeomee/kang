package com.eastflag.kang.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eastflag.kang.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Fragment001 extends Fragment {

    private View mView;
    @Bind(R.id.input11) TextView input11;
    @Bind(R.id.input12) TextView input12;
    @Bind(R.id.input13) TextView input13;
    @Bind(R.id.input14) TextView input14;
    @Bind(R.id.btn1) TextView btn1;
    @Bind(R.id.btn2) TextView btn2;
    @Bind(R.id.btn3) TextView btn3;
    @Bind(R.id.btn4) TextView btn4;
    @Bind(R.id.btn5) TextView btn5;
    @Bind(R.id.btn6) TextView btn6;
    @Bind(R.id.btn7) TextView btn7;
    @Bind(R.id.btn8) TextView btn8;
    @Bind(R.id.btn9) TextView btn9;
    @Bind(R.id.btn0) TextView btn0;
    @Bind(R.id.btnCL) TextView btnCL;
    @Bind(R.id.btnBS) TextView btnBS;

    private int mCount;
    private StringBuffer mInput = new StringBuffer();

    public Fragment001() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_001, container, false);
        mView.findViewById(R.id.reInput).setVisibility(View.GONE);

        ButterKnife.bind(this, mView);

        btn1.setOnClickListener(mClick);
        btn2.setOnClickListener(mClick);
        btn3.setOnClickListener(mClick);
        btn4.setOnClickListener(mClick);
        btn5.setOnClickListener(mClick);
        btn6.setOnClickListener(mClick);
        btn7.setOnClickListener(mClick);
        btn8.setOnClickListener(mClick);
        btn9.setOnClickListener(mClick);
        btn0.setOnClickListener(mClick);
        btnCL.setOnClickListener(mClick);
        btnBS.setOnClickListener(mClick);


        return mView;
    }

    View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btn1:
                    mInput.append("1");
                    break;
                case R.id.btn2:
                    mInput.append("2");
                    break;
                case R.id.btn3:
                    mInput.append("3");
                    break;
                case R.id.btn4:
                    mInput.append("4");
                    break;
                case R.id.btn5:
                    mInput.append("5");
                    break;
                case R.id.btn6:
                    mInput.append("6");
                    break;
                case R.id.btn7:
                    mInput.append("7");
                    break;
                case R.id.btn8:
                    mInput.append("8");
                    break;
                case R.id.btn9:
                    mInput.append("9");
                    break;
                case R.id.btn0:
                    mInput.append("0");
                    break;
                case R.id.btnCL:
                    mInput = new StringBuffer();
                    break;
                case R.id.btnBS:
                    if(mInput.length() > 0) {
                        mInput.substring(0, mInput.length() - 1);
                    }
                    break;
                default:
                    break;
            }

            Log.d("LDK", mInput.length() + "," + mInput.toString());

            input11.setBackgroundColor(mInput.length() >= 1 ? Color.BLACK : 0xFF996622);
            input12.setBackgroundColor(mInput.length() >= 2 ? Color.BLACK : 0xFF996622);
            input13.setBackgroundColor(mInput.length() >= 3 ? Color.BLACK : 0xFF996622);
            input14.setBackgroundColor(mInput.length() >= 4 ? Color.BLACK : 0xFF996622);

            if(mInput.length() == 4) {

            }
        }
    };

}
