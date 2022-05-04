package com.jacklee.tuckshopteacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.List;

public class ReportAdapter extends BaseAdapter {

    private Context context;
    private List<BuyRecord> buyList;

    LayoutInflater mInflater;
    public ReportAdapter(Context context, List<BuyRecord> buyList){
        this.context = context;
        this.buyList = buyList;
    }

    @Override
    public int getCount() {
        return buyList.size();
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
        rowView = inflater.inflate(R.layout.records_adapter, parent, false);

        Button recorddetail;
        TextView recordid, recorddate, recordtotalprice;

        recordid = (TextView) rowView.findViewById(R.id.rlistitem_id);
        recorddate = (TextView) rowView.findViewById(R.id.rlistitem_time);
        recordtotalprice = (TextView) rowView.findViewById(R.id.rlistitem_totalamount);
        recorddetail = (Button) rowView.findViewById(R.id.rlistitem_details);

        recordid.setText("#" + buyList.get(position).RecordId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        recorddate.setText(dateFormat.format(buyList.get(position).DateTime));

        recordtotalprice.setText("$" + String.format("%.2f", buyList.get(position).getTotalAmount()));

        recorddetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Food[] foods = buyList.get(position).getFoods();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Details of order #" + buyList.get(position).RecordId);
                String msg = "";
                for (Food food : foods) {
                    msg += "\n" + food.FoodName + " ($ " + String.format("%.2f",food.Price) + ")" + " x " + food.Quantity + " = " + "$ " + String.format("%.2f",food.Price * food.Quantity) + "\n";
                }

                msg += "\n\nTotal Consumption : $ " + String.format("%.2f", buyList.get(position).getTotalAmount());

                builder.setMessage(msg + "\n");
                builder.show();
            }
        });

        return rowView;
    }

}