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
                            getFoodList("");
                        }
                        break;

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

    private void getFoodList(String studentid) {
        binding.foodProgressBar.setVisibility(View.VISIBLE);
        binding.foodLoading.setVisibility(View.VISIBLE);

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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}