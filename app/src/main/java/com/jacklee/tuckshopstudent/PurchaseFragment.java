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
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.jacklee.tuckshopstudent.databinding.FragmentPurchaseBinding;

import java.util.Arrays;
import java.util.List;

public class PurchaseFragment extends Fragment {

    private FragmentPurchaseBinding binding;

    private List<Food> foodList;
    private ListView listView;
    private PurchaseAdapter purchaseAdapter;
    private Handler mHandler;

    final String hostname = "https://iit3008-11379925.000webhostapp.com";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPurchaseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case HandleCode.GetFoodListSuccess:
                    {
                        foodList = Arrays.asList(new Gson().fromJson((String) msg.obj, Food[].class));
                        // Create ListView
                        listView = binding.purchaseListview;
                        listView.setItemsCanFocus(true);
                        purchaseAdapter = new PurchaseAdapter(getActivity(), foodList);
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


            };
        };

        //Add Listener to [Purchase] Button
        OnPurchaseClicked();

        // Get food list  (HTTP method)
        RefreshFoodList();

        return root;
    }

    private void OnPurchaseClicked() {
        binding.purchasePurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Sum is " + purchaseAdapter.getTotalAmount(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void RefreshFoodList() {
        binding.purchaseProgressBar.setVisibility(View.VISIBLE);
        binding.purchaseLoading.setVisibility(View.VISIBLE);

        HttpUtil.sendHTTPRequest(hostname + "/getFoodList.php", "", new HttpCallbackListener() {
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