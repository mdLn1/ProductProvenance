package com.example.productprovenance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CommercialActivity extends AppCompatActivity implements View.OnClickListener{
    private String[] titles = new String[]{"Sell", "Resell", "Return"};
    private  ViewPager2 viewPager;
    private  CommercialStateAdapter commercialActivityAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commercial);
           viewPager = findViewById(R.id.viewPagerCommercial);
        TabLayout tabLayout = findViewById(R.id.tabLayCommercial);
        commercialActivityAdapter = new CommercialStateAdapter(this);
        viewPager.setAdapter(commercialActivityAdapter);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles[position]);
            }
        }).attach();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == Constants.NFC_SCAN_ACTION) {
                String nfcData = data.getStringExtra(Constants.GET_SCANNED_NFC_DATA);
                commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).onNFCScan(nfcData);
            } else if (requestCode == Constants.QR_SCAN_ACTION) {
                String qrData = data.getStringExtra(Constants.GET_SCANNED_QR_DATA);
                commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).onQRScan(qrData);
            }
        } else if(resultCode == RESULT_CANCELED) {
            if(requestCode == Constants.NFC_SCAN_ACTION) {
                Toast.makeText(this, "You need to scan an nfc tag to proceed further", Toast.LENGTH_LONG).show();
            } else if (requestCode == Constants.QR_SCAN_ACTION) {
                Toast.makeText(this, "You need to scan a QR code to proceed further", Toast.LENGTH_LONG).show();

            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonQRCodeScan:
                startActivityForResult(new Intent(this, SimpleTestActivity.class),Constants.QR_SCAN_ACTION);
                break;
            case R.id.buttonNFCTagScan:
                startActivityForResult(new Intent(this, NFCScanActivity.class), Constants.NFC_SCAN_ACTION);
                break;
            default:
                Toast.makeText(this, "blafsasf", Toast.LENGTH_SHORT).show();
        }
    }
}
