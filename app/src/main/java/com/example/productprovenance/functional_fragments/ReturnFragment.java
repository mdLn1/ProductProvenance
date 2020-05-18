package com.example.productprovenance.functional_fragments;

import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

public class ReturnFragment extends Fragment implements OnClickListener, CommercialActions {

    private int step = 1;
    private Button buttonQRCodeScan, buttonNFCTagScan, buttonReturnProduct;
    private TextInputEditText inputOwnerReturnText;
    private TextInputLayout inputOwnerLayout;
    private CommercialActivity parentActivity;
    private String qrCode;
    private String nfcCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.return_fragment, container, false);
        buttonQRCodeScan = view.findViewById(R.id.buttonQRCodeScan);
        buttonNFCTagScan = view.findViewById(R.id.buttonNFCTagScan);
        buttonReturnProduct  = view.findViewById(R.id.buttonReturnProduct);

        parentActivity = (CommercialActivity) getActivity();

        inputOwnerLayout = view.findViewById(R.id.inputOwnerReturnLayout);
        inputOwnerReturnText = view.findViewById(R.id.inputOwnerReturnText);
        inputOwnerReturnText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isInputValid(inputOwnerReturnText.getText())) {
                    inputOwnerLayout.setError(null);
                }
                return false;
            }
        });

        buttonQRCodeScan.setOnClickListener(this);
        buttonNFCTagScan.setOnClickListener(this);
        buttonReturnProduct.setOnClickListener(this);
        return view;
    }
    public boolean isInputValid(@Nullable Editable text) {
        return text != null && text.length() >= 8;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonQRCodeScan:
                parentActivity.onClick(v);
                break;
            case R.id.buttonNFCTagScan:
                if(step < 2) {
                    Toast.makeText(getActivity(), "You need to do step 1 first", Toast.LENGTH_SHORT).show();
                } else {
                    parentActivity.onClick(v);
                }
                break;
            case R.id.buttonReturnProduct:
                if(step < 3) {
                    Toast.makeText(getActivity(), "You need to do step 2 first", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isInputValid(inputOwnerReturnText.getText())) {
                        inputOwnerLayout.setError(getString(R.string.error_owner));
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
        buttonReturnProduct.setEnabled(false);
        buttonReturnProduct.setVisibility(View.VISIBLE);
        inputOwnerLayout.setVisibility(View.VISIBLE);
        buttonNFCTagScan.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
    }

    @Override
    public void onNFCScan(String nfcCode) {
        step += 1;
        this.nfcCode = nfcCode;
        buttonReturnProduct.setEnabled(true);
        inputOwnerLayout.setEnabled(true);
        buttonReturnProduct.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
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
        return new String[] {Objects.requireNonNull(inputOwnerReturnText.getText().toString())};
    }

    @Override
    public void clearInputs() {
        buttonNFCTagScan.setEnabled(false);
        buttonNFCTagScan.setBackgroundColor(getActivity().getResources().getColor(R.color.colorDisabled, getActivity().getTheme()));

        buttonReturnProduct.setEnabled(false);
        buttonReturnProduct.setVisibility(View.INVISIBLE);
        buttonReturnProduct.setBackgroundColor(getActivity().getResources().getColor(R.color.colorDisabled, getActivity().getTheme()));

        inputOwnerReturnText.clearFocus();

        inputOwnerLayout.clearFocus();
        inputOwnerLayout.setEnabled(false);
        inputOwnerLayout.setVisibility(View.INVISIBLE);
        hideKeyboardFrom(getContext(), getView());
        step = 1;
    }
}
