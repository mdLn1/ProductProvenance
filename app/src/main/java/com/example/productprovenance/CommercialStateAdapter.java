package com.example.productprovenance;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CommercialStateAdapter extends FragmentStateAdapter {

    private SellFragment sellFragment;
    private ResellFragment resellFragment;
    private ReturnFragment returnFragment;

    public CommercialStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                sellFragment = new SellFragment();
                return sellFragment;
            case 1:
                resellFragment = new ResellFragment();
                return resellFragment;
            case 2:
                returnFragment = new ReturnFragment();
                return returnFragment;
            default:
                return new SellFragment();
        }
    }

    public CommercialActions getCurrentFragment(int position) {
        if (position == 0) {
            return sellFragment;
        } else if (position == 1) {
            return  resellFragment;
        } else {
            return returnFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
