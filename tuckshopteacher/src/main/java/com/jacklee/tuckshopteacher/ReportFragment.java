package com.jacklee.tuckshopteacher;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jacklee.tuckshopteacher.databinding.FragmentReportBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReportFragment extends Fragment {

    private FragmentReportBinding binding;
    private ListView listView;
    private Handler mHandler;
    private ReportAdapter reportAdapter;

    int selectedIndex = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mHandler = new Handler(){
            public void handleMessage(Message msg) {

                try {
                    switch (msg.what) {
                        case HandleCode.ReportSuccess:
                        {
                            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                            List<ProductDetail> report = Arrays.asList(gson.fromJson((String)msg.obj, ProductDetail[].class));

                            binding.reportNoproduct.setVisibility(report.size() == 0 ? View.VISIBLE : View.INVISIBLE);

                            // Create ListView
                            listView = binding.reportListview;
                            listView.setItemsCanFocus(true);
                            reportAdapter = new ReportAdapter(getActivity(), report);
                            listView.setAdapter(reportAdapter);

                            // Calculate total amount
                            double sum = 0.00;
                            for (ProductDetail product : report) {
                                sum += product.Income;
                            }

                            binding.reportTotalincome.setText("$ " + String.format("%.2f", sum));
                        }

                        binding.reportProgressBar.setVisibility(View.INVISIBLE);
                        binding.reportLoading.setVisibility(View.INVISIBLE);

                        break;


                        case HandleCode.GetFoodListFailed:
                        case HandleCode.ReportFailed: {
                            fetchDataErrorToast();
                        }
                        break;

                        case HandleCode.GetFoodListSuccess:
                        {
                            Food[] foods = new Gson().fromJson((String) msg.obj, Food[].class);

                            binding.reportNofood.setVisibility(foods.length == 0 ? View.VISIBLE : View.INVISIBLE);

                            String[] namelist = new String[foods.length];
                            for (int i = 0; i< foods.length; i++) {
                                namelist[i] = foods[i].FoodName;
                            }

                            ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, namelist);
                            adp.setDropDownViewResource(R.layout.spinner_item);
                            binding.reportProducts.setAdapter(adp);


                            binding.reportProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                                    selectedIndex = position;
                                    getProductRecords(String.valueOf(foods[selectedIndex].FoodId));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {

                                }
                            });


                            if (foods.length > 0)
                                if (foods.length > selectedIndex)
                                    binding.reportProducts.setSelection(selectedIndex);
                                else {
                                    selectedIndex = 0;
                                    binding.reportProducts.setSelection(0);
                                }
                            else {
                                binding.reportProgressBar.setVisibility(View.INVISIBLE);
                                binding.reportLoading.setVisibility(View.INVISIBLE);
                            }


                        }
                        break;

                        case HandleCode.Error_Msg:
                        {
                            String response=((Exception)msg.obj).toString();
                            Toast.makeText(getContext(), "An error occurred: " + response, Toast.LENGTH_SHORT).show();
                            Log.e("myerror", response);
                        }
                        break;

                        default:
                            break;
                    }
                } catch (Exception e) {

                }
            };
        };

        getFoodList();

        return root;
    }

    private void fetchDataErrorToast() {
        Toast.makeText(getContext(), "There was an error fetching the data, please try again.", Toast.LENGTH_SHORT).show();
        binding.reportNoproduct.setVisibility(View.VISIBLE);
        binding.reportProgressBar.setVisibility(View.INVISIBLE);
        binding.reportLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void getFoodList() {
        binding.reportProgressBar.setVisibility(View.VISIBLE);
        binding.reportLoading.setVisibility(View.VISIBLE);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("username", GlobalVariables.account.Username);
        hashMap.put("password", GlobalVariables.account.getPassword());

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


    private void getProductRecords(String foodid) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        binding.reportProgressBar.setVisibility(View.VISIBLE);
        binding.reportLoading.setVisibility(View.VISIBLE);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("username", GlobalVariables.account.Username);
        hashMap.put("password", GlobalVariables.account.getPassword());
        hashMap.put("foodid", foodid);

        HttpUtil.sendHTTPRequest(GlobalVariables.hostname + "/getProductReport.php", hashMap, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.ReportSuccess;
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
                msg.what = HandleCode.ReportFailed;
                mHandler.sendMessage(msg);
            }

            @Override
            public void OnBadRequest() {

            }
        });
    }
}