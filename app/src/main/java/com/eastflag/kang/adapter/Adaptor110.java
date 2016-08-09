package com.eastflag.kang.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.eastflag.kang.R;
import com.eastflag.kang.dto.MemberVO;

import java.util.ArrayList;

/**
 * Created by oyg on 2016-01-07.
 */
public class Adaptor110 extends BaseAdapter {
    private Context mContext;
    private ArrayList<MemberVO> mMemberList;

    public Adaptor110(Context mContext, ArrayList<MemberVO> mMemberList) {
        this.mContext = mContext;
        this.mMemberList = mMemberList;
    }

    public void setData(ArrayList<MemberVO> mMemberList) {
        this.mMemberList = mMemberList;
    }

    @Override
    public int getCount() {
        return mMemberList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMemberList.get(position);
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
            convertView = View.inflate(mContext, R.layout.adaptor110, null);

            holder.number = (TextView) convertView.findViewById(R.id.tv_Num);
            holder.name = (TextView) convertView.findViewById(R.id.tv_Name);
            holder.position = (TextView) convertView.findViewById(R.id.tv_position);
            holder.phone = (TextView) convertView.findViewById(R.id.tv_phone);
            holder.sms = (TextView) convertView.findViewById(R.id.tv_sms);
            holder.admin = (TextView) convertView.findViewById(R.id.tv_admin);

            holder.phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + mMemberList.get(position).getMb_pn()));
                    mContext.startActivity(intent);
                }
            });
            holder.sms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("sms:" + mMemberList.get(position).getMb_pn()));
                    intent.putExtra("sms_body",  "");
                    mContext.startActivity(intent);
                }
            });

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.number.setText(String.valueOf(position + 1));
        holder.name.setText(mMemberList.get(position).getMb_name());
        holder.position.setText(mMemberList.get(position).getMy_position());

        if("y".equals(mMemberList.get(position).getAdmin_yn())) {
            holder.admin.setVisibility(View.VISIBLE);
        } else {
            holder.admin.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        TextView number;
        TextView name;
        TextView position;
        TextView phone;
        TextView sms;
        TextView admin;
    }
}
