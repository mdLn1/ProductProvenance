package com.example.productprovenance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ProductDetailsFragment extends Fragment {
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_details_fragment, container, false);
        TextView tv = view.findViewById(R.id.textView);
        try{
            String QRData = getArguments().getString(Constants.GET_SCANNED_QR_DATA);
            tv.setText(QRData);
        } catch (NullPointerException ne) {
            tv.setText("Nothing found");
        }
        return view;

    }
}
