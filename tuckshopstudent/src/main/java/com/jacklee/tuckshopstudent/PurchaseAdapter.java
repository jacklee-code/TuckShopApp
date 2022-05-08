package com.jacklee.tuckshopstudent;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseAdapter extends BaseAdapter {

    private Context context;
    private List<Food> foodList;
    private HashMap<String, Integer> shoppingCart;
    private HashMap<String, Double> priceTable;
    private TextView totalAmountView;

    LayoutInflater mInflater;
    public PurchaseAdapter(Context context,List<Food> foodList, TextView totalAmountView){
        this.context = context;
        this.foodList = foodList;
        shoppingCart = new HashMap<>();
        priceTable = new HashMap<>();
        for (Food food : foodList)
            priceTable.put(food.FoodId, food.Price);

        this.totalAmountView = totalAmountView;
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

    public double getTotalAmount() {
        double sum = 0;
        for (Map.Entry<String, Integer> entry: shoppingCart.entrySet()) {
            sum += entry.getValue() * priceTable.get(entry.getKey());
        }
        return sum;
    }

    public ShoppingCart getShoppingCart(Account account) {
        ShoppingCart sc = new ShoppingCart();
        sc.Username = account.Username;
        sc.Password = account.getPassword();
        sc.ItemList = shoppingCart;
        return sc;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.purchase_adapter, parent, false);

        EditText quantity;
        TextView foodname, foodtype, foodprice;

        quantity = (EditText) rowView.findViewById(R.id.plistitem_quantity);
        foodname = (TextView) rowView.findViewById(R.id.plistitem_foodname);
        foodtype = (TextView) rowView.findViewById(R.id.plistitem_foodtype);
        foodprice = (TextView) rowView.findViewById(R.id.plistitem_foodprice);



        foodname.setTag(foodList.get(position).FoodId);
        quantity.setHint(foodList.get(position).Banned ? "Banned" : "MAX:" + foodList.get(position).Quantity);
        quantity.setEnabled(!foodList.get(position).Banned);
        foodname.setText(foodList.get(position).FoodName);
        foodtype.setText(foodList.get(position).FoodType);
        foodprice.setText("$" + String.format("%.2f", foodList.get(position).Price));

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
                    int iQuantity = Integer.parseInt(s.toString());
                    if (iQuantity > 0) {
                        int max = Integer.parseInt(quantity.getHint().toString().replace("MAX:", ""));
                        if (iQuantity <= max) {
                            String foodid = foodname.getTag().toString();
                            shoppingCart.put(foodid, iQuantity);

                        } else {
                            quantity.setText(s.toString().substring(0, s.length() - 1 ));
                            quantity.setSelection(quantity.getText().length());
                            Toast.makeText(context, "The quantity you want to buy exceeds the stock limit.", Toast.LENGTH_SHORT).show();
                            quantity.startAnimation(shakeError());

                        }
                    }
                } else {
                    int key = Integer.parseInt(foodname.getTag().toString());
                    if (shoppingCart.containsKey(key)){
                        shoppingCart.remove(key);
                    }
                }
                totalAmountView.setText("$ " + getTotalAmount());
            }
        });

        return rowView;
    }

    public TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 20, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }

}