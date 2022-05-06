package com.jacklee.tuckshopparent;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileAdapter extends BaseAdapter {

    private Context context;
    private List<StudentProfile> studentList;
    private Handler handler;

    LayoutInflater mInflater;
    public ProfileAdapter(Context context, List<StudentProfile> studentList, Handler handler){
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
        ImageButton profileunlink;

        TextView profileid, profilename, profilebalance;

        profileid = (TextView) rowView.findViewById(R.id.plistitem_id);
        profilename = (TextView) rowView.findViewById(R.id.plistitem_name);
        profilebalance = (TextView) rowView.findViewById(R.id.plistitem_balance);
        profiletopup = (Button) rowView.findViewById(R.id.plistitem_topup);
        profileunlink = (ImageButton) rowView.findViewById(R.id.plistitem_unlink);

        profileid.setText("#" + studentList.get(position).UserId);
        profilename.setText(studentList.get(position).Fullname);
        profilebalance.setText("$" + String.format("%.2f", studentList.get(position).Balance));

        profiletopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Top-up");
                builder.setMessage("Top-up amount:");
                builder.setCancelable(false);

                final EditText input = new EditText(context);

                input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(input);
                builder.setPositiveButton("Top-up", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String number = input.getText().toString();
                        if (isValidDecimal(number)) {
                            TopUpDetail topUpDetail = new TopUpDetail();
                            topUpDetail.amount = number;
                            topUpDetail.targetId = studentList.get(position).UserId;
                            Message msg = handler.obtainMessage();
                            msg.what = HandleCode.DoTopUp;
                            msg.obj = topUpDetail;
                            handler.sendMessage(msg);
                        } else {
                            Toast.makeText(context, "Please input a valid value.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        profileunlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Unlink Confirmation");
                builder.setMessage("\nAre you sure you want to unlink this account?\n");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Message msg = handler.obtainMessage();
                        msg.what = HandleCode.DoUnlink;
                        msg.obj = studentList.get(position).UserId;
                        handler.sendMessage(msg);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        return rowView;
    }

    private boolean isValidDecimal(String numberStr) {
        return numberStr.matches("^\\d+$$") || numberStr.matches("\\d+\\.\\d+$");
    }

}