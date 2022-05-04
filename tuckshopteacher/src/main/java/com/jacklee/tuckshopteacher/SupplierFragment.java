package com.jacklee.tuckshopteacher;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.jacklee.tuckshopteacher.databinding.FragmentSupplierBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplierFragment extends Fragment {

    private Handler mHandler;

    private ListView listView;
    private SupplierAdapter adapter;
    private ProgressBar new_progressBar;

    private FragmentSupplierBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSupplierBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        mHandler = new Handler(){
            public void handleMessage(Message msg) {
                try {
                    switch (msg.what) {
                        case HandleCode.GetSupplierFailed:
                        {
                            Toast.makeText(getContext(), "There was an error fetching the data, please try again.", Toast.LENGTH_SHORT).show();
                            binding.supplierProgressBar.setVisibility(View.INVISIBLE);
                            binding.supplierLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.GetSupplierSuccess:
                        {
                            List<Supplier> suppliers = Arrays.asList(new Gson().fromJson((String) msg.obj, Supplier[].class));

                            listView = binding.supplierListview;
                            listView.setItemsCanFocus(true);
                            adapter = new SupplierAdapter(getActivity(), suppliers, mHandler);
                            listView.setAdapter(adapter);

                            binding.supplierProgressBar.setVisibility(View.INVISIBLE);
                            binding.supplierLoading.setVisibility(View.INVISIBLE);
                        }
                        break;

                        case HandleCode.DoTopUp:
                        {

                        }
                        break;

                        case HandleCode.TopupSuccess:
                        {
                            Toast.makeText(getContext(), "Top-up successful", Toast.LENGTH_SHORT).show();
                            getSuppliersInfo();
                        }
                        break;

                        case HandleCode.TopupFailed:
                        {
                            Toast.makeText(getContext(), "Top-up failed", Toast.LENGTH_SHORT).show();
                            binding.supplierProgressBar.setVisibility(View.INVISIBLE);
                            binding.supplierLoading.setVisibility(View.INVISIBLE);
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
                            new_progressBar.setVisibility(View.INVISIBLE);
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setTitle("Link Failed");
                            alert.setMessage("\nStudent username/password may be incorrect. \nPlease try again.\n");
                            alert.setPositiveButton("OK", null);
                            alert.show();
                        }
                        break;

                        case HandleCode.LinkRepeated:
                        {
                            Log.e("mytest", "repeated");
                            new_progressBar.setVisibility(View.INVISIBLE);
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setTitle("Link Failed");
                            alert.setMessage("The student has been linked.");
                            alert.setPositiveButton("OK", null);
                            alert.show();
                        }
                        break;

                        case HandleCode.DoUnlink:
                        {
                            doRemoveSupplier((String) msg.obj);
                        }
                        break;

                        case HandleCode.LinkSuccess:
                        {
                            Dialog dialog = (Dialog) msg.obj;
                            dialog.dismiss();
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setTitle("Congratulations");
                            alert.setMessage("Account successfully linked");
                            alert.setPositiveButton("OK", null);
                            alert.show();

                            getSuppliersInfo();
                        }
                        break;

                        case HandleCode.UnlinkSuccess:
                        {
                            Toast.makeText(getContext(), "Unlink Successful", Toast.LENGTH_SHORT).show();
                            getSuppliersInfo();
                        }
                        break;

                        case HandleCode.UnlinkFailed:
                        {
                            Toast.makeText(getContext(), "Unlink Failed", Toast.LENGTH_SHORT).show();
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

        binding.supplierNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSupplierAddDialog();
            }
        });

        getSuppliersInfo();


        return root;
    }

    private void doRemoveSupplier(String supplierId) {
        binding.supplierProgressBar.setVisibility(View.VISIBLE);
        binding.supplierLoading.setVisibility(View.VISIBLE);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("username", GlobalVariables.account.Username);
        hashMap.put("password", GlobalVariables.account.getPassword());
        hashMap.put("supplierid", supplierId);

        HttpUtil.sendHTTPRequest(GlobalVariables.hostname + "/removeSupplier.php", hashMap, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.UnlinkSuccess;
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
                msg.what = HandleCode.UnlinkFailed;
                mHandler.sendMessage(msg);
            }

            @Override
            public void OnBadRequest() {

            }
        });

    }

    private void showSupplierAddDialog() {
        View link_view;

        EditText supplier_name, supplier_description;
        Button supplier_add, supplier_cancel;

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        link_view = inflater.inflate(R.layout.dialog_newsupplier,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add new supplier");
        builder.setCancelable(false);
        builder.setView(link_view);
        Dialog dialog = builder.create();
        dialog.show();

        supplier_name = (EditText) link_view.findViewById(R.id.dialog_name);
        supplier_description = (EditText) link_view.findViewById(R.id.dialog_description);
        supplier_add = (Button) link_view.findViewById(R.id.dialog_add);
        supplier_cancel = (Button) link_view.findViewById(R.id.dialog_cancel);
        new_progressBar = (ProgressBar) link_view.findViewById(R.id.dialog_progressBar);

        View.OnClickListener link_clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.dialog_cancel)
                    dialog.dismiss();

                else if (view.getId() == R.id.dialog_add) {
                    new_progressBar.setVisibility(View.VISIBLE);

                    String url = GlobalVariables.hostname + "/addSupplier.php";
                    Map<String, String> hm = new HashMap<>();
                    hm.put("username", GlobalVariables.account.Username);
                    hm.put("password", GlobalVariables.account.getPassword());
                    hm.put("suppliername", supplier_name.getText().toString());
                    hm.put("supplierdescription", supplier_description.getText().toString());

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

        supplier_add.setOnClickListener(link_clickListener);
        supplier_cancel.setOnClickListener(link_clickListener);
    }

    private void getSuppliersInfo() {
        binding.supplierProgressBar.setVisibility(View.VISIBLE);
        binding.supplierLoading.setVisibility(View.VISIBLE);
        Map<String, String> hm = new HashMap<>();
        hm.put("username", GlobalVariables.account.Username);
        hm.put("password", GlobalVariables.account.getPassword());

        HttpUtil.sendHTTPRequest(GlobalVariables.hostname + "/getSuppliersInfo.php", hm, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                Message msg=mHandler.obtainMessage();
                msg.what = HandleCode.GetSupplierSuccess;
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
                msg.what = HandleCode.GetSupplierFailed;
                mHandler.sendMessage(msg);
            }

            @Override
            public void OnBadRequest() {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}