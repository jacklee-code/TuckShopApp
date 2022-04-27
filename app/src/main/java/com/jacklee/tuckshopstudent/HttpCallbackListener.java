package com.jacklee.tuckshopstudent;

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

    void OnForbiden();
}
