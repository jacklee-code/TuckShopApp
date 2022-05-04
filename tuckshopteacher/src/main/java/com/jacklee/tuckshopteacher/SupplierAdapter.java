package com.jacklee.tuckshopteacher;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class SupplierAdapter extends BaseAdapter {

    private Context context;
    private List<Supplier> supplierList;
    private Handler handler;

    LayoutInflater mInflater;
    public SupplierAdapter(Context context, List<Supplier> supplierList, Handler handler){
        this.context = context;
        this.supplierList = supplierList;
        this.handler = handler;
    }

    @Override
    public int getCount() {
        return supplierList.size();
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
        
        ImageButton supplierremove;

        TextView supplierid, suppliername, supplierincome;

        supplierid = (TextView) rowView.findViewById(R.id.slistitem_id);
        suppliername = (TextView) rowView.findViewById(R.id.slistitem_name);
        supplierincome = (TextView) rowView.findViewById(R.id.slistitem_income);

        supplierremove = (ImageButton) rowView.findViewById(R.id.slistitem_delete);

        supplierid.setText("#" + supplierList.get(position).Id);
        suppliername.setText(supplierList.get(position).Name);
        supplierincome.setText("$" + String.format("%.2f", supplierList.get(position).Income));

        supplierremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Warning!");
                builder.setMessage("\nAre you sure you want to remove this account?\n\nPlease note that when you delete this supplier, the purchase record and food list related to it will be deleted at the same time.\n");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes (Irreversible)", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Message msg = handler.obtainMessage();
                        msg.what = HandleCode.DoUnlink;
                        msg.obj = supplierList.get(position).Id;
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