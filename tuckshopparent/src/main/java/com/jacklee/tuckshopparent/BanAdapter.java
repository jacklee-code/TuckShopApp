package com.jacklee.tuckshopparent;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BanAdapter extends BaseAdapter {

    private Context context;
    private List<Food> foodList;

    Handler handler;

    LayoutInflater mInflater;
    public BanAdapter(Context context, List<Food> foodList, Handler handler){
        this.context = context;
        this.foodList = foodList;
        this.handler = handler;
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
        rowView = inflater.inflate(R.layout.ban_adapter, parent, false);

        ToggleButton ban;
        TextView foodname, foodtype, foodprice;

        ban = (ToggleButton) rowView.findViewById(R.id.blistitem_ban);
        foodname = (TextView) rowView.findViewById(R.id.blistitem_name);
        foodtype = (TextView) rowView.findViewById(R.id.blistitem_type);
        foodprice = (TextView) rowView.findViewById(R.id.blistitem_price);

        ban.setChecked(foodList.get(position).Banned);
        foodname.setTag(foodList.get(position).FoodId);
        foodname.setText(foodList.get(position).FoodName);
        foodtype.setText(foodList.get(position).FoodType);
        foodprice.setText("$" + String.format("%.2f", foodList.get(position).Price));

        ban.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Message msg = handler.obtainMessage();
                msg.what = isChecked ? HandleCode.DoBan : HandleCode.DoUnban;
                msg.obj = String.valueOf(foodList.get(position).FoodId);
                handler.sendMessage(msg);
            }
        });

        return rowView;
    }


}