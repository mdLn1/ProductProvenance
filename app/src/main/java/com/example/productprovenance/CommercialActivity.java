package com.example.productprovenance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CommercialActivity extends AppCompatActivity {
    private String[] titles = new String[]{"Sell", "Resell", "Return"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commercial);
            ViewPager2 viewPager = findViewById(R.id.viewPagerCommercial);
        TabLayout tabLayout = findViewById(R.id.tabLayCommercial);
        CommercialStateAdapter commercialActivity = new CommercialStateAdapter(this);

        viewPager.setAdapter(commercialActivity);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles[position]);
            }
        }).attach();
    }
}
