package com.jacklee.tuckshopparent;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.jacklee.tuckshopparent.databinding.FragmentProfileBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private Handler mHandler;

    private ListView listView;
    private ProfileAdapter adapter;

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        mHandler = new Handler(){
            public void handleMessage(Message msg) {
                try {
                    switch (msg.what) {
                        case HandleCode.ProfileFailed:
                        {
                            Toast.makeText(getContext(), "There was an error fetching the data, please try again.", Toast.LENGTH_SHORT).show();
                            binding.profileProgressBar.setVisibility(View.INVISIBLE);
                            binding.profileLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.ProfileSuccess:
                        {
                            GlobalVariables.profiles = Arrays.asList(new Gson().fromJson((String) msg.obj, StudentProfile[].class));
                            //TODO: Create Listview
                            listView = binding.profileListview;
                            listView.setItemsCanFocus(true);
                            adapter = new ProfileAdapter(getActivity(), GlobalVariables.profiles, GlobalVariables.account, mHandler);
                            listView.setAdapter(adapter);

                            binding.profileProgressBar.setVisibility(View.INVISIBLE);
                            binding.profileLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.DoTopUp:
                        {
                            TopUpDetail td = (TopUpDetail) msg.obj;
                            topUp(td.targetId, td.amount);
                        }
                        break;

                        case HandleCode.TopupSuccess:
                        {
                            Toast.makeText(getContext(), "Top-up successful", Toast.LENGTH_SHORT).show();
                            refreshUserProfile();
                        }
                        break;

                        case HandleCode.TopupFailed:
                        {
                            Toast.makeText(getContext(), "Top-up failed", Toast.LENGTH_SHORT);
                            binding.profileProgressBar.setVisibility(View.INVISIBLE);
                            binding.profileLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.Test:
                            Toast.makeText(getContext(), "Test Success", Toast.LENGTH_SHORT).show();
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
                } catch(Exception e) {
                    Log.e("myerror", e.toString());
                }


            };
        };

        refreshUserProfile();


        return root;
    }

    private void topUp(String targetId, String amount) {
        binding.profileProgressBar.setVisibility(View.VISIBLE);
        binding.profileLoading.setVisibility(View.VISIBLE);

        HashMap<String, String> params = new HashMap<>();
        params.put("username", GlobalVariables.account.Username);
        params.put("password", GlobalVariables.account.getPassword());
        params.put("userId", targetId);
        params.put("amount", amount);

        HttpUtil.sendHTTPRequest(GlobalVariables.hostname + "/topup.php", params, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                Message msg = mHandler.obtainMessage();
                msg.what = HandleCode.TopupSuccess;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {
                Message msg = mHandler.obtainMessage();
                msg.what = HandleCode.Error_Msg;
                msg.obj = e;
                mHandler.sendMessage(msg);
            }

            @Override
            public void OnForbidden() {
                Message msg = mHandler.obtainMessage();
                msg.what = HandleCode.TopupFailed;
                mHandler.sendMessage(msg);
            }
        });
    }

    private void refreshUserProfile() {
        binding.profileProgressBar.setVisibility(View.VISIBLE);
        binding.profileLoading.setVisibility(View.VISIBLE);
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

    public boolean isValidDecimal(String numberStr) {
        return numberStr.matches("^\\d+$$") || numberStr.matches("\\d+\\.\\d+$");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}