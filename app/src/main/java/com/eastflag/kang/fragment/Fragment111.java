package com.eastflag.kang.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eastflag.kang.R;

/**
 * 회원 등록 화면
 */
public class Fragment111 extends Fragment {


    public Fragment111() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_111, container, false);
    }


}
