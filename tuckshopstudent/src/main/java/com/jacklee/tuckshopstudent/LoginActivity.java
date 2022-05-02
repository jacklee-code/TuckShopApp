package com.jacklee.tuckshopstudent;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jacklee.tuckshopstudent.R;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {


    private boolean usernameOK, passwordOK = false;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar loadingProgressBar, reg_progressbar;

    private View register_view;

    private EditText reg_username, reg_password, reg_password2, reg_fullname;
    private Button reg_register, reg_cancel;

    Account account;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar =findViewById(R.id.loading);
        registerButton = findViewById(R.id.register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowRegisterDialog();
            }
        });

        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case HandleCode.LoginSuccess:
                        {
                                String response=(String) msg.obj;
                                Account account = new Gson().fromJson(response, Account.class);
                                showLoginSuccess(account.Fullname);

                                // Open New Activity
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                GlobalVariables.account = account;
                                startActivity(i);
                        }
                        break;

                    case HandleCode.LoginFailed:
                        {
                            showLoginFailed(getString(R.string.fail_username_or_password));
                        }

                        break;
                    case HandleCode.Error_Msg:
                        {
                            String response=((Exception)msg.obj).toString();
                            showLoginFailed(getString(R.string.fail_network_problem) + response);
                            Log.e("error", response);
                        }
                        break;

                    case HandleCode.RegisterFailed:
                        {
                            reg_progressbar.setVisibility(View.INVISIBLE);
                            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                            alert.setTitle("Registration Failed");
                            alert.setMessage("The username you have chosen may already be in use.");
                            alert.setPositiveButton("OK", null);
                            alert.show();
                        }
                        break;

                    case HandleCode.RegisterSuccess:
                        {
                            Dialog dialog = (Dialog) msg.obj;
                            reg_progressbar.setVisibility(View.INVISIBLE);
                            dialog.dismiss();
                            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                            alert.setTitle("Registration Success");
                            alert.setMessage("Please go back to the main screen to login.");
                            alert.setPositiveButton("OK", null);
                            alert.show();
                        }

                        break;

                    default:
                        break;
                }
                loadingProgressBar.setVisibility(View.INVISIBLE);
            };
        };


        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isUserNameValid(s.toString())) {
                    usernameEditText.setError(getString(R.string.invalid_username));
                    usernameOK =false;
                } else {
                    usernameOK = true;
                }
                loginButton.setEnabled(usernameOK && passwordOK);
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isPasswordValid(s.toString())) {
                    passwordEditText.setError(getString(R.string.invalid_password));
                    passwordOK = false;
                } else
                    passwordOK = true;
                loginButton.setEnabled(usernameOK && passwordOK);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                Login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });
    }

    private void ShowRegisterDialog() {
        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        register_view = inflater.inflate(R.layout.activity_register,null);

        //-----------產生登入視窗--------
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign Up");
        builder.setCancelable(false);
        builder.setView(register_view);
        Dialog dialog = builder.create();
        dialog.show();

        reg_username = (EditText) register_view.findViewById(R.id.register_username);
        reg_fullname = (EditText) register_view.findViewById(R.id.register_fullname);
        reg_password = (EditText) register_view.findViewById(R.id.register_password);
        reg_password2 = (EditText) register_view.findViewById(R.id.register_password2);
        reg_register = (Button) register_view.findViewById(R.id.register_register);
        reg_cancel = (Button) register_view.findViewById(R.id.register_cancel);
        reg_progressbar = (ProgressBar) register_view.findViewById(R.id.reg_progressbar);

        TextWatcher reg_TextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean rusernameok, rpwok, rpw2ok, rfullnameok = false;

                rusernameok = isUserNameValid(reg_username.getText().toString());
                rpwok = isPasswordValid(reg_password.getText().toString());
                rpw2ok = reg_password.getText().toString().equals(reg_password2.getText().toString());
                rfullnameok = isFullnameValid(reg_fullname.getText().toString());

                if (!rusernameok)
                    reg_username.setError(getString(R.string.invalid_username));

                if (!rpwok)
                    reg_password.setError(getString(R.string.invalid_password));

                if (!rpw2ok)
                    reg_password2.setError("Password does not match, please re-enter");

                if (!rfullnameok)
                    reg_fullname.setError("Not a valid name");

                reg_register.setEnabled(rusernameok && rpwok && rpw2ok && rfullnameok);
            }
        };

        View.OnClickListener reg_clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.register_cancel)
                    dialog.dismiss();

                else if (view.getId() == R.id.register_register) {
                    reg_progressbar.setVisibility(View.VISIBLE);

                    Account ac = new Account();
                    ac.Username = reg_username.getText().toString();
                    ac.setPassword(reg_password.getText().toString());
                    ac.Fullname = reg_fullname.getText().toString();

                    String url = GlobalVariables.hostname + "/register.php";
                    Map<String, String> hm = new HashMap<>();
                    hm.put("username", ac.Username);
                    hm.put("password", ac.getPassword());
                    hm.put("fullname", ac.Fullname);

                    //TODO: change in every version
                    hm.put("typeid", "1");

                    HttpUtil.sendHTTPRequest(url, hm, new HttpCallbackListener() {

                        @Override
                        public void onFinish(String response) {
                            Message msg=mHandler.obtainMessage();
                            msg.what = HandleCode.RegisterSuccess;
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
                            msg.what = HandleCode.RegisterFailed;
                            msg.obj = dialog;
                            mHandler.sendMessage(msg);
                        }
                    });
                }
            }
        };

        reg_username.addTextChangedListener(reg_TextWatcher);
        reg_password.addTextChangedListener(reg_TextWatcher);
        reg_password2.addTextChangedListener(reg_TextWatcher);
        reg_fullname.addTextChangedListener(reg_TextWatcher);
        reg_password.addTextChangedListener(reg_TextWatcher);
        reg_register.setOnClickListener(reg_clickListener);
        reg_cancel.setOnClickListener(reg_clickListener);
    }

    private void Login(String username, String password) {
        String url = GlobalVariables.hostname + "/login.php";
        Map<String, String> hm = new HashMap<>();
        Account ac = new Account();
        ac.Username = username;
        ac.setPassword(password);
        hm.put("username", ac.Username);
        hm.put("password", ac.getPassword());

        HttpUtil.sendHTTPRequest(url, hm, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.LoginSuccess;
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
                msg.what = HandleCode.LoginFailed;
                mHandler.sendMessage(msg);
            }
        });
    }


    private void showLoginSuccess(String username) {
        String welcome = getString(R.string.welcome) + username;
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(String fail_msg) {
        Toast.makeText(getApplicationContext(), getString(R.string.login_failed) + fail_msg, Toast.LENGTH_SHORT).show();
    }


    private boolean isUserNameValid(String username) {
        String regex = "^[a-zA-Z0-9]+$";

        if (username == null) {
            return false;
        }

        return Pattern.compile(regex).matcher(username).matches();
    }

    private boolean isFullnameValid(String fullname) {
        String regex = "^[a-zA-Z ]+$";

        if (fullname == null) {
            return false;
        }

        return Pattern.compile(regex).matcher(fullname).matches() && fullname.length() > 3;
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}