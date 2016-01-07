package com.eastflag.kang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eastflag.kang.R;
import com.eastflag.kang.dto.MoimVO;

import java.util.ArrayList;

/**
 * Created by eastflag on 2016-01-07.
 */
public class Adaptor010 extends BaseAdapter {

    private Context mContext;
    private ArrayList<MoimVO> mMoimList;

    public Adaptor010(Context context, ArrayList<MoimVO> moimList) {
        mContext = context;
        mMoimList = moimList;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.adaptor010, null);

            holder.number = (TextView) convertView.findViewById(R.id.number);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.chongmu = (TextView) convertView.findViewById(R.id.chongmu);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.number.setText(String.valueOf(position +1));
        holder.title.setText(mMoimList.get(position).getMn());
        holder.chongmu.setText("총무 ");
        holder.chongmu.append(mMoimList.get(position).getAdm_mb());

        return convertView;
    }

    class ViewHolder {
        TextView number;
        TextView title;
        TextView chongmu;
    }
}
