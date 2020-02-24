package com.example.productprovenance;

import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import static com.example.productprovenance.Utils.hideKeyboardFrom;

public class ResellFragment extends Fragment implements View.OnClickListener {
    private int step = 1;
    private Button buttonQRCodeScan, buttonNFCTagScan, buttonResellProduct;
    private TextInputEditText inputNewBuyerText;
    private TextInputLayout inputNewBuyerLayout;
    private TextInputEditText inputOwnerText;
    private TextInputLayout inputOwnerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resell_fragment, container, false);
        buttonQRCodeScan = view.findViewById(R.id.buttonQRCodeScan);
        buttonNFCTagScan = view.findViewById(R.id.buttonNFCTagScan);
        buttonResellProduct = view.findViewById(R.id.buttonResellProduct);

        inputNewBuyerLayout = view.findViewById(R.id.inputNewBuyer);
        inputNewBuyerText = view.findViewById(R.id.inputNewBuyerText);
        inputNewBuyerText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isInputValid(inputNewBuyerText.getText())) {
                    inputNewBuyerLayout.setError(null);
                }
                return false;
            }
        });

        inputOwnerLayout = view.findViewById(R.id.inputOwner);
        inputOwnerText = view.findViewById(R.id.inputOwnerText);
        inputOwnerText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isInputValid(inputOwnerText.getText())) {
                    inputOwnerLayout.setError(null);
                }
                return false;
            }
        });

        buttonQRCodeScan.setOnClickListener(this);
        buttonNFCTagScan.setOnClickListener(this);
        buttonResellProduct.setOnClickListener(this);
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
                buttonResellProduct.setVisibility(View.VISIBLE);
                inputOwnerLayout.setVisibility(View.VISIBLE);
                inputNewBuyerLayout.setVisibility(View.VISIBLE);
                buttonNFCTagScan.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
                Toast.makeText(getActivity(), "ScanQRCode", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonNFCTagScan:
                if (step < 2) {
                    Toast.makeText(getActivity(), "You need to do step 1 first", Toast.LENGTH_SHORT).show();
                } else {
                    step += 1;
                    buttonResellProduct.setEnabled(true);
                    inputNewBuyerLayout.setEnabled(true);
                    inputOwnerLayout.setEnabled(true);
                    buttonResellProduct.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
                    Toast.makeText(getActivity(), "ScanNFCTag", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonResellProduct:
                if (step < 3) {
                    Toast.makeText(getActivity(), "You need to do step 2 first", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isInputValid(inputOwnerText.getText())) {
                        inputOwnerLayout.setError(getString(R.string.error_password));
                    } else if (!isInputValid(inputNewBuyerText.getText())) {
                        inputNewBuyerLayout.setError(getString(R.string.error_password));
                    } else {
                        Toast.makeText(getActivity(), "SaleCheckout", Toast.LENGTH_SHORT).show();

                        buttonNFCTagScan.setEnabled(false);
                        buttonNFCTagScan.setBackgroundColor(getActivity().getResources().getColor(R.color.colorDisabled, getActivity().getTheme()));

                        buttonResellProduct.setEnabled(false);
                        buttonResellProduct.setVisibility(View.INVISIBLE);
                        buttonResellProduct.setBackgroundColor(getActivity().getResources().getColor(R.color.colorDisabled, getActivity().getTheme()));

                        inputOwnerText.setText(null);
                        inputOwnerText.clearFocus();

                        inputOwnerLayout.clearFocus();
                        inputOwnerLayout.setEnabled(false);
                        inputOwnerLayout.setVisibility(View.INVISIBLE);

                        inputNewBuyerText.setText(null);
                        inputNewBuyerText.clearFocus();

                        inputNewBuyerLayout.clearFocus();
                        inputNewBuyerLayout.setEnabled(false);
                        inputNewBuyerLayout.setVisibility(View.INVISIBLE);

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
