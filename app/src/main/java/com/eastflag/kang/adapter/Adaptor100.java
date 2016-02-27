package com.eastflag.kang.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eastflag.kang.R;
import com.eastflag.kang.dto.MoimVO;
import com.eastflag.kang.fragment.MoimModifyFragment;
import com.eastflag.kang.listener.OnDismiss;

import java.util.ArrayList;

/**
 * Created by eastflag on 2016-01-07.
 */
public class Adaptor100 extends BaseAdapter {

    private Context mContext;
    private ArrayList<MoimVO> mMoimList;
    private OnDismiss mListener;

    public Adaptor100(Context context, ArrayList<MoimVO> moimList, OnDismiss listener) {
        mContext = context;
        mMoimList = moimList;
        mListener = listener;
    }

    public void setData(ArrayList<MoimVO> moimList) {
        mMoimList = moimList;
    }

    @Override
    public int getCount() {
        return mMoimList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMoimList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.adaptor100, null);

            holder.number = (TextView) convertView.findViewById(R.id.number);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.chongmu = (TextView) convertView.findViewById(R.id.chongmu);
            holder.adm_yn = (TextView) convertView.findViewById(R.id.modify);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.number.setText(String.valueOf(position + 1));
        holder.title.setText(mMoimList.get(position).getMn());
        holder.chongmu.setText("총무 ");
        holder.chongmu.append(mMoimList.get(position).getAdm_mb());

        if("y".equals(mMoimList.get(position).getAdm_yn())) {
            holder.adm_yn.setVisibility(View.VISIBLE);
        } else {
            holder.adm_yn.setVisibility(View.GONE);
        }

        if("0".equals(mMoimList.get(position).getM_status())) {
            holder.status.setVisibility(View.VISIBLE);
        } else {
            holder.status.setVisibility(View.GONE);
        }

        holder.adm_yn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoimVO vo = mMoimList.get(position);
                MoimModifyFragment dialog = MoimModifyFragment.newInstance(vo.getM_id(), vo.getMn(), vo.getAdm_mb(), vo.getAdm_email());
                dialog.setListner(mListener);
                dialog.show(((Activity)mContext).getFragmentManager(), "모임수정");
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView number;
        TextView title;
        TextView chongmu;
        TextView adm_yn;
        TextView status;
    }
}
