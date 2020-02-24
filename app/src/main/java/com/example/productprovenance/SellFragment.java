package com.example.productprovenance;

import android.app.Activity;
import android.content.Context;
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

public class SellFragment extends Fragment implements OnClickListener {

    private int step = 1;
    private Button buttonQRCodeScan, buttonNFCTagScan, buttonSaleCheckout;
    private TextInputEditText inputBuyerText;
    private TextInputLayout inputBuyerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.sell_fragment, container, false);
        buttonQRCodeScan = view.findViewById(R.id.buttonQRCodeScan);
        buttonNFCTagScan = view.findViewById(R.id.buttonNFCTagScan);
        buttonSaleCheckout = view.findViewById(R.id.buttonSaleCheckout);

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
                step += 1;
                buttonNFCTagScan.setEnabled(true);
                buttonSaleCheckout.setVisibility(View.VISIBLE);
                inputBuyerLayout.setVisibility(View.VISIBLE);
                buttonNFCTagScan.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
                Toast.makeText(getActivity(), "ScanQRCode", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonNFCTagScan:
                if (step < 2) {
                    Toast.makeText(getActivity(), "You need to do step 1 first", Toast.LENGTH_SHORT).show();
                } else {
                    step += 1;
                    buttonSaleCheckout.setEnabled(true);
                    inputBuyerLayout.setEnabled(true);
                    buttonSaleCheckout.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
                    Toast.makeText(getActivity(), "ScanNFCTag", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonSaleCheckout:
                if (step < 3) {
                    Toast.makeText(getActivity(), "You need to do step 2 first", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isInputValid(inputBuyerText.getText())) {
                        inputBuyerLayout.setError(getString(R.string.error_password));
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


}
