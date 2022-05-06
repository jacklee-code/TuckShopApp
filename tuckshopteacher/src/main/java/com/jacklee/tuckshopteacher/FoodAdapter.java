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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodAdapter extends BaseAdapter {

    private Context context;
    private List<Food> foodList;
    private FoodType[] foodTypes;
    private int selectedIndex = 0;
    private ProgressBar progressBar;

    Handler handler;

    LayoutInflater mInflater;
    public FoodAdapter(Context context, List<Food> foodList, Handler handler, FoodType[] foodTypes, ProgressBar progressBar){
        this.context = context;
        this.foodList = foodList;
        this.handler = handler;
        this.foodTypes = foodTypes;
        this.progressBar = progressBar;
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
                    progressBar.setVisibility(View.VISIBLE);
                    String url = GlobalVariables.hostname + "/removeFood.php";
                    Map<String, String> hm = new HashMap<>();
                    hm.put("username", GlobalVariables.account.Username);
                    hm.put("password", GlobalVariables.account.getPassword());
                    hm.put("foodid", String.valueOf(foodList.get(position).FoodId));

                    HttpUtil.sendHTTPRequest(url, hm, new HttpCallbackListener() {

                        @Override
                        public void onFinish(String response) {
                            Message msg=handler.obtainMessage();
                            msg.what = HandleCode.RemoveFoodSuccess;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onError(Exception e) {
                            Message msg=handler.obtainMessage();
                            msg.what = HandleCode.Error_Msg;
                            msg.obj = e;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void OnForbidden() {
                            Message msg=handler.obtainMessage();
                            msg.what = HandleCode.RemoveFoodFailed;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void OnBadRequest() { }
                    });

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