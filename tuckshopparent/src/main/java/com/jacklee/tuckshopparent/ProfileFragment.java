package com.jacklee.tuckshopparent;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.jacklee.tuckshopparent.databinding.FragmentProfileBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    private Handler mHandler;

    private ListView listView;
    private ProfileAdapter adapter;
    private ProgressBar link_progressBar;

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

                            binding.profileNolinkage.setVisibility(GlobalVariables.profiles.size() == 0 ? View.VISIBLE : View.INVISIBLE);


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
                            Toast.makeText(getContext(), "Top-up failed", Toast.LENGTH_SHORT).show();
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

                        case HandleCode.LinkFailed:
                        {
                            link_progressBar.setVisibility(View.INVISIBLE);
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setTitle("Link Failed");
                            alert.setMessage("\nStudent username/password may be incorrect. \nPlease try again.\n");
                            alert.setPositiveButton("OK", null);
                            alert.show();
                        }
                        break;

                        case HandleCode.LinkRepeated:
                        {
                            link_progressBar.setVisibility(View.INVISIBLE);
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setTitle("Link Failed");
                            alert.setMessage("The student has been linked.");
                            alert.setPositiveButton("OK", null);
                            alert.show();
                        }
                        break;

                        case HandleCode.RegisterSuccess:
                        {
                            Dialog dialog = (Dialog) msg.obj;
                            dialog.dismiss();
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setTitle("Congratulations");
                            alert.setMessage("Account successfully linked");
                            alert.setPositiveButton("OK", null);
                            alert.show();

                            refreshUserProfile();
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

        binding.profileLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLinkLoginDialog();
            }
        });

        refreshUserProfile();


        return root;
    }

    private void showLinkLoginDialog() {
        View link_view;

        EditText link_username, link_password;
        Button link_link;

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        link_view = inflater.inflate(R.layout.dialog_link,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Create new link");
        builder.setCancelable(false);
        builder.setView(link_view);
        Dialog dialog = builder.create();
        dialog.show();

        link_username = (EditText) link_view.findViewById(R.id.dialog_username);
        link_password = (EditText) link_view.findViewById(R.id.dialog_password);
        link_link = (Button) link_view.findViewById(R.id.dialog_link);
        link_progressBar = (ProgressBar) link_view.findViewById(R.id.dialog_progressBar);

        TextWatcher link_TextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean rusernameok, rpwok = false;

                rusernameok = isUserNameValid(link_username.getText().toString());
                rpwok = isPasswordValid(link_password.getText().toString());

                if (!rusernameok)
                    link_username.setError(getString(R.string.invalid_username));

                if (!rpwok)
                    link_password.setError(getString(R.string.invalid_password));

                link_link.setEnabled(rusernameok && rpwok);
            }
        };

        View.OnClickListener link_clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.dialog_cancel)
                    dialog.dismiss();

                else if (view.getId() == R.id.dialog_link) {
                    link_progressBar.setVisibility(View.VISIBLE);

                    Account ac = new Account();
                    ac.Username = link_username.getText().toString();
                    ac.setPassword(link_password.getText().toString());

                    String url = GlobalVariables.hostname + "/link.php";
                    Map<String, String> hm = new HashMap<>();
                    hm.put("username", GlobalVariables.account.Username);
                    hm.put("password", GlobalVariables.account.getPassword());
                    hm.put("linkusername", ac.Username);
                    hm.put("linkpassword", ac.getPassword());

                    HttpUtil.sendHTTPRequest(url, hm, new HttpCallbackListener() {

                        @Override
                        public void onFinish(String response) {
                            Message msg=mHandler.obtainMessage();
                            msg.what = HandleCode.LinkSuccess;
                            msg.obj = dialog;
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
                            msg.what = HandleCode.LinkFailed;
                            msg.obj = dialog;
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void OnBadRequest() {
                            Message msg=mHandler.obtainMessage();
                            msg.what = HandleCode.LinkRepeated;
                            msg.obj = dialog;
                            mHandler.sendMessage(msg);
                        }
                    });
                }
            }
        };

        link_username.addTextChangedListener(link_TextWatcher);
        link_password.addTextChangedListener(link_TextWatcher);
        link_password.addTextChangedListener(link_TextWatcher);
        link_link.setOnClickListener(link_clickListener);
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

            @Override
            public void OnBadRequest() {

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

            @Override
            public void OnBadRequest() {

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

    private boolean isUserNameValid(String username) {
        String regex = "^[a-zA-Z0-9]+$";

        if (username == null) {
            return false;
        }

        return Pattern.compile(regex).matcher(username).matches();
    }


    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}