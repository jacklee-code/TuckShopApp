package com.jacklee.tuckshopparent;

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
import com.jacklee.tuckshopparent.databinding.FragmentBanBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class BanFragment extends Fragment {

    private FragmentBanBinding binding;

    private List<Food> foodList;
    private ListView listView;
    private BanAdapter banAdapter;
    private Handler mHandler;

    private int selectedIndex = 0;

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
                            listView = binding.banListview;
                            listView.setItemsCanFocus(true);
                            banAdapter = new BanAdapter(getActivity(), foodList, mHandler);
                            listView.setAdapter(banAdapter);

                            binding.banProgressBar.setVisibility(View.INVISIBLE);
                            binding.banLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.GetFoodListFailed:
                        {
                            Toast.makeText(getContext(), "Failed to get food list, please try again.", Toast.LENGTH_SHORT);
                            binding.banProgressBar.setVisibility(View.INVISIBLE);
                            binding.banLoading.setVisibility(View.INVISIBLE);
                        }
                        break;


                        case HandleCode.ProfileSuccess:
                        {
                            GlobalVariables.profiles = Arrays.asList(new Gson().fromJson((String) msg.obj, StudentProfile[].class));

                            binding.banNolink.setVisibility(GlobalVariables.profiles.size() == 0 ? View.VISIBLE : View.INVISIBLE);

                            String[] namelist = new String[GlobalVariables.profiles.size()];
                            for (int i = 0; i< GlobalVariables.profiles.size(); i++) {
                                namelist[i] = GlobalVariables.profiles.get(i).Username;
                            }

                            ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, namelist);
                            adp.setDropDownViewResource(R.layout.spinner_item);
                            binding.banStudents.setAdapter(adp);


                            binding.banStudents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                                    selectedIndex = position;
                                    getFoodList(GlobalVariables.profiles.get(selectedIndex).UserId);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {

                                }
                            });


                            if (GlobalVariables.profiles.size() > 0)
                                if (GlobalVariables.profiles.size() > selectedIndex)
                                    binding.banStudents.setSelection(selectedIndex);
                                else {
                                    selectedIndex = 0;
                                    binding.banStudents.setSelection(0);
                                }
                            else {
                                binding.banProgressBar.setVisibility(View.INVISIBLE);
                                binding.banLoading.setVisibility(View.INVISIBLE);
                            }


                        }
                        break;

                        case HandleCode.ProfileFailed:
                        {
                            Toast.makeText(getContext(), "There was an error fetching the data, please try again.", Toast.LENGTH_SHORT).show();
                            binding.banNolink.setVisibility(View.VISIBLE);
                            binding.banProgressBar.setVisibility(View.INVISIBLE);
                            binding.banLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.Error_Msg:
                        {
                            String response=((Exception)msg.obj).toString();
                            Toast.makeText(getContext(), "An error occurred: " + response, Toast.LENGTH_SHORT).show();
                            Log.e("myerror", response);
                        }
                        break;

                        case HandleCode.DoBan:
                        {
                            doBanUnban(true, (String) msg.obj);
                        }
                        break;

                        case HandleCode.DoUnban:
                        {
                            doBanUnban(false, (String) msg.obj);
                        }
                        break;

                        case HandleCode.BanUnbanSuccess:
                        {
                            getStudentList();
                        }
                        break;

                        case HandleCode.BanUnbanFailed:
                        {
                            Toast.makeText(getContext(), "Ban / Unban Failed. Please try again.", Toast.LENGTH_SHORT);
                            binding.banProgressBar.setVisibility(View.INVISIBLE);
                            binding.banLoading.setVisibility(View.INVISIBLE);
                        }
                        break;


                        default:
                            break;
                    }
                } catch (Exception e) {

                }


            };
        };

        getStudentList();

        return root;
    }

    private void doBanUnban(boolean isBan, String foodid) {
        binding.banProgressBar.setVisibility(View.VISIBLE);
        binding.banLoading.setVisibility(View.VISIBLE);

        Map<String, String> hm = new HashMap<>();
        hm.put("username", GlobalVariables.account.Username);
        hm.put("password", GlobalVariables.account.getPassword());
        hm.put("targetid", GlobalVariables.profiles.get(selectedIndex).UserId);
        hm.put("foodid", foodid);

        HttpUtil.sendHTTPRequest(GlobalVariables.hostname + "/" + (isBan ? "" : "un") + "ban.php", hm, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.BanUnbanSuccess;
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
                msg.what = HandleCode.BanUnbanFailed;
                mHandler.sendMessage(msg);
            }

            @Override
            public void OnBadRequest() {

            }
        });
    }

    private void getStudentList() {
        binding.banProgressBar.setVisibility(View.VISIBLE);
        binding.banLoading.setVisibility(View.VISIBLE);
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

            @Override
            public void OnBadRequest() {

            }
        });
    }

    private void getFoodList(String studentid) {
        binding.banProgressBar.setVisibility(View.VISIBLE);
        binding.banLoading.setVisibility(View.VISIBLE);

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