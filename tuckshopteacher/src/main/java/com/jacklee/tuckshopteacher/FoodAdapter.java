package com.jacklee.tuckshopteacher;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class FoodAdapter extends BaseAdapter {

    private Context context;
    private List<Food> foodList;
    private FoodType[] foodTypes;
    private int selectedIndex = 0;

    Handler handler;

    LayoutInflater mInflater;
    public FoodAdapter(Context context, List<Food> foodList, Handler handler, FoodType[] foodTypes){
        this.context = context;
        this.foodList = foodList;
        this.handler = handler;
        this.foodTypes = foodTypes;
    }

    @Override
    public int getCount() {
        return foodList.size();
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
        rowView = inflater.inflate(R.layout.food_adapter, parent, false);

        ImageButton edit, delete;
        TextView foodname, foodprice, foodquantity, foodtype, foodsupplier;

        edit = (ImageButton) rowView.findViewById(R.id.flistitem_edit);
        delete = (ImageButton) rowView.findViewById(R.id.flistitem_delete);

        foodname = (TextView) rowView.findViewById(R.id.flistitem_name);
        foodtype = (TextView) rowView.findViewById(R.id.flistitem_type);
        foodprice = (TextView) rowView.findViewById(R.id.flistitem_price);
        foodquantity = (TextView) rowView.findViewById(R.id.flistitem_quantity);
        foodsupplier = (TextView) rowView.findViewById(R.id.flistitem_supplier);

        foodname.setText(foodList.get(position).FoodName);
        foodtype.setText(foodList.get(position).FoodType);
        foodquantity.setText("" + foodList.get(position).Quantity);
        foodsupplier.setText(foodList.get(position).Supplier);
        foodprice.setText("$" + String.format("%.2f", foodList.get(position).Price));

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.flistitem_delete) {

                } else if (v.getId() == R.id.flistitem_edit) {
                    Message msg = handler.obtainMessage();
                    msg.what = HandleCode.DoEditFood;
                    msg.obj = foodList.get(position);
                    handler.sendMessage(msg);
                }
            }
        };

        edit.setOnClickListener(onClickListener);
        delete.setOnClickListener(onClickListener);

        return rowView;
    }



}