package com.example.productprovenance.functional_fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.productprovenance.MainActivity;
import com.example.productprovenance.R;
import com.example.productprovenance.Utils;
import com.google.zxing.WriterException;

public class QRNFCProductFragment extends Fragment implements View.OnClickListener {
    private MainActivity parentActivity;
    private String productName;
    private String productId;
    private String productQR;
    private String productNFC;
    private ImageView qrCodeProductImageView;

    public static QRNFCProductFragment newInstance(String productName, String productId, String productQR, String productNFC) {
        QRNFCProductFragment myFragment = new QRNFCProductFragment();

        Bundle args = new Bundle();
        args.putString("productName", productName);
        args.putString("productId", productId);
        args.putString("productQR", productQR);
        args.putString("productNFC", productNFC);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.print_qr_and_write_nfc_fragment, container, false);
        parentActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            productName = getArguments().getString("productName", "not defined");
        }
        if (getArguments() != null) {
            productId = getArguments().getString("productId", "not defined");
        }
        if (getArguments() != null) {
            productQR = getArguments().getString("productQR", "not defined");
        }
        if (getArguments() != null) {
            productNFC = getArguments().getString("productNFC", "not defined");
        }
        TextView productNameTextView = (TextView) view.findViewById(R.id.productNameTextView);
        if (getArguments() != null) {
            productNameTextView.setText(String.format("Created product name: %s", productName));
        }
        Button viewQRCodeButton = view.findViewById(R.id.viewQRCodeButton);
        Button writeTagNFCButton = view.findViewById(R.id.writeNfcTagButton);
        viewQRCodeButton.setOnClickListener(this);
        writeTagNFCButton.setOnClickListener(this);
        qrCodeProductImageView = view.findViewById(R.id.qrCodeProductImageView);
        return view;
    }

    public String getProductName() {
        return productName;
    }
    public String getProductNFC() {
        return productNFC;
    }

    public String getProductId() {
        return productId;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.viewQRCodeButton) {
            try {
                Bitmap bitmap = Utils.encodeAsBitmap(productQR);
                qrCodeProductImageView.setVisibility(View.VISIBLE);
                qrCodeProductImageView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } else
            parentActivity.onClick(v);
    }
}
