package com.jacklee.tuckshopstudent;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jacklee.tuckshopstudent.R;


import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {


    private boolean usernameOK, passwordOK = false;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar;

    final String hostname = "https://iit3008-11379925.000webhostapp.com";

    private Handler mHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar =findViewById(R.id.loading);

        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 200:
                        {
                            String response=(String) msg.obj;
                            showLoginSuccess(response);
                        }
                        break;

                    case 400:
                        {
                            showLoginFailed(getString(R.string.fail_username_or_password));
                        }

                        break;
                    case 999:
                        {
                            String response=((Exception)msg.obj).toString();
                            showLoginFailed(getString(R.string.fail_network_problem) + response);
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
                String url = hostname + "/login.php";
                HttpUtil.sendHTTPRequest(url, null ,new HttpCallbackListener() {

                    @Override
                    public void onFinish(String response) {
                        Message msg=mHandler.obtainMessage();
                        msg.what = 200;
                        msg.obj = response;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Exception e) {
                        Message msg=mHandler.obtainMessage();
                        msg.what = 999;
                        msg.obj = e;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void OnForbiden() {
                        Message msg=mHandler.obtainMessage();
                        msg.what = 400;
                        mHandler.sendMessage(msg);
                    }
                });
            }
        });
    }


    private void showLoginSuccess(String username) {
        String welcome = getString(R.string.welcome) + username;
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(String fail_msg) {
        Toast.makeText(getApplicationContext(), getString(R.string.login_failed) + fail_msg, Toast.LENGTH_SHORT).show();
    }


    // A placeholder username validation check
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