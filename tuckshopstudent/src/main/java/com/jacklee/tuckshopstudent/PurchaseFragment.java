package com.jacklee.tuckshopstudent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jacklee.tuckshopstudent.databinding.FragmentPurchaseBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseFragment extends Fragment {

    private FragmentPurchaseBinding binding;

    private List<Food> foodList;
    private ListView listView;
    private PurchaseAdapter purchaseAdapter;
    private Handler mHandler;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPurchaseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                try {
                    switch (msg.what) {
                        case HandleCode.GetFoodListSuccess:
                        {
                            foodList = Arrays.asList(new Gson().fromJson((String) msg.obj, Food[].class));
                            // Create ListView
                            listView = binding.purchaseListview;
                            listView.setItemsCanFocus(true);
                            purchaseAdapter = new PurchaseAdapter(getActivity(), foodList, binding.purchaseTotalamount);
                            listView.setAdapter(purchaseAdapter);

                            binding.purchaseProgressBar.setVisibility(View.INVISIBLE);
                            binding.purchaseLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.GetFoodListFailed:
                        {
                            Toast.makeText(getContext(), "Failed to get food list, please try again.", Toast.LENGTH_LONG);
                            binding.purchaseProgressBar.setVisibility(View.INVISIBLE);
                            binding.purchaseLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.PurchaseSuccess:
                        {
                            Toast.makeText(getContext(), "The purchase was successful.", Toast.LENGTH_SHORT).show();
                            RefreshUserProfile();
                            RefreshFoodList();
                        }
                        break;


                        case HandleCode.ProfileSuccess:
                        {
                            GlobalVariables.profile = new Gson().fromJson((String) msg.obj, StudentProfile.class);
                            binding.purchaseBalance.setText("$ " + GlobalVariables.profile.Balance);
                            binding.purchaseProgressBar.setVisibility(View.INVISIBLE);
                            binding.purchaseLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.ProfileFailed:
                        {
                            Toast.makeText(getContext(), "There was an error fetching the data, please try again.", Toast.LENGTH_LONG).show();
                            binding.purchaseProgressBar.setVisibility(View.INVISIBLE);
                            binding.purchaseLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.PurchaseFailed:
                        {
                            Toast.makeText(getContext(), "The purchase was Failed.", Toast.LENGTH_SHORT).show();
                            binding.purchaseProgressBar.setVisibility(View.INVISIBLE);
                            binding.purchaseLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.Error_Msg:
                        {
                            String response=((Exception)msg.obj).toString();
                            Toast.makeText(getContext(), "An error occurred: " + response, Toast.LENGTH_LONG).show();
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

        //Add Listener to [Purchase] Button
        OnPurchaseClicked();

        // Get food list  (HTTP method)
        RefreshFoodList();

        // Get Balance
        RefreshUserProfile();

        return root;
    }

    private void RefreshUserProfile() {
        binding.purchaseProgressBar.setVisibility(View.VISIBLE);
        binding.purchaseLoading.setVisibility(View.VISIBLE);
        Map<String, String> hm = new HashMap<>();
        hm.put("username", GlobalVariables.account.Username);
        hm.put("password", GlobalVariables.account.getPassword());

        HttpUtil.sendHTTPRequest(GlobalVariables.hostname + "/getStudentInfo.php", hm, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.ProfileSuccess;
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
                msg.what = HandleCode.ProfileFailed;
                mHandler.sendMessage(msg);
            }
        });
    }

    private void OnPurchaseClicked() {
        binding.purchasePurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.purchaseProgressBar.setVisibility(View.VISIBLE);
                binding.purchaseLoading.setVisibility(View.VISIBLE);

                String json = new Gson().toJson(purchaseAdapter.getShoppingCart(GlobalVariables.account));
                Log.e("purchase", json);
                HttpUtil.sendHTTPRequest(GlobalVariables.hostname + "/purchase.php", json, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Message msg=mHandler.obtainMessage();
                        msg.what = HandleCode.PurchaseSuccess;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("myerror", e.toString());
                    }

                    @Override
                    public void OnForbidden() {
                        Message msg=mHandler.obtainMessage();
                        msg.what = HandleCode.PurchaseFailed;
                        mHandler.sendMessage(msg);
                    }
                });
            }
        });
    }

    private void RefreshFoodList() {
        binding.purchaseProgressBar.setVisibility(View.VISIBLE);
        binding.purchaseLoading.setVisibility(View.VISIBLE);

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
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}