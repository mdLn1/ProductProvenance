package com.example.productprovenance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class TransferProductFragment extends Fragment implements View.OnClickListener {
    private MainActivity parentActivity;
    private TextInputEditText inputTextProductAddress;
    private TextInputEditText inputTextTransferAccountAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfer_product_fragment, container, false);
        inputTextProductAddress = view.findViewById(R.id.inputTextProductAddress);
        inputTextTransferAccountAddress = view.findViewById(R.id.inputTextTransferAccountAddress);
        parentActivity = (MainActivity)getActivity();

        Button buttonQRCodeScanProduct = view.findViewById(R.id.buttonQRCodeScanProduct);
        Button buttonQRCodeScanTransferAccount = view.findViewById(R.id.buttonQRCodeScanTransferAccount);

        buttonQRCodeScanProduct.setOnClickListener(this);
        buttonQRCodeScanTransferAccount.setOnClickListener(this);

        return view;
    }

    public void updateTransferProductText(String text){
        inputTextProductAddress.setText(text);
        inputTextProductAddress.clearFocus();
    }

    public void updateTransferAccountText(String text){
        inputTextTransferAccountAddress.setText(text);
        inputTextTransferAccountAddress.clearFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                parentActivity.onClick(v);
        }

    }
}
