package com.jacklee.tuckshopteacher;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.jacklee.tuckshopteacher.databinding.FragmentFoodBinding;

import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodFragment extends Fragment {

    private FragmentFoodBinding binding;

    private List<Food> foodList;
    private ListView listView;
    private FoodAdapter foodAdapter;
    private Handler mHandler;
    private FoodType[] foodTypes;
    private Supplier[] supplierList;

    private ProgressBar edit_progressBar;

    private int selectedIndex = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFoodBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mHandler = new Handler(){
            public void handleMessage(Message msg) {
                try {
                    switch (msg.what) {
                        case HandleCode.GetFoodListSuccess:
                        {
                            foodList = Arrays.asList(new Gson().fromJson((String) msg.obj, Food[].class));

                            binding.foodNofood.setVisibility(foodList.size() == 0 ? View.VISIBLE : View.INVISIBLE);

                            // Create ListView
                            listView = binding.foodListview;
                            listView.setItemsCanFocus(true);
                            foodAdapter = new FoodAdapter(getActivity(), foodList, mHandler, foodTypes);
                            listView.setAdapter(foodAdapter);

                            binding.foodProgressBar.setVisibility(View.INVISIBLE);
                            binding.foodLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.GetFoodListFailed:
                        {
                            Toast.makeText(getContext(), "Failed to get food list, please try again.", Toast.LENGTH_SHORT);
                            binding.foodProgressBar.setVisibility(View.INVISIBLE);
                            binding.foodLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.Error_Msg:
                        {
                            String response=((Exception)msg.obj).toString();
                            Toast.makeText(getContext(), "An error occurred: " + response, Toast.LENGTH_SHORT).show();
                            Log.e("myerror", response);
                        }
                        break;

                        case HandleCode.FoodTypesSuccess:
                        {
                            foodTypes = new Gson().fromJson((String)msg.obj, FoodType[].class);
                            getSupplierList();
                        }
                        break;

                        case HandleCode.GetSupplierSuccess:
                        {
                            supplierList = new Gson().fromJson((String) msg.obj, Supplier[].class);
                            getFoodList("");
                        }
                        break;

                        case HandleCode.GetSupplierFailed:
                        case HandleCode.FoodTypesFailed:
                        case HandleCode.AddFoodFailed:
                        case HandleCode.RemoveFoodFailed:
                        case HandleCode.ChangeFailed:
                        {
                            Toast.makeText(getContext(), "Operation failed, please try again.", Toast.LENGTH_SHORT).show();
                            binding.foodProgressBar.setVisibility(View.INVISIBLE);
                            binding.foodLoading.setVisibility(View.INVISIBLE);
                        }
                        break;


                        default:
                            break;
                    }
                } catch (Exception e) {

                }


            };
        };

        // Button Listener
        AddFoodClicked();

        getFoodTypeList();

        return root;
    }

    private void AddFoodClicked() {
        binding.foodAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void getFoodTypeList() {
        binding.foodProgressBar.setVisibility(View.VISIBLE);
        binding.foodLoading.setVisibility(View.VISIBLE);

        HttpUtil.sendHTTPRequest(GlobalVariables.hostname + "/getFoodTypes.php", "", new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.FoodTypesSuccess;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {
                Log.e("myerror", e.toString());
            }

            @Override
            public void OnForbidden() {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.FoodTypesFailed;
                mHandler.sendMessage(msg);
            }

            @Override
            public void OnBadRequest() {

            }
        });
    }

    private void getSupplierList() {
        Map<String, String> hm = new HashMap<>();
        hm.put("username", GlobalVariables.account.Username);
        hm.put("password", GlobalVariables.account.getPassword());

        HttpUtil.sendHTTPRequest(GlobalVariables.hostname + "/getSuppliersInfo.php", hm, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.GetSupplierSuccess;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.Error_Msg;
                msg.obj = e;
                mHandler.sendMessage(msg);
            }

            @Override
            public void OnForbidden() {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.GetSupplierFailed;
                mHandler.sendMessage(msg);
            }

            @Override
            public void OnBadRequest() {

            }
        });
    }

    private void getFoodList(String studentid) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("username", GlobalVariables.account.Username);
        hashMap.put("password", GlobalVariables.account.getPassword());
        hashMap.put("targetid", studentid);



        HttpUtil.sendHTTPRequest(GlobalVariables.hostname + "/getFoodList.php", hashMap, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.GetFoodListSuccess;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {
                Log.e("myerror", e.toString());
            }

            @Override
            public void OnForbidden() {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.GetFoodListFailed;
                mHandler.sendMessage(msg);
            }

            @Override
            public void OnBadRequest() {

            }
        });
    }

    private void showFoodEditDialog(FoodType[] foodtypeList, Supplier[] supplierList, Food foodInfo, boolean editmode) {
        View food_view;

        Spinner edit_types, edit_suppliers;
        EditText edit_foodname, edit_price, edit_quantity;
        Button edit_add, edit_cancel;

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        food_view = inflater.inflate(R.layout.dialog_editfood,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(editmode ? "Edit Food Info" : "Add New Food");
        builder.setCancelable(false);
        builder.setView(food_view);
        Dialog dialog = builder.create();
        dialog.show();

        edit_foodname = (EditText) food_view.findViewById(R.id.dialog2_foodname);
        edit_price = (EditText) food_view.findViewById(R.id.dialog2_price);
        edit_quantity = (EditText) food_view.findViewById(R.id.dialog2_quantity);
        edit_types = (Spinner) food_view.findViewById(R.id.dialog2_foodtype);
        edit_suppliers = (Spinner) food_view.findViewById(R.id.dialog2_supplierlist);
        edit_add = (Button) food_view.findViewById(R.id.dialog2_add);
        edit_cancel = (Button) food_view.findViewById(R.id.dialog2_cancel);
        edit_progressBar = (ProgressBar) food_view.findViewById(R.id.dialog2_progressBar);

        // Change Text in Edit Mode
        if (editmode) {
            edit_foodname.setText(foodInfo.FoodName);
            edit_add.setText("Edit");
            edit_price.setText("$" + String.format("%.2f", foodInfo.Price));
            edit_quantity.setText("" + foodInfo.Quantity);
        }

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

        View.OnClickListener dialogClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.dialog2_cancel)
                    dialog.dismiss();

                else if (view.getId() == R.id.dialog2_add) {
                    edit_progressBar.setVisibility(View.VISIBLE);

                    Account ac = new Account();
                    ac.Username = edit_foodname.getText().toString();
                    ac.setPassword(edit_price.getText().toString());

                    String url = GlobalVariables.hostname + "/link.php";
                    Map<String, String> hm = new HashMap<>();
                    hm.put("username", GlobalVariables.account.Username);
                    hm.put("password", GlobalVariables.account.getPassword());
                    hm.put("linkusername", ac.Username);
                    hm.put("linkpassword", ac.getPassword());

                    HttpUtil.sendHTTPRequest(url, hm, new HttpCallbackListener() {

                        @Override
                        public void onFinish(String response) {
                            Message msg=mHandler.obtainMessage();
                            msg.what = HandleCode.LinkSuccess;
                            msg.obj = dialog;
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void onError(Exception e) {
                            Message msg=mHandler.obtainMessage();
                            msg.what = HandleCode.Error_Msg;
                            msg.obj = e;
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void OnForbidden() {
                            Message msg=mHandler.obtainMessage();
                            msg.what = HandleCode.LinkFailed;
                            msg.obj = dialog;
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void OnBadRequest() {
                            Message msg=mHandler.obtainMessage();
                            msg.what = HandleCode.LinkRepeated;
                            msg.obj = dialog;
                            mHandler.sendMessage(msg);
                        }
                    });
                }
            }
        };

        edit_foodname.addTextChangedListener(generalWatcher);
        edit_quantity.addTextChangedListener(generalWatcher);
        edit_add.setOnClickListener(dialogClickListener);
        edit_cancel.setOnClickListener(dialogClickListener);

        //TODO: Money Watcher

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

    private void loadSpinner(Spinner spinner, String[] list, int selectedIndex) {
        String[] typeNames = new String[foodTypes.length];
        for (int i = 0; i < typeNames.length; i++)
            typeNames[i] = foodTypes[i].Name;

        ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, typeNames);
        adp.setDropDownViewResource(R.layout.spinner_item);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}