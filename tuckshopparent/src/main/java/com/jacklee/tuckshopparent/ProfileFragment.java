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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.jacklee.tuckshopparent.databinding.FragmentProfileBinding;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private Handler mHandler;

    final String hostname = "https://iit3008-11379925.000webhostapp.com";

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
                            Toast.makeText(getContext(), "There was an error fetching the data, please try again.", Toast.LENGTH_LONG).show();
                            binding.profileProgressBar.setVisibility(View.INVISIBLE);
                            binding.profileLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.ProfileSuccess:
                        {
                            GlobalVariables.profiles = new Gson().fromJson((String) msg.obj, StudentProfile[].class);
                            //TODO: Create Listview


                            binding.profileProgressBar.setVisibility(View.INVISIBLE);
                            binding.profileLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.TopupSuccess:
                        {
                            Dialog dialog = (Dialog) msg.obj;
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Top-up successful", Toast.LENGTH_LONG).show();
                            refreshUserProfile();
                        }
                        break;

                        case HandleCode.TopupFailed:
                        {
                            Toast.makeText(getContext(), "Top-up failed", Toast.LENGTH_LONG);
                        }
                        break;

                        case HandleCode.Test:
                            Toast.makeText(getContext(), "Test Success", Toast.LENGTH_SHORT).show();
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
                } catch(Exception e) {

                }


            };
        };

        refreshUserProfile();


        return root;
    }

    private void refreshUserProfile() {
        binding.profileProgressBar.setVisibility(View.VISIBLE);
        binding.profileLoading.setVisibility(View.VISIBLE);
        Map<String, String> hm = new HashMap<>();
        hm.put("username", GlobalVariables.account.Username);
        hm.put("password", GlobalVariables.account.getPassword());

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