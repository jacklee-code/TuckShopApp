package com.jacklee.tuckshopteacher;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class SupplierAdapter extends BaseAdapter {

    private Context context;
    private List<StudentProfile> studentList;
    private Handler handler;

    LayoutInflater mInflater;
    public SupplierAdapter(Context context, List<StudentProfile> studentList, Handler handler){
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
        rowView = inflater.inflate(R.layout.supplier_adapter, parent, false);

        Button profiletopup;
        ImageButton profileunlink;

        TextView profileid, profilename, profilebalance;

        profileid = (TextView) rowView.findViewById(R.id.slistitem_id);
        profilename = (TextView) rowView.findViewById(R.id.slistitem_name);
        profilebalance = (TextView) rowView.findViewById(R.id.slistitem_income);

        profileunlink = (ImageButton) rowView.findViewById(R.id.slistitem_delete);

        profileid.setText("#" + studentList.get(position).UserId);
        profilename.setText(studentList.get(position).Fullname);
        profilebalance.setText("$" + String.format("%.2f", studentList.get(position).Balance));

        profileunlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Unlink Confirmation");
                builder.setMessage("\nAre you sure you want to unlink this account?\n");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes - Unlink", new DialogInterface.OnClickListener() {

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