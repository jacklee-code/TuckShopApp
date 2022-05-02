package com.jacklee.tuckshopparent;

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
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.jacklee.tuckshopparent.databinding.FragmentBanBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BanFragment extends Fragment {

    private FragmentBanBinding binding;

    private List<Food> foodList;
    private ListView listView;
    private BanAdapter banAdapter;
    private Handler mHandler;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mHandler = new Handler(){
            public void handleMessage(Message msg) {
                try {
                    switch (msg.what) {
                        case HandleCode.GetFoodListSuccess:
                        {
                            foodList = Arrays.asList(new Gson().fromJson((String) msg.obj, Food[].class));
                            // Create ListView
                            listView = binding.purchaseListview;
                            listView.setItemsCanFocus(true);
                            banAdapter = new BanAdapter(getActivity(), foodList, binding.purchaseTotalamount);
                            listView.setAdapter(banAdapter);

                            binding.purchaseProgressBar.setVisibility(View.INVISIBLE);
                            binding.purchaseLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.GetFoodListFailed:
                        {
                            Toast.makeText(getContext(), "Failed to get food list, please try again.", Toast.LENGTH_SHORT);
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
                            GlobalVariables.profiles = Arrays.asList(new Gson().fromJson((String) msg.obj, StudentProfile[].class));
                            //binding.purchaseBalance.setText("$ " + GlobalVariables.profile.Balance);
                            binding.purchaseProgressBar.setVisibility(View.INVISIBLE);
                            binding.purchaseLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.ProfileFailed:
                        {
                            Toast.makeText(getContext(), "There was an error fetching the data, please try again.", Toast.LENGTH_SHORT).show();
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

    private void RefreshFoodList() {
        binding.purchaseProgressBar.setVisibility(View.VISIBLE);
        binding.purchaseLoading.setVisibility(View.VISIBLE);

        HashMap<String, String> hashMap = new HashMap<>();
        //TODO: Change From Single ID to Multi ID in PHP
        //hashMap.put("UserId", GlobalVariables.profile.UserId);

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