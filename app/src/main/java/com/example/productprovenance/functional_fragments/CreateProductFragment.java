package com.example.productprovenance.functional_fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.productprovenance.MainActivity;
import com.example.productprovenance.R;
import com.google.android.material.textfield.TextInputEditText;

public class CreateProductFragment extends Fragment implements View.OnClickListener {
    private MainActivity parentActivity;
    private TextInputEditText inputTextSellerAccountAddress;
    private TextInputEditText productNameEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_product_fragment, container, false);
        inputTextSellerAccountAddress = view.findViewById(R.id.inputTextSellerAccountAddress);
        productNameEditText = view.findViewById(R.id.inputTextProductName);
        Button buttonAddProduct = view.findViewById(R.id.buttonCreateProduct);
        Button buttonQRCodeScanSellerAccount = view.findViewById(R.id.buttonQRCodeScanSellerAccount);
        buttonQRCodeScanSellerAccount.setOnClickListener(this);
        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               if (productNameEditText.getText().length() < 2 || inputTextSellerAccountAddress.getText().length() < 2) {
                                                   if (productNameEditText.getText().length() < 2) {
                                                       productNameEditText.setError(getString(R.string.error_product_name));
                                                   }
                                                   if (inputTextSellerAccountAddress.getText().length() < 2) {
                                                       inputTextSellerAccountAddress.setError(getString(R.string.error_seller_account_address));
                                                   }
                                               }
                                               else  {
                                                   productNameEditText.setError(null);
//                    ((NavigationHost) getActivity()).navigatePrevious();
                                                   parentActivity.onClick(v);

                                               }
                                           }
                                       }

        );

        productNameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (productNameEditText.getText().length() < 2) {
                    productNameEditText.setError(getString(R.string.error_product_name));
                }else  {
                    productNameEditText.setError(null);
                }
                return false;
            }
        });
        inputTextSellerAccountAddress.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (inputTextSellerAccountAddress.getText().length() < 2) {
                    inputTextSellerAccountAddress.setError(getString(R.string.error_seller_account_address));
                }else  {
                    inputTextSellerAccountAddress.setError(null);
                }
                return false;
            }
        });
        parentActivity = (MainActivity)getActivity();
        return view;
    }

    public void updateSellerAccountAddress(String text) {
        inputTextSellerAccountAddress.setText(text);
        inputTextSellerAccountAddress.clearFocus();
    }

    @Override
    public void onClick(View v) {
        parentActivity.onClick(v);
    }
}
