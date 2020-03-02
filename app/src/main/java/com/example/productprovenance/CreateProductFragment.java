package com.example.productprovenance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class CreateProductFragment extends Fragment implements View.OnClickListener {
    private MainActivity parentActivity;
    private TextInputEditText inputTextSellerAccountAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_product_fragment, container, false);
        inputTextSellerAccountAddress = view.findViewById(R.id.inputTextSellerAccountAddress);

        Button buttonAddProduct = view.findViewById(R.id.buttonAddProduct);
        Button buttonQRCodeScanSellerAccount = view.findViewById(R.id.buttonQRCodeScanSellerAccount);
        buttonAddProduct.setOnClickListener(this);
        buttonQRCodeScanSellerAccount.setOnClickListener(this);
        parentActivity = (MainActivity)getActivity();
        return view;
    }

    public void updateSellerAccountAddres(String text) {
        inputTextSellerAccountAddress.setText(text);
        inputTextSellerAccountAddress.clearFocus();
    }

    @Override
    public void onClick(View v) {
        parentActivity.onClick(v);
    }
}
