package com.jacklee.tuckshopparent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jacklee.tuckshopparent.databinding.FragmentRecordsBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecordsFragment extends Fragment {

    private FragmentRecordsBinding binding;
    private ListView listView;
    private Handler mHandler;
    private RecordsAdapter recordsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecordsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mHandler = new Handler(){
            public void handleMessage(Message msg) {

                try {
                    switch (msg.what) {
                        case HandleCode.GetBuyRecordsSuccess:
                        {
                            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                            List<BuyRecord> records = Arrays.asList(gson.fromJson((String)msg.obj, BuyRecord[].class));

                            // Create ListView
                            listView = binding.recordsListview;
                            listView.setItemsCanFocus(true);
                            recordsAdapter = new RecordsAdapter(getActivity(), records);
                            listView.setAdapter(recordsAdapter);

                            // Calculate total amount
                            double sum = 0.00;
                            for (BuyRecord record : records) {
                                sum += record.getTotalAmount();
                            }

                            binding.recordsTotalconsumption.setText("$ " + String.format("%.2f", sum));
                        }
                        break;

                        case HandleCode.GetBuyRecordsFailed:
                        {

                        }
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

                    binding.recordsLoading.setVisibility(View.INVISIBLE);
                    binding.recordsProgressBar.setVisibility(View.INVISIBLE);
                } catch (Exception e) {

                }
            };
        };
        getBuyRecords();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void getStudentList() {
        binding.recordsProgressBar.setVisibility(View.VISIBLE);
        binding.recordsProgressBar.setVisibility(View.VISIBLE);
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


    private void getBuyRecords() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        binding.recordsLoading.setVisibility(View.VISIBLE);
        binding.recordsProgressBar.setVisibility(View.VISIBLE);

        HashMap<String, String> hashMap = new HashMap<>();
        //TODO: Change from single id to multi id
        //hashMap.put("UserId", GlobalVariables.profile.UserId);

        HttpUtil.sendHTTPRequest(GlobalVariables.hostname + "/getBuyRecords.php", hashMap, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.GetBuyRecordsSuccess;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {
                Log.e("myerror", e.toString());
            }

            @Override
            public void OnForbidden() {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.GetBuyRecordsFailed;
                mHandler.sendMessage(msg);
            }

            @Override
            public void OnBadRequest() {

            }
        });
    }
}