package com.example.productprovenance;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.WriterException;

public class QRCodeGeneratorActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_generator);

        ImageView imageView = (ImageView) findViewById(R.id.qrCodeProductImageView);
        sharedPref = getSharedPreferences(Constants.userDataStore, Context.MODE_PRIVATE);
        if(sharedPref.contains(Constants.accountAddress)) {
            try {
                Bitmap bitmap = Utils.encodeAsBitmap(sharedPref.getString(Constants.accountAddress, "unknown"));
                imageView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

    }

}