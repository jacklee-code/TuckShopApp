package com.jacklee.tuckshopstudent;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.jacklee.tuckshopstudent.databinding.FragmentProfileBinding;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    Account account;
    StudentProfile profile;
    private Handler mHandler;

    final String hostname = "https://iit3008-11379925.000webhostapp.com";

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        account = (Account) getActivity().getIntent().getSerializableExtra("account");

        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case HandleCode.ProfileFailed:
                    {

                    }
                    break;

                    case HandleCode.ProfileSuccess:
                    {
                        binding.profileProgressBar.setVisibility(View.INVISIBLE);
                        binding.profileLoading.setVisibility(View.INVISIBLE);
                        profile = new Gson().fromJson((String) msg.obj, StudentProfile[].class)[0];
                        binding.profileFullname.setText(profile.Fullname);
                        binding.profileUserid.setText(profile.UserId);
                        binding.profileUsername.setText(profile.Username);
                        binding.profileAccounttype.setText(profile.AccountType);
                        binding.profileBalance.setText("$ " + String.valueOf(profile.Balance));
                    }
                    break;


                    default:
                        break;
                }
            };
        };

        Map<String, String> hm = new HashMap<>();
        hm.put("username", account.Username);
        hm.put("password", account.getPassword());

        //TODO: change in every version
        hm.put("typeid", "2");

        HttpUtil.sendHTTPRequest(hostname + "/getStudentInfo.php", hm, new HttpCallbackListener() {

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
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}