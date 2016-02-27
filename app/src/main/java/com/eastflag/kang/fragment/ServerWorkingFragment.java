package com.eastflag.kang.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eastflag.kang.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServerWorkingFragment extends Fragment {

    private View mView;

    public ServerWorkingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_server_working, container, false);
        ButterKnife.bind(this, mView);

        return mView;
    }

    @OnClick(R.id.btnClose)
    public void onClose() {
        getActivity().finish();
    }
}
