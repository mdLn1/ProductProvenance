package com.example.productprovenance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import static com.example.productprovenance.Utils.hideKeyboardFrom;

public class SellFragment extends Fragment implements OnClickListener, CommercialActions {

    private int step = 1;
    private Button buttonQRCodeScan, buttonNFCTagScan, buttonSaleCheckout;
    private TextInputEditText inputBuyerText;
    private TextInputLayout inputBuyerLayout;
    private CommercialActivity parentActivity;
    private String qrCode;
    private String nfcCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.sell_fragment, container, false);
        buttonQRCodeScan = view.findViewById(R.id.buttonQRCodeScan);
        buttonNFCTagScan = view.findViewById(R.id.buttonNFCTagScan);
        buttonSaleCheckout = view.findViewById(R.id.buttonSaleCheckout);

        parentActivity = (CommercialActivity)getActivity();

        inputBuyerLayout = view.findViewById(R.id.inputBuyer);
        inputBuyerText = view.findViewById(R.id.inputBuyerText);
        inputBuyerText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isInputValid(inputBuyerText.getText())) {
                    inputBuyerLayout.setError(null);
                }
                return false;
            }
        });

        buttonQRCodeScan.setOnClickListener(this);
        buttonNFCTagScan.setOnClickListener(this);
        buttonSaleCheckout.setOnClickListener(this);
        return view;
    }

    public boolean isInputValid(@Nullable Editable text) {
        return text != null && text.length() >= 8;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonQRCodeScan:
                parentActivity.onClick(v);
                break;
            case R.id.buttonNFCTagScan:
                if (step < 2) {
                    Toast.makeText(getActivity(), "You need to do step 1 first", Toast.LENGTH_SHORT).show();
                } else {
                    parentActivity.onClick(v);
                }
                break;
            case R.id.buttonSaleCheckout:
                if (step < 3) {
                    Toast.makeText(getActivity(), "You need to do step 2 first", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isInputValid(inputBuyerText.getText())) {
                        inputBuyerLayout.setError(getString(R.string.error_buyer));
                    } else {
                        inputBuyerLayout.setError(null);
                        Toast.makeText(getActivity(), "SaleCheckout", Toast.LENGTH_SHORT).show();
                        buttonNFCTagScan.setEnabled(false);
                        buttonNFCTagScan.setBackgroundColor(getActivity().getResources().getColor(R.color.colorDisabled, getActivity().getTheme()));

                        buttonSaleCheckout.setEnabled(false);
                        buttonSaleCheckout.setBackgroundColor(getActivity().getResources().getColor(R.color.colorDisabled, getActivity().getTheme()));
                        buttonSaleCheckout.setVisibility(View.INVISIBLE);

                        inputBuyerText.setText(null);
                        inputBuyerText.clearFocus();

                        inputBuyerLayout.clearFocus();
                        inputBuyerLayout.setEnabled(false);
                        inputBuyerLayout.setVisibility(View.INVISIBLE);

                        hideKeyboardFrom(getContext(), getView());
                        step = 1;
                    }
                }
                break;
            default:
                Toast.makeText(getActivity(), "Just a click", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onQRScan(String qrCode) {
        step += 1;
        this.qrCode = qrCode;
        buttonNFCTagScan.setEnabled(true);
        buttonSaleCheckout.setEnabled(false);
        buttonSaleCheckout.setVisibility(View.VISIBLE);
        inputBuyerLayout.setVisibility(View.VISIBLE);
        buttonNFCTagScan.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
        Toast.makeText(getActivity(), "ScanQRCode", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNFCScan(String nfcCode) {
        step += 1;
        this.nfcCode = nfcCode;
        buttonSaleCheckout.setEnabled(true);
        inputBuyerLayout.setEnabled(true);
        buttonSaleCheckout.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
        Toast.makeText(getActivity(), "ScanNFCTag", Toast.LENGTH_SHORT).show();
    }
}
