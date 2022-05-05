package com.jacklee.tuckshopteacher;

import android.content.Context;
import android.media.Image;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

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

        ImageButton save, delete;
        EditText foodname, foodprice, foodquantity;
        Spinner foodtype;

        save = (ImageButton) rowView.findViewById(R.id.flistitem_savechange);
        setSaveEnable(save, false);

        delete = (ImageButton) rowView.findViewById(R.id.flistitem_delete);
        foodname = (EditText) rowView.findViewById(R.id.flistitem_name);
        foodtype = (Spinner) rowView.findViewById(R.id.flistitem_type);
        foodprice = (EditText) rowView.findViewById(R.id.flistitem_price);
        foodquantity = (EditText) rowView.findViewById(R.id.flistitem_quantity);

        foodname.setTag(foodList.get(position).FoodId);
        foodname.setText(foodList.get(position).FoodName);
        foodquantity.setText("" + foodList.get(position).Quantity);
        foodprice.setText("$" + String.format("%.2f", foodList.get(position).Price));

        //Load Type
        loadSpinner(foodtype, foodList.get(position).TypeId);

        //Load Watcher
        foodprice.addTextChangedListener(new DollarWatcher(foodprice, save, foodname, foodquantity));

        TextWatcher generalWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setSaveEnable(save, foodname.length() > 0 && isValidDecimal(foodprice.getText().toString().replace("$", "")) && isInteger(foodquantity.getText().toString()));
            }
        };

        foodname.addTextChangedListener(generalWatcher);
        foodquantity.addTextChangedListener(generalWatcher);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.flistitem_delete) {

                } else if (v.getId() == R.id.flistitem_savechange) {

                }
            }
        };

        save.setOnClickListener(onClickListener);
        delete.setOnClickListener(onClickListener);


        return rowView;
    }

    private int getIndexByFoodId(int foodid) {
        int i = 0;
        for (FoodType type : foodTypes) {
            if (type.Id == foodid)
                return i;
            i++;
        }
        return -1;
    }

    private void loadSpinner(Spinner spinner, int foodid) {
        String[] typeNames = new String[foodTypes.length];
        for (int i = 0; i < typeNames.length; i++)
            typeNames[i] = foodTypes[i].Name;

        ArrayAdapter<String> adp = new ArrayAdapter<String>(context, R.layout.spinner_foodtype, typeNames);
        adp.setDropDownViewResource(R.layout.spinner_foodtype);
        spinner.setAdapter(adp);

        int index = getIndexByFoodId(foodid);
        if (index != -1)
            spinner.setSelection(index);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                selectedIndex = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    private class DollarWatcher implements TextWatcher {
        private final EditText et;
        private final EditText foodname, quantity;
        private final ImageButton saveButton;

        public DollarWatcher(EditText editText, ImageButton saveButton, EditText foodname, EditText quantity) {
            this.et = editText;
            this.foodname = foodname;
            this.saveButton = saveButton;
            this.quantity = quantity;
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                et.removeTextChangedListener(this);


                while (s.length() != 0 && s.toString().toCharArray()[0] != '$') {
                    s.delete(0, 1);
                }

                if (s.length() < 1) {
                    et.setText("$");
                    et.setSelection(s.length() + 1);
                }

                setSaveEnable(saveButton, foodname.length() > 0 && isValidDecimal(s.toString().replace("$", "")) && isInteger(quantity.getText().toString()));

                et.addTextChangedListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    }

    private void setSaveEnable(ImageButton saveButton, boolean enable) {
        saveButton.setEnabled(enable);
        saveButton.setImageAlpha(enable ? 255 : 75);
    }

    private boolean isValidDecimal(String numberStr) {
        return numberStr.matches("^\\d+$$") || numberStr.matches("\\d+\\.\\d+$");
    }

    private boolean isInteger(String numberStr) {
        return numberStr.matches("\\d+");
    }

}