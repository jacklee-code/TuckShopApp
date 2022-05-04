package com.jacklee.tuckshopteacher;

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

    void OnForbidden();

    void OnBadRequest();
}
