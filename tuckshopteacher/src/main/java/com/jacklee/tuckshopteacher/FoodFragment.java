package com.jacklee.tuckshopteacher;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompatSideChannelService;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.zxing.Result;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jacklee.tuckshopteacher.databinding.FragmentFoodBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class FoodFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private FragmentFoodBinding binding;

    ZXingScannerView qr_view;
    Button edit_add, edit_cancel;
    Button stopscan;
    EditText edit_foodid;

    private List<Food> foodList;
    private ListView listView;
    private FoodAdapter foodAdapter;
    private Handler mHandler;
    private FoodType[] foodTypes;
    private Supplier[] suppliers;

    private int selectedSupplierId, selectedFoodTypeId;

    private ProgressBar edit_progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFoodBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //取得相機權限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        } else {

        }

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
                            foodAdapter = new FoodAdapter(getActivity(), foodList, mHandler, foodTypes, binding.foodProgressBar);
                            listView.setAdapter(foodAdapter);

                            binding.foodProgressBar.setVisibility(View.INVISIBLE);
                            binding.foodLoading.setVisibility(View.INVISIBLE);
                            binding.foodAdd.setEnabled(true);
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
                            suppliers = new Gson().fromJson((String) msg.obj, Supplier[].class);
                            getFoodList("");
                        }
                        break;

                        case  HandleCode.DoEditFood:
                        {
                            Food food = (Food) msg.obj;
                            showFoodEditDialog(food, true);
                        }
                        break;

                        case HandleCode.GetSupplierFailed:
                        case HandleCode.FoodTypesFailed:
                        case HandleCode.EditFoodFailed:
                        case HandleCode.RemoveFoodFailed:
                        {
                            Toast.makeText(getContext(), "Operation failed, please try again.", Toast.LENGTH_SHORT).show();
                            binding.foodProgressBar.setVisibility(View.INVISIBLE);
                            binding.foodLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.RemoveFoodSuccess:
                        {
                            getFoodTypeList();
                            Toast.makeText(getContext(), "Data successfully deleted", Toast.LENGTH_SHORT).show();
                        }
                        break;

                        case HandleCode.EditFoodSuccess:
                        {
                            Toast.makeText(getContext(), "Update food information successfully", Toast.LENGTH_SHORT).show();
                            ((Dialog) msg.obj).dismiss();
                            getFoodTypeList();
                        }
                        break;

                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                showFoodEditDialog(null, false);
            }
        });
    }

    private void getFoodTypeList() {
        binding.foodAdd.setEnabled(false);
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

    private void openQRCamera(){
        qr_view.setVisibility(View.VISIBLE);
        stopscan.setVisibility(View.VISIBLE);
        edit_add.setVisibility(View.INVISIBLE);
        edit_cancel.setVisibility(View.INVISIBLE);
        qr_view.setResultHandler(this);
        qr_view.startCamera();
    }

    private void stopCamera() {
        qr_view.stopCamera();
        edit_add.setVisibility(View.VISIBLE);
        edit_cancel.setVisibility(View.VISIBLE);
        qr_view.setVisibility(View.GONE);
        stopscan.setVisibility(View.GONE);
        super.onStop();
    }

    private void showFoodEditDialog(Food foodInfo, boolean editmode) {
        View food_view;

        Spinner edit_types, edit_suppliers;
        EditText edit_foodname, edit_price, edit_quantity;
        ImageButton edit_scan;

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        food_view = inflater.inflate(R.layout.dialog_editfood,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(editmode ? "Edit Food Info" : "Add New Food");
        builder.setCancelable(false);
        builder.setView(food_view);
        Dialog dialog = builder.create();
        dialog.show();

        edit_foodid = (EditText) food_view.findViewById(R.id.dialog2_foodid);
        edit_foodname = (EditText) food_view.findViewById(R.id.dialog2_foodname);
        edit_price = (EditText) food_view.findViewById(R.id.dialog2_price);
        edit_quantity = (EditText) food_view.findViewById(R.id.dialog2_quantity);
        edit_types = (Spinner) food_view.findViewById(R.id.dialog2_foodtype);
        edit_suppliers = (Spinner) food_view.findViewById(R.id.dialog2_supplierlist);
        edit_add = (Button) food_view.findViewById(R.id.dialog2_add);
        edit_cancel = (Button) food_view.findViewById(R.id.dialog2_cancel);
        edit_progressBar = (ProgressBar) food_view.findViewById(R.id.dialog2_progressBar);
        edit_scan = (ImageButton) food_view.findViewById(R.id.dialog2_scan);
        qr_view = food_view.findViewById(R.id.dialog2_qrview);
        stopscan = (Button) food_view.findViewById(R.id.dialog2_stopscan);

        edit_scan.setEnabled(!editmode);
        edit_foodid.setEnabled(!editmode);
        edit_scan.setImageAlpha(!editmode ? 255 : 75);

        edit_price.setText("$");

        int i = 0;
        int typeIndex = -1;
        int supplierIndex = -1;

        String[] foodtypelist = new String[foodTypes.length];
        for (FoodType type : foodTypes) {
            foodtypelist[i] = type.Name;
            if (editmode) {
                if (foodInfo.TypeId == type.Id)
                    typeIndex = i;
            }
            i++;
        }

        i = 0;
        String[] supplernamelist = new String[suppliers.length];
        for (Supplier supplier : suppliers) {
            supplernamelist[i] = supplier.Name;
            if (editmode) {
                if (foodInfo.SupplierId.equals(supplier.Id))
                    supplierIndex = i;
            }
            i++;
        }

        loadSpinner(edit_types, foodtypelist, typeIndex);
        loadSpinner(edit_suppliers, supplernamelist, supplierIndex);

        // Change Text in Edit Mode
        if (editmode) {
            edit_foodid.setText("" + foodInfo.FoodId );
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
                edit_add.setEnabled(edit_foodname.length() > 0 &&
                                    isValidDecimal(edit_price.getText().toString().replace("$", "")) &&
                                    isInteger(edit_quantity.getText().toString()) &&
                                    edit_types.getSelectedItem() != null &&
                                    edit_suppliers.getSelectedItem() != null &&
                                    edit_foodid.length() > 0);
            }
        };

        edit_foodname.addTextChangedListener(generalWatcher);
        edit_quantity.addTextChangedListener(generalWatcher);

        edit_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        View.OnClickListener dialogClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.dialog2_stopscan)
                    stopCamera();
                else if (view.getId() == R.id.dialog2_cancel)
                    dialog.dismiss();

                else if (view.getId() == R.id.dialog2_add) {
                    edit_progressBar.setVisibility(View.VISIBLE);

                    String url = GlobalVariables.hostname + "/updateFood.php";
                    Map<String, String> hm = new HashMap<>();
                    hm.put("username", GlobalVariables.account.Username);
                    hm.put("password", GlobalVariables.account.getPassword());
                    hm.put("foodname", edit_foodname.getText().toString());
                    hm.put("quantity", edit_quantity.getText().toString());
                    hm.put("price", edit_price.getText().toString().replace("$", ""));
                    hm.put("typeid", String.valueOf(foodTypes[edit_types.getSelectedItemPosition()].Id));
                    hm.put("supplierid", String.valueOf(suppliers[edit_suppliers.getSelectedItemPosition()].Id));
                    hm.put("foodid", edit_foodid.getText().toString());
                    if (editmode)
                        hm.put("editmode", "true");

                    HttpUtil.sendHTTPRequest(url, hm, new HttpCallbackListener() {

                        @Override
                        public void onFinish(String response) {
                            Message msg=mHandler.obtainMessage();
                            msg.what = HandleCode.EditFoodSuccess;
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
                            msg.what = HandleCode.EditFoodFailed;
                            msg.obj = dialog;
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void OnBadRequest() { }
                    });
                }
            }
        };

        edit_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQRCamera();
            }
        });

        edit_add.setOnClickListener(dialogClickListener);
        edit_cancel.setOnClickListener(dialogClickListener);
        stopscan.setOnClickListener(dialogClickListener);

        edit_price.addTextChangedListener(
                new DollarWatcher(edit_price, edit_add, edit_foodname, edit_quantity, edit_types, edit_suppliers)
        );

    }

    private void loadSpinner(Spinner spinner, String[] list, int selectedIndex) {
        ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, list);
        adp.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adp);

        if (selectedIndex != -1)
            spinner.setSelection(selectedIndex);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    @Override
    public void handleResult(Result result) {
        stopCamera();
        edit_foodid.setText(result.getText());
    }



    private class DollarWatcher implements TextWatcher {
        private final EditText et, foodname, quantity;
        private final Button button;
        private final Spinner sp1, sp2;

        public DollarWatcher(EditText editText, Button button, EditText foodname, EditText quantity, Spinner sp1, Spinner sp2) {
            this.et = editText;
            this.foodname = foodname;
            this.button = button;
            this.quantity = quantity;
            this.sp1 = sp1;
            this.sp2 = sp2;
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

                button.setEnabled(foodname.length() > 0 &&
                        isValidDecimal(et.getText().toString().replace("$", "")) &&
                        isInteger(quantity.getText().toString()) &&
                        sp1.getSelectedItem() != null &&
                        sp2.getSelectedItem() != null &&
                        edit_foodid.length() > 0);

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

    private boolean isValidDecimal(String numberStr) {
        return numberStr.matches("^\\d+$$") || numberStr.matches("\\d+\\.\\d+$");
    }

    private boolean isInteger(String numberStr) {
        return numberStr.matches("\\d+");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults[0] ==0){
            Toast.makeText(getContext(), "Good", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "權限勒？", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}