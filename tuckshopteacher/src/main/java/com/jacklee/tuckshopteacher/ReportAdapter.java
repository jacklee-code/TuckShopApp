package com.jacklee.tuckshopteacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class ReportAdapter extends BaseAdapter {

    private Context context;
    private List<ProductDetail> productList;

    LayoutInflater mInflater;
    public ReportAdapter(Context context, List<ProductDetail> productList){
        this.context = context;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
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
        rowView = inflater.inflate(R.layout.report_adapter, parent, false);

        TextView reportrecordid, reportcustomer, reportdatetime, reportincome, reportquantity;

        reportrecordid = (TextView) rowView.findViewById(R.id.reportlistitem_recordid);
        reportcustomer = (TextView) rowView.findViewById(R.id.reportlistitem_customer);
        reportdatetime = (TextView) rowView.findViewById(R.id.reportlistitem_time);
        reportincome = (TextView) rowView.findViewById(R.id.reportlistitem_income);
        reportquantity = (TextView) rowView.findViewById(R.id.reportlistitem_quantity);

        reportrecordid.setText("#" + productList.get(position).RecordId);
        reportcustomer.setText(productList.get(position).Customer);
        reportquantity.setText(String.valueOf(productList.get(position).Quantity));
        reportincome.setText("$ " + String.format("%.2f", productList.get(position).Income));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        reportdatetime.setText(dateFormat.format(productList.get(position).Time));

        return rowView;
    }

}