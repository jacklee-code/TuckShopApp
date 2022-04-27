package com.jacklee.tuckshopstudent;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtil {
    public static void sendHTTPRequest(final String address, final Map<String, String> params, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(address).openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);


                    connection.connect();

                    DataOutputStream dos=new DataOutputStream(connection.getOutputStream());

                    String urlParams = "";

                    if (params != null) {
                        for(Map.Entry<String, String> pair : params.entrySet()) {
                            urlParams += pair.getKey() + "=" + URLEncoder.encode(pair.getValue(),"UTF-8") + "&";
                        }
                    }

                    if (urlParams.length() > 0)
                        urlParams = urlParams.substring(0, urlParams.length() - 1);


                    Log.i("url parameter", urlParams);

                    dos.writeBytes(urlParams);
                    dos.flush();
                    dos.close();

                    // Get Response
                    int resultCode = connection.getResponseCode();
                    if (HttpURLConnection.HTTP_OK == resultCode) { // 200 OK
                        StringBuffer sb = new StringBuffer();
                        String readLine = new String();
                        BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        while ((readLine = responseReader.readLine()) != null) {
                            sb.append(readLine).append("\n");
                        }
                        responseReader.close();
                        listener.onFinish(sb.toString());

                    } else if (HttpURLConnection.HTTP_FORBIDDEN == resultCode) { // 403 Forbiden
                        listener.OnForbiden();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError(e);
                    }

                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

            }
        }).start();
    }
}
