package com.jacklee.tuckshopparent;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class ProfileAdapter extends BaseAdapter {

    private Context context;
    private List<StudentProfile> studentList;
    private Account account;
    private Handler handler;

    LayoutInflater mInflater;
    public ProfileAdapter(Context context, List<StudentProfile> studentList, Account parentAccount, Handler handler){
        this.context = context;
        this.studentList = studentList;
        this.handler = handler;
    }

    @Override
    public int getCount() {
        return studentList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.profile_adapter, parent, false);

        Button profiletopup;
        TextView profileid, profilename, profilebalance;

        profileid = (TextView) rowView.findViewById(R.id.plistitem_id);
        profilename = (TextView) rowView.findViewById(R.id.plistitem_name);
        profilebalance = (TextView) rowView.findViewById(R.id.plistitem_balance);
        profiletopup = (Button) rowView.findViewById(R.id.plistitem_topup);

        profileid.setText("#" + studentList.get(position).UserId);
        profilename.setText(studentList.get(position).Fullname);
        profilebalance.setText("$" + String.format("%.2f", studentList.get(position).Balance));

        profiletopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: topup and send msg to handler when success
                HashMap<String, String> hashMap = new HashMap<>();


                // Test Handler;
                Message msg = handler.obtainMessage();
                msg.what = HandleCode.Test;
                handler.sendMessage(msg);
            }
        });

        return rowView;
    }

}