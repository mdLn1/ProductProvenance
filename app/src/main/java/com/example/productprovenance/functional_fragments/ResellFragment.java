package com.example.productprovenance.functional_fragments;

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

import com.example.productprovenance.CommercialActions;
import com.example.productprovenance.CommercialActivity;
import com.example.productprovenance.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import static com.example.productprovenance.Utils.hideKeyboardFrom;

public class ResellFragment extends Fragment implements View.OnClickListener, CommercialActions {
    private int step = 1;
    private Button buttonQRCodeScan, buttonNFCTagScan, buttonResellProduct;
    private TextInputEditText inputNewBuyerText;
    private TextInputLayout inputNewBuyerLayout;
    private TextInputEditText inputOwnerResellText;
    private TextInputLayout inputOwnerResellLayout;
    private CommercialActivity parentActivity;
    private String qrCode;
    private String nfcCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resell_fragment, container, false);
        buttonQRCodeScan = view.findViewById(R.id.buttonQRCodeScan);
        buttonNFCTagScan = view.findViewById(R.id.buttonNFCTagScan);
        buttonResellProduct = view.findViewById(R.id.buttonResellProduct);

        parentActivity = (CommercialActivity) getActivity();

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

        inputOwnerResellLayout = view.findViewById(R.id.inputOwnerResellLayout);
        inputOwnerResellText = view.findViewById(R.id.inputOwnerResellText);
        inputOwnerResellText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isInputValid(inputOwnerResellText.getText())) {
                    inputOwnerResellLayout.setError(null);
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
                parentActivity.onClick(v);
                break;
            case R.id.buttonNFCTagScan:
                if (step < 2) {
                    Toast.makeText(getActivity(), "You need to do step 1 first", Toast.LENGTH_SHORT).show();
                } else {
                    parentActivity.onClick(v);
                }
                break;
            case R.id.buttonResellProduct:
                if (step < 3) {
                    Toast.makeText(getActivity(), "You need to do step 2 first", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isInputValid(inputOwnerResellText.getText())) {
                        inputOwnerResellText.setError(getString(R.string.error_owner));
                    } else if (!isInputValid(inputNewBuyerText.getText())) {
                        inputNewBuyerLayout.setError(getString(R.string.error_new_buyer));
                    } else {
                        parentActivity.onClick(v);
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
        buttonResellProduct.setEnabled(false);
        buttonResellProduct.setVisibility(View.VISIBLE);
        inputOwnerResellLayout.setVisibility(View.VISIBLE);
        inputNewBuyerLayout.setVisibility(View.VISIBLE);
        buttonNFCTagScan.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
    }

    @Override
    public void onNFCScan(String nfcCode) {
        step += 1;
        this.nfcCode = nfcCode;
        buttonResellProduct.setEnabled(true);
        inputNewBuyerLayout.setEnabled(true);
        inputOwnerResellLayout.setEnabled(true);
        buttonResellProduct.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
    }

    @Override
    public String getNfcCode() {
        return this.nfcCode;
    }

    @Override
    public String getQRCode() {
        return this.qrCode;
    }

    @Override
    public String[] getInputs() {
        return new String[] {Objects.requireNonNull(inputOwnerResellText.getText().toString()), Objects.requireNonNull(inputNewBuyerText.getText().toString())};
    }

    @Override
    public void clearInputs() {
        buttonNFCTagScan.setEnabled(false);
        buttonNFCTagScan.setBackgroundColor(getActivity().getResources().getColor(R.color.colorDisabled, getActivity().getTheme()));

        buttonResellProduct.setEnabled(false);
        buttonResellProduct.setBackgroundColor(getActivity().getResources().getColor(R.color.colorDisabled, getActivity().getTheme()));
        buttonResellProduct.setVisibility(View.INVISIBLE);

        inputOwnerResellText.setText(null);
        inputOwnerResellText.clearFocus();

        inputOwnerResellLayout.clearFocus();
        inputOwnerResellLayout.setEnabled(false);
        inputOwnerResellLayout.setVisibility(View.INVISIBLE);

        inputNewBuyerText.setText(null);
        inputNewBuyerText.clearFocus();

        inputNewBuyerLayout.clearFocus();
        inputNewBuyerLayout.setEnabled(false);
        inputNewBuyerLayout.setVisibility(View.INVISIBLE);

        hideKeyboardFrom(getContext(), getView());
        step = 1;
    }
}
