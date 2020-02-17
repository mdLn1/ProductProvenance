package com.example.productprovenance;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CommercialStateAdapter extends FragmentStateAdapter {
    public CommercialStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                SellFragment sellFragment = new SellFragment();
                return sellFragment;
            case 1:
                ResellFragment resellFragment = new ResellFragment();
                return resellFragment;
            case 2:
                ReturnFragment returnFragment = new ReturnFragment();
                return returnFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
