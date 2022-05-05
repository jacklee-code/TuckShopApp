package com.jacklee.tuckshopteacher;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

public class FoodAdapter extends BaseAdapter {

    private Context context;
    private List<Food> foodList;

    Handler handler;

    LayoutInflater mInflater;
    public FoodAdapter(Context context, List<Food> foodList, Handler handler){
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
        rowView = inflater.inflate(R.layout.food_adapter, parent, false);

        Button ban;
        TextView foodname, foodtype, foodprice;

        ban = (Button) rowView.findViewById(R.id.flistitem_change);
        foodname = (TextView) rowView.findViewById(R.id.flistitem_name);
        foodtype = (TextView) rowView.findViewById(R.id.flistitem_type);
        foodprice = (TextView) rowView.findViewById(R.id.flistitem_price);

        foodname.setTag(foodList.get(position).FoodId);
        foodname.setText(foodList.get(position).FoodName);
        foodtype.setText(foodList.get(position).FoodType);
        foodprice.setText("$" + String.format("%.2f", foodList.get(position).Price));


        return rowView;
    }


}