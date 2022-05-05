package com.jacklee.tuckshopparent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    int selectedIndex = 0;

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

                            binding.recordsNorecord.setVisibility(records.size() == 0 ? View.VISIBLE : View.INVISIBLE);

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

                        case HandleCode.ProfileSuccess:
                        {
                            GlobalVariables.profiles = Arrays.asList(new Gson().fromJson((String) msg.obj, StudentProfile[].class));

                            binding.recordsNolink.setVisibility(GlobalVariables.profiles.size() == 0 ? View.VISIBLE : View.INVISIBLE);

                            String[] namelist = new String[GlobalVariables.profiles.size()];
                            for (int i = 0; i< GlobalVariables.profiles.size(); i++) {
                                namelist[i] = GlobalVariables.profiles.get(i).Username;
                            }

                            ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, namelist);
                            adp.setDropDownViewResource(R.layout.spinner_item);
                            binding.recordsStudents.setAdapter(adp);


                            binding.recordsStudents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                                    selectedIndex = position;
                                    getBuyRecords(GlobalVariables.profiles.get(selectedIndex).UserId);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {

                                }
                            });


                            if (GlobalVariables.profiles.size() > 0)
                                if (GlobalVariables.profiles.size() > selectedIndex)
                                    binding.recordsStudents.setSelection(selectedIndex);
                                else {
                                    selectedIndex = 0;
                                    binding.recordsStudents.setSelection(0);
                                }
                            else {
                                binding.recordsProgressBar.setVisibility(View.INVISIBLE);
                                binding.recordsLoading.setVisibility(View.INVISIBLE);
                            }


                        }
                        break;

                        case HandleCode.ProfileFailed:
                        case HandleCode.GetBuyRecordsFailed: {
                            fetchDataErrorToast();
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

        getStudentList();

        return root;
    }

    private void fetchDataErrorToast() {
        Toast.makeText(getContext(), "There was an error fetching the data, please try again.", Toast.LENGTH_SHORT).show();
        binding.recordsNolink.setVisibility(View.VISIBLE);
        binding.recordsProgressBar.setVisibility(View.INVISIBLE);
        binding.recordsLoading.setVisibility(View.INVISIBLE);
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


    private void getBuyRecords(String studentid) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        binding.recordsLoading.setVisibility(View.VISIBLE);
        binding.recordsProgressBar.setVisibility(View.VISIBLE);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("username", GlobalVariables.account.Username);
        hashMap.put("password", GlobalVariables.account.getPassword());
        hashMap.put("targetid", studentid);

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