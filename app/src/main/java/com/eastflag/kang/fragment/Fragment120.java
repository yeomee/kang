package com.eastflag.kang.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidquery.AQuery;
import com.eastflag.kang.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 회칙화면
 */
public class Fragment120 extends Fragment {

    private View mView;
    private AQuery mAq;

    public Fragment120() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_120, container, false);
        mAq = new AQuery(mView);
        ButterKnife.bind(this, mView);

        return mView;
    }
}
