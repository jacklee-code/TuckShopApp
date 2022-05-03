package com.jacklee.tuckshopstudent;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private Handler mHandler;

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
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
                            GlobalVariables.profile = new Gson().fromJson((String) msg.obj, StudentProfile.class);
                            binding.profileFullname.setText(GlobalVariables.profile.Fullname);
                            binding.profileUserid.setText(GlobalVariables.profile.UserId);
                            binding.profileUsername.setText(GlobalVariables.profile.Username);
                            binding.profileAccounttype.setText(GlobalVariables.profile.AccountType);
                            binding.profileBalance.setText("$ " + String.valueOf(GlobalVariables.profile.Balance));
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

        binding.profileTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Top-up");
                builder.setMessage("Top-up amount:");
                builder.setCancelable(false);

                final EditText input = new EditText(getActivity());

                input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(input);

                builder.setPositiveButton("Top-up", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String number = input.getText().toString();
                        if (isValidDecimal(number)) {
                            Map<String, String> topupparams = new HashMap<>();
                            topupparams.put("username", GlobalVariables.account.Username);
                            topupparams.put("password", GlobalVariables.account.getPassword());
                            topupparams.put("userId", GlobalVariables.profile.UserId);
                            topupparams.put("amount", number);

                            binding.profileLoading.setVisibility(View.VISIBLE);
                            binding.profileLoading.setVisibility(View.VISIBLE);

                            HttpUtil.sendHTTPRequest(GlobalVariables.hostname + "/topup.php", topupparams, new HttpCallbackListener() {
                                @Override
                                public void onFinish(String response) {
                                    Message msg=mHandler.obtainMessage();
                                    msg.what = HandleCode.TopupSuccess;
                                    msg.obj = dialog;
                                    mHandler.sendMessage(msg);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e("myerror", e.getMessage());
                                }

                                @Override
                                public void OnForbidden() {
                                    Message msg=mHandler.obtainMessage();
                                    msg.what = HandleCode.TopupFailed;
                                    mHandler.sendMessage(msg);
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Please input a valid value.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        });

        refreshUserProfile();


        return root;
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